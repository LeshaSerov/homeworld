package telegram;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class Ping {

    public static BaseRequest <SendMessage, SendResponse>  CreatePing(Message message) {
        return new SendMessage(message.chat().id(),"Выберите режим пинга.").replyMarkup(
                new InlineKeyboardMarkup(
                        new InlineKeyboardButton("Чат").callbackData("0")
                )
        );
    }
}
