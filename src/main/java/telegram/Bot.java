package telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import kotlin.Pair;
import repository.dao.ChatDao;
import repository.dao.GroupDao;
import repository.dao.MemberDao;
import telegram.Events.HandlerCommands;
import telegram.Events.HandlerEventsInGroups;
import telegram.Events.HandlerEventsModifyRightsBot;
import telegram.domain.Access;
import telegram.domain.MemberData;
import telegram.domain.State;
import util.ConnectionPool.ConnectionPool;

import java.util.*;

public class Bot {
    private final TelegramBot bot = new TelegramBot("");
    private final ConnectionPool connector = new ConnectionPool();
    private final Map<Long, MemberData> controllerStates = new HashMap<>();
    private final State stateDefault = Initiator.initializeStateMachine();
    private final ArrayList<BaseResponse> lastResponses = new ArrayList<>();

    public void start() {
        //Initiator.reset(connector);
        System.out.println("Start");
        new Thread(() ->{
            while(true){
                GroupDao.CheckWarning(connector);
                 System.out.println("Я проснулся");
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        ).start();

        bot.setUpdatesListener(updates -> {
            updates.forEach(x -> new Thread(() -> {
                process(x);
            }).start());
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        try {
            System.out.println("Update");
            List<BaseRequest> requests = new ArrayList<>();
            InformantUpdate informant = definitionIdAndTypeUpdate(update);

            {
                //Тут добавить метод который соберет в себе все левые методы
                //requests.addAll(null);
                //Обработка событий не касающихся машины состояний
                switch (informant.type) {
                    case MyChatMember -> requests.add(HandlerEventsModifyRightsBot.process(update.myChatMember(), connector));
                    case Message -> {
                        requests.add(HandlerEventsInGroups.process(update.message(), connector));
                        requests.add(HandlerCommands.process(update.message(), connector));
                        if (update.message().leftChatMember() != null && Objects.equals(update.message().leftChatMember().username(), "AdviserAssistantBot")) {

                            System.out.println("Удаляю чат");
                            new ChatDao().deleteChat(new Chat().id(), connector);
                        }
                    }
                }
                for (BaseRequest x : requests) {
                    if (x != null)
                        bot.execute(x);
                }
                requests.clear();
            }

            //Проверка, что сообщение пишется боту
            if (Objects.equals(informant.idMember, informant.idChat)) {
                //Обработка Состояний
                MemberData vault = controllerStates.get(informant.idMember);
                State vaultState = null;
                if (vault != null) {
                    vaultState = vault.getState();
                }

                if (informant.type == TypeUpdate.Message) {
                    Message message = update.message();
                    if (message.text() != null && message.text().startsWith("/default")) {
                        User user = message.from();
                        new MemberDao()
                                .addMember(
                                        user.id(),
                                        user.firstName(),
                                        user.lastName(),
                                        user.username(),
                                        connector);
                        controllerStates.put(informant.idMember, new MemberData(stateDefault));
                        requests.add(new DeleteMessage(informant.idChat, informant.idMessage));
                        if (vault != null && vault.getIdMessage() != null)
                            requests.add(new DeleteMessage(informant.idChat, vault.getIdMessage()));
                    } else if (message.text() != null && message.text().startsWith("/exit")) {
                        controllerStates.remove(informant.idMember);
                        requests.add(new DeleteMessage(informant.idChat, informant.idMessage));
                        if (vault != null)
                            requests.add(new DeleteMessage(informant.idChat, vault.getIdMessage()));
                    } else if (message.text() != null && Objects.equals(message.text(), "/reboot") && message.chat().id() == 1182966178) {
                        controllerStates.forEach((x, z) -> requests.add(new SendMessage(x, "Перезагрузка бота")));
                        controllerStates.clear();
                        Initiator.reset(connector);
                    } else if (vaultState != null && vaultState.getOperatorWhoProcessesMessages() != null) {
                        requests.addAll(vaultState.getOperatorWhoProcessesMessages()
                                .apply(new State.Data(informant.idMember, vault, update, connector, bot)));
                        vault.setIsStateWhichSendsMessages(true);

                        if (vaultState.getStateNext() != null) {
                            vault.setState(vaultState.getStateNext());
                        }

                        //Переход обратно, если конечный этап.
                        if (vaultState.getStateNext() == null && vaultState.getPaths().isEmpty() || vault.getIsReturn()) {
                            vault.setState(vaultState.previous());
                            if (vault.getIsReturn()) {
                                vault.setIsReturn(false);
                            }
                        }

                    }
                } else if (informant.type == TypeUpdate.CallbackQuery) {
                    Integer idMessage = update.callbackQuery().message().messageId();
                    String text = update.callbackQuery().data();
                    if ((vault == null) || (!Objects.equals(vault.getIdMessage(), idMessage)))
                        requests.add(new DeleteMessage(informant.idChat, update.callbackQuery().message().messageId()));
                    else {
                        if (Objects.equals(text, "Назад")) {
                            if (vaultState.getOperatorWhichGeneratesKeyboard() != null) {
                                vault.setPreviousStateGenerateKeyboard(null);
                                vault.setNumberSublist(0);
                            }
                            vault.setState(vaultState.previous());
                        } else if (Objects.equals(text, "⬅⬅⬅")) {
                            vault.setNumberSublist(vault.getNumberSublist() - 1);
                        } else if (Objects.equals(text, "➡➡➡")) {
                            vault.setNumberSublist(vault.getNumberSublist() + 1);
                        } else if (Objects.equals(text, "Int/Int")) {
                            requests.add(new AnswerCallbackQuery(update.callbackQuery().id())
                                    .text("")
                                    .showAlert(false));
                        } else {
                            State stateNext = null;



                            //Сохранение данных с кнопки, которую только что нажали
                            if (vaultState.getOperatorWhichGeneratesKeyboard() != null) {
                                vault.addInfo(vaultState.getType(), text);
                                stateNext = vaultState.getStateNext();
                            } else {
                                stateNext = vaultState.getPaths()
                                        .stream()
                                        .filter(element -> Objects.equals(element.getName(), text))
                                        .findFirst().orElseThrow();
                            }
                            vault.setState(stateNext);
                            vaultState = vault.getState();
                            if (vaultState.getOperatorWhichRunsAtStartup() != null) {
                                //Специальный метод - запускаемый при запуске - конкретную реализацию необходимо создавать отдельно
                                requests.addAll(vaultState.getOperatorWhichRunsAtStartup()
                                        .apply(new State.Data
                                                (informant.idMember, vault, update, connector, bot)));
                                vault.setIsStateWhichSendsMessages(true);

                                //Переход обратно, если конечный этап.
                                if (vaultState.getStateNext() == null && vaultState.getPaths().isEmpty() || vault.getIsReturn()) {
                                    vault.setState(vaultState.previous());
                                    if (vault.getIsReturn()) {
                                        vault.setIsReturn(false);
                                    }
                                }

                            }
                        }
                    }
                }

                for (BaseRequest x : requests) {
                    if (x != null)
                        bot.execute(x);
                }
                requests.clear();

                //Генерация Сообщений С клавиатурами
                vault = controllerStates.get(informant.idMember);
                if (vault != null && Objects.equals(informant.idMember, informant.idChat)) {
                    vaultState = vault.getState();

                    //Создание описания специальным методом
                    String description = vaultState.getDescription();

                    if (vaultState.getOperatorWhoGeneratesDescription() != null)
                        description = vaultState.getOperatorWhoGeneratesDescription().apply(
                                new State.Data(informant.idMember, vault, update, connector, bot)
                        );
                    description = "Состояние: *" + vaultState.getName() + "*\n" + description;

                    if (vault.getIdGroup() != null) {
                        vault.setRole(new GroupDao().getMembersRole(informant.idMember, vault.getIdGroup(), connector));
                    }

                    //Генерация клавиатуры
                    InlineKeyboardMarkup inlineKeyboardMarkup = GenerationKeyboard(informant.idMember, vault, update, connector);

                    if (vault.getIdMessage() == null) {
                        /*new Thread(() -> {
                            for (int i = 1; i < 200; i++) {
                                bot.execute(new DeleteMessage(informant.idMember, informant.idMessage - i));
                            }
                        }
                        ).start();*/
                        SendResponse response = bot.execute(new SendMessage(informant.idChat, description)
                                .replyMarkup(inlineKeyboardMarkup).parseMode(ParseMode.MarkdownV2));
                        vault.setIdMessage(response.message().messageId());
                    } else if (vault.getIsStateWhichSendsMessages()) {
                        bot.execute(new DeleteMessage(informant.idChat, vault.getIdMessage()));
                        SendResponse response = bot.execute(new SendMessage(informant.idChat, description)
                                .replyMarkup(inlineKeyboardMarkup).parseMode(ParseMode.MarkdownV2));
                        vault.setIdMessage(response.message().messageId());
                        vault.setIsStateWhichSendsMessages(false);
                    } else {
                        BaseResponse response = bot.execute(new EditMessageText(informant.idChat, vault.getIdMessage(), description)
                                .replyMarkup(inlineKeyboardMarkup).parseMode(ParseMode.MarkdownV2));
                    }
                }
               /*
                for (BaseRequest x : requests) {
                    if (x != null) {
                        lastResponses.clear(); //Это для тестирования можно удалить если что
                        //long start = System.currentTimeMillis();
                        BaseResponse response = bot.execute(x);
                        long finish = System.currentTimeMillis();
                        long elapsed = finish - start;
                        System.out.println("Прошло времени, мс: " + elapsed + " " + x.getMethod());
                        if (!response.isOk()) {
                            lastResponses.add(response);
                            if (update.callbackQuery() != null && response.description().startsWith("Bad Request: message is not modified")) {
                                bot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text(""));
                            } else if (response.description().startsWith("Bad Request: message is not modified")) {
                                bot.execute(new SendMessage(informant.idChat, ""));
                            } else
                                bot.execute(new SendMessage(informant.idChat,
                                        "ErrorCode: " + response.errorCode() + "\n" +
                                                "Description: " + response.description()));
                        }
                    }
                }*/

            }

        } catch (RuntimeException e) {
            if (update.callbackQuery() != null) {
                bot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ошибка обработки нажатия кнопки"));
                controllerStates.get(update.callbackQuery().from().id()).previous();
            } else {
                bot.execute(new SendMessage(update.message().chat().id(), "Ошибка обработки текста"));
                controllerStates.get(update.message().from().id()).previous();
            }
        } catch (Exception e) {
            System.out.println("Ошибка чего то сломалось! как ты смог!");
        }
    }

    private InformantUpdate definitionIdAndTypeUpdate(Update update) {

        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        ChatMemberUpdated myChatMember = update.myChatMember();

        boolean isMessage = message != null;
        boolean isCallbackQuery = callbackQuery != null;
        boolean isMyChatMember = myChatMember != null;

        InformantUpdate informant = new InformantUpdate();

        if (isMyChatMember) {
            informant.idMember = myChatMember.from().id();
            informant.idChat = myChatMember.chat().id();
            informant.type = TypeUpdate.MyChatMember;
        } else if (isCallbackQuery) {
            informant.idMember = callbackQuery.from().id();
            informant.idChat = callbackQuery.message().chat().id();
            informant.idMessage = callbackQuery.message().messageId();
            informant.type = TypeUpdate.CallbackQuery;
        } else if (isMessage) {
            informant.idMember = message.from().id();
            informant.idChat = message.chat().id();
            informant.idMessage = message.messageId();
            informant.type = TypeUpdate.Message;
        } else {
            informant.idMember = null;
            informant.idChat = null;
            informant.idMessage = null;
            informant.type = TypeUpdate.Others;
        }
        return informant;
    }

    private InlineKeyboardMarkup GenerationKeyboard(Long idMember, MemberData data, Update update, ConnectionPool connector) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (data.getState().getOperatorWhichGeneratesKeyboard() != null) {
            //GenerationButton
            if (data.getPreviousStateGenerateKeyboard() != data.getState())
                data.setNumberSublist(0);
            data.setPreviousStateGenerateKeyboard(data.getState());
            List<Pair<String, String>> ListResult = data.getState().getOperatorWhichGeneratesKeyboard()
                    .apply(new State.Data(idMember, data, update, connector, bot));

            //Подсписок из результата 8 элементов
            //Так же тут определяется наличие кнопок
            //для перехода влево и вправо
            // - для перемещения по списку.

            int divider = 8;

            List<Pair<String, String>> ListButtons = null;
            int size = ListResult.size();

            int countSubLists = size / divider;
            if (size % divider > 0)
                countSubLists = countSubLists + 1;

            Integer numberSubList = data.getNumberSublist();
            if (numberSubList == -1) {
                numberSubList = countSubLists - 1;
                data.setNumberSublist(numberSubList);
            } else if (countSubLists - 1 < numberSubList) {
                numberSubList = numberSubList - countSubLists;
                data.setNumberSublist(numberSubList);
            }

            if (size <= divider) {
                ListButtons = ListResult;
                data.setNumberSublist(0);
            } else if (numberSubList != countSubLists - 1) {
                ListButtons = ListResult.subList(numberSubList * divider, (numberSubList + 1) * divider);
            } else {
                ListButtons = ListResult.subList(numberSubList * divider, size);
            }


            for (Pair<String, String> x : ListButtons) {
                inlineKeyboardMarkup.addRow(new InlineKeyboardButton(x.component2())
                        .callbackData(x.component1()));
            }
            if (countSubLists > 1)
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton("⬅").callbackData("⬅⬅⬅"),
                        new InlineKeyboardButton(numberSubList + 1 + "/" + countSubLists).callbackData("Int/Int"),
                        new InlineKeyboardButton("➡").callbackData("➡➡➡")
                );

        } else {
            for (State element : data.getState().getPaths()) {
                if (Access.checkAccess(data.getRole(), element.getLevelAccess()))
                    inlineKeyboardMarkup.addRow(new InlineKeyboardButton(element.getNameButton()).callbackData(element.getName()));
            }
        }
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Назад").callbackData("Назад"));
        return inlineKeyboardMarkup;
    }

    private enum TypeUpdate {
        Message,
        CallbackQuery,
        MyChatMember,
        Others
    }

    private static class InformantUpdate {
        Long idMember;
        Long idChat;
        Integer idMessage;
        TypeUpdate type;
    }
}
