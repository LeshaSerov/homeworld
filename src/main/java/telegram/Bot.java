package telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import telegram.domain.MemberVault;
import telegram.domain.State;
import telegram.handlers.HandlerEvent;
import telegram.handlers.HandlerGroupMessages;
import telegram.handlers.mechanicsStates.HandlerState;
import util.ConnectionPool.ConnectionPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot {
    private final TelegramBot bot = new TelegramBot("5417780715:AAFRy0huha6DSO0VNWVJF7ThjN6kpzZ6iWk");
    private final ConnectionPool connector = new ConnectionPool();
    private final Map<Long, MemberVault> controllerStates = new HashMap<Long, MemberVault>();
    private final State stateDefault = new Initiator().initializeDefaultState();

    public void start() {
        new Initiator().initializeDatabase(connector);
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        try {
            System.out.println("Received Update");

            List<BaseRequest> request = new ArrayList<>();

            Boolean inHandlers = false;

            Message message = update.message();
            CallbackQuery callbackQuery = update.callbackQuery();
            ChatMemberUpdated myChatMember = update.myChatMember();

            Long id_member = null;

            if (message != null) {
                id_member = message.from().id();

                if (!new HandlerState().checkBotChat(message)) {
                    request.add(new HandlerGroupMessages().processing(message, connector));
                    id_member = null;
                }
                else if (message.text().startsWith("/default")) {
                    controllerStates.put(id_member, new MemberVault(stateDefault));
                    request.add(new DeleteMessage(message.chat().id(), message.messageId()));
                }
                else if (message.text().startsWith("/exit")) {
                    controllerStates.remove(id_member);
                    request.add(new DeleteMessage(message.chat().id(), message.messageId()));
                }
            }
            else if (callbackQuery != null) {
                id_member = callbackQuery.from().id();
            }
            else if (myChatMember != null) {
                request.add(new HandlerEvent().processing(myChatMember, connector));
            }

            if (controllerStates.containsKey(id_member)) {
                request.addAll(new HandlerState().processing(controllerStates.get(id_member), message, callbackQuery));
            }
            else if(callbackQuery != null)
                request.add(new DeleteMessage(callbackQuery.message().chat().id(), callbackQuery.message().messageId()));


            for (BaseRequest e : request
            ) {
                if (e != null)
                    bot.execute(e);
            }

        } catch (NullPointerException ignored) {
        }
    }


//        Проверка Типа Апдейта
//        Проверка в чате или в личке с ботом
//        Обработка Апдейтов - состояние пользователя
//        1 есть handlerActivator
//


//        try {
//            connector.CreateConnect();
//            controllerStates.put(1, new MemberVault(stateDefault));
//        }catch (Exception ignored){
//
//        }
//    }
}

//
//        BaseRequest request = null;
//
//        if (update.callbackQuery() != null) {
//            request = getAnswerRequest(update);
//        } else if (update.message() != null) {
//            request = getRequest(update);
//        }
//
//        if (request != null) {
//            bot.execute(request);
//        }
//    }
//
//
//    private BaseRequest getAnswerRequest(Update update) {
//        CallbackQuery callbackQuery = update.callbackQuery();
//        String call = callbackQuery.message().text();
//        String call_answer = callbackQuery.message().replyMarkup().inlinenameButtonboard()[0][0].text();
//        return new DeleteMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().message().messageId());
//    }
//
//    private BaseRequest getRequest(Update update) {
//        String text = update.message().text();
//        if (text.startsWith("/ping")) return Ping.CreatePing(update.message());
//
//        else if (text.startsWith("/"))
//            return new DeleteMessage(update.message().chat().id(), update.message().messageId());
//        else return new SendMessage(update.message().chat().id(), text);
//    }
//
//
//
//}
