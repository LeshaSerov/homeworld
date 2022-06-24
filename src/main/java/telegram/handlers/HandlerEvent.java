package telegram.handlers;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.ChatMemberUpdated;
import com.pengrad.telegrambot.request.SendMessage;
import repository.dao.ChatDao;
import util.ConnectionPool.ConnectionPool;

import static java.lang.Long.parseLong;

public class HandlerEvent {
    public SendMessage processing(ChatMemberUpdated myChatMember, ConnectionPool connector) {
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
            answer = "Привет)\nПожалуйста, выдай мне админские права,\nиначе я не смогу работать в этом чате.";
            request = new SendMessage(chat_id, answer);
        } else if (status == statusAdministrator) {
            AddChat(chat, connector);
            answer = "Привет)\nЯ Помощник. \nАдминские права обнаружил, \nПриступаю к своей работе";
            request = new SendMessage(chat_id, answer);
        }
        return request;
    }

    private void AddChat(Chat chat, ConnectionPool connector) {
        try {
            Long chat_id = parseLong(chat.id().toString().replace("-100", ""));
            new ChatDao().addChat(chat_id, chat.title(), connector);
        } catch (Exception ignored) {
        }
    }

    private void DeleteChat(Chat chat, ConnectionPool connector) {
        try {
            new ChatDao().deleteChat(chat.id(), connector);
        } catch (Exception ignored) {
        }
    }

}
