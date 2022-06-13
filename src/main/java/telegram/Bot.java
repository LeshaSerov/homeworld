package telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import telegram.command.Ping;
import util.ConnectionPool.ConnectionPool;


public class Bot {
    private final TelegramBot bot = new TelegramBot("5417780715:AAFRy0huha6DSO0VNWVJF7ThjN6kpzZ6iWk");

    private ConnectionPool connector;

    public Bot(ConnectionPool connectionPool) {
        connector = connectionPool;
    }

    public void serve() {
        bot.setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }


    private void process(Update update) {




        BaseRequest request = null;

        if (update.callbackQuery() != null) {
            request = getAnswerRequest(update);
        } else if (update.message() != null) {
            request = getRequest(update);
        }

        if (request != null) {
            bot.execute(request);
        }
    }


    private BaseRequest getAnswerRequest(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        String call = callbackQuery.message().text();
        String call_answer = callbackQuery.message().replyMarkup().inlineKeyboard()[0][0].text();
        return new DeleteMessage(update.callbackQuery().message().chat().id(), update.callbackQuery().message().messageId());
    }

    private BaseRequest getRequest(Update update) {
        String text = update.message().text();
        if (text.startsWith("/ping")) return Ping.CreatePing(update.message());

        else if (text.startsWith("/"))
            return new DeleteMessage(update.message().chat().id(), update.message().messageId());
        else return new SendMessage(update.message().chat().id(), text);
    }



}
