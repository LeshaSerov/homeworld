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
        ChatMember.Status statusKick = ChatMember.Status.kicked;
        ChatMember.Status statusMember = ChatMember.Status.member;
        ChatMember.Status statusAdministrator = ChatMember.Status.administrator;

        if (status == statusKick) {
            DeleteChat(chat, connector);
        } else if (status == statusMember) {
            answer = """
                    Привет)
                    Пожалуйста, выдай мне админские права,
                    иначе я не смогу работать в этом чате.""";
            request = new SendMessage(chat_id, answer);
        } else if (status == statusAdministrator) {
            AddChat(chat, connector);
            answer = """
                    Привет)
                    Я Помощник.
                    Админские права обнаружил,
                    Приступаю к своей работе""";
            request = new SendMessage(chat_id, answer);
        }
        return request;
    }

    private static void AddChat(Chat chat, ConnectionPool connector) {
        try {
            Long chat_id = parseLong(chat.id().toString());
            new ChatDao().addChat(chat_id, chat.title(), connector);
        } catch (Exception ignored) {
        }
    }

    private static void DeleteChat(Chat chat, ConnectionPool connector) {
        try {
            new ChatDao().deleteChat(chat.id(), connector);
        } catch (Exception ignored) {
        }
    }

}
