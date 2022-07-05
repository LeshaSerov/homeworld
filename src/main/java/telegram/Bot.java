package telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import kotlin.Pair;
import telegram.Events.HandlerCommands;
import telegram.Events.HandlerEventsInGroups;
import telegram.Events.HandlerEventsModifyRightsBot;
import telegram.domain.MemberData;
import telegram.domain.State;
import util.ConnectionPool.ConnectionPool;

import java.util.*;

public class Bot {
    private final TelegramBot bot = new TelegramBot("5417780715:AAFRy0huha6DSO0VNWVJF7ThjN6kpzZ6iWk");
    private final ConnectionPool connector = new ConnectionPool();
    private final Map<Long, MemberData> controllerStates = new HashMap<Long, MemberData>();
    private final State stateDefault = Initiator.initializeDefaultState();

    public void start() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        try {
            List<BaseRequest> requests = new ArrayList<>();
            InformantUpdate informant = definitionIdAndTypeUpdate(update);

            switch (informant.type) {
                case MyChatMember -> requests.add(HandlerEventsModifyRightsBot.process(update.myChatMember(), connector));
                case Message -> {
                    requests.add(HandlerEventsInGroups.process(update.message(), connector));
                    requests.add(HandlerCommands.process(update.message(), connector));
                }
            }

            //Обработка Состояний
            if (Objects.equals(informant.idMember, informant.idChat)) {

                MemberData vault = controllerStates.get(informant.idMember);
                State vaultState = vault.getState();

                if (informant.type == TypeUpdate.Message) {
                    Message message = update.message();

                    if (message.text().startsWith("/default")) {
                        controllerStates.put(informant.idMember, new MemberData(stateDefault));
                    } else if (message.text().startsWith("/exit")) {
                        controllerStates.remove(informant.idMember);

                    } else if (vaultState.getOperatorWhoProcessesMessages() != null) {
                        requests.addAll(vaultState.getOperatorWhoProcessesMessages()
                                .apply(new State.Data(informant.idMember, vault, update, connector)));
                    }

                } else if (informant.type == TypeUpdate.CallbackQuery) {

                    Integer idMessage = update.callbackQuery().message().messageId();
                    String text = update.callbackQuery().data();

                    if (!Objects.equals(vault.getIdMessage(), idMessage))
                        requests.add(new DeleteMessage(informant.idChat, update.callbackQuery().message().messageId()));
                        //Переход в следующее состояние
                    else {
                        if (Objects.equals(text, "Назад")) {
                            vault.setState(vaultState.previous());
                        } else {

                            //Сохранение данных с кнопки, которую только что нажали
                            if (vaultState.getOperatorWhichGeneratesKeyboard() != null) {
                                vault.addInfo(vaultState.getType(), text);
                            }

                            State stateNext = vaultState.getPaths()
                                    .stream()
                                    .filter(element -> Objects.equals(element.getName(), text))
                                    .findFirst().orElseThrow();
                            vault.setState(stateNext);
                            vaultState = vault.getState();

                            if (vaultState.getOperatorWhichRunsAtStartup() != null) {
                                //Специальный метод - запускаемый при запуске - конкретную реализацию необходимо создавать отдельно
                                requests.addAll(vaultState.getOperatorWhichRunsAtStartup()
                                        .apply(new State.Data(informant.idMember, vault, update, connector)));
                                //Переход обратно, если конечный этап.
                                if (vaultState.getStateNext() == null && vaultState.getPaths().isEmpty())
                                    vault.setState(vaultState.previous());
                            }
                        }
                    }
                }
            }

            //Генерация Сообщений С клавиатурами
            MemberData vault = controllerStates.get(informant.idMember);
            if (vault != null) {
                State vaultState = vault.getState();
                //Создание клавиатуры специальным методом
                InlineKeyboardMarkup inlineKeyboardMarkup = GenerationKeyboard(informant.idMember,vault,update,connector);
                if (vault.getIdMessage() == null)
                    requests.add(new SendMessage(informant.idChat, vaultState.getDescription()).replyMarkup(inlineKeyboardMarkup));
                else {
                    requests.add(new EditMessageText(informant.idChat, update.callbackQuery().message().messageId(), vaultState.getDescription()).replyMarkup(inlineKeyboardMarkup));
                }
            }

            for (BaseRequest x : requests) {
                if (x != null)
                    bot.execute(x);
            }
        } catch (RuntimeException e) {
            bot.execute(new AnswerCallbackQuery(update.callbackQuery().id()).text("Ошибка"));
        } catch (Exception e) {
            System.out.println("Ошибка");
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
            informant.type = TypeUpdate.CallbackQuery;
        } else if (isMessage) {
            informant.idMember = message.from().id();
            informant.idChat = message.chat().id();
            informant.type = TypeUpdate.Message;
        }
        return informant;
    }

    private InlineKeyboardMarkup GenerationKeyboard(Long idMember, MemberData data, Update update, ConnectionPool connector) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        if (data.getState().getOperatorWhichGeneratesKeyboard() != null) {
            //GenerationButton
            List<Pair<String, String>> ListButtons = data.getState().getOperatorWhichGeneratesKeyboard()
                    .apply(new State.Data(idMember, data, update, connector));
            for (Pair<String, String> x : ListButtons) {
                inlineKeyboardMarkup.addRow(
                        new InlineKeyboardButton(
                                x.component2()
                        ).callbackData(x.component1()));
            }
        } else {
            for (State element :
                    data.getState().getPaths()) {
                inlineKeyboardMarkup.addRow(new InlineKeyboardButton(element.getNameButton()).callbackData(element.getName()));
            }
        }
        inlineKeyboardMarkup.addRow(new InlineKeyboardButton("Назад").callbackData("Назад"));
        return inlineKeyboardMarkup;
    }

    private enum TypeUpdate {
        Message,
        CallbackQuery,
        MyChatMember
    }

    private static class InformantUpdate {
        Long idMember;
        Long idChat;
        TypeUpdate type;
    }
}
