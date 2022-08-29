package telegram.Events;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import repository.dao.ChatDao;
import util.ConnectionPool.ConnectionPool;

import static java.lang.Long.parseLong;

public class HandlerEventsModifyRightsBot {
    public static BaseRequest process(ChatMemberUpdated myChatMember, ConnectionPool connector) {
        SendMessage request = null;

        Chat chat = myChatMember.chat();
        Long chat_id = chat.id();

        String answer = null;

        ChatMember.Status status = myChatMember.newChatMember().status();
        ChatMember.Status statusLeft = ChatMember.Status.left;
        ChatMember.Status statusMember = ChatMember.Status.member;
        ChatMember.Status statusAdministrator = ChatMember.Status.administrator;

        if (status == statusAdministrator) {
            try {
                new ChatDao().addChat(chat_id, chat.title(), connector);
            } catch (Exception ignored) {
            }
            answer = """
                    Привет)
                    Я Помощник.
                    Админские права обнаружил,
                    Приступаю к своей работе""";
            request = new SendMessage(chat_id, answer);
        }
        else if (status == statusLeft)
        {
            try {
                new ChatDao().deleteChat(chat_id, connector);
            } catch (Exception ignored) {
            }
        }
        return request;
    }

}
