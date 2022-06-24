package telegram.handlers;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import repository.dao.ChatDao;
import repository.dao.MemberDao;
import util.ConnectionPool.ConnectionPool;

import java.util.Arrays;
import java.util.Objects;

public class HandlerGroupMessages {
    public BaseRequest processing(Message message, ConnectionPool connector) {
        BaseRequest request = null;
        Long chat_id = message.chat().id();
        User[] updateNewUser = message.newChatMembers();
        User updateLeftUser = message.leftChatMember();

        if (updateNewUser != null && Arrays.stream(updateNewUser).noneMatch(User::isBot)) {
            AddMemberInChat(chat_id, Arrays.stream(updateNewUser).findFirst().orElse(null), connector);
            //request = new SendMessage(chat_id, "Привет)");
        } else if (updateLeftUser != null) {
            DeleteMemberInChat(chat_id, updateLeftUser.id(), connector);
            //request = new SendMessage(chat_id, "Пока");
        }
        else {
            AddMemberInChat(chat_id, message.from(), connector);
            if (message.text().startsWith("/default") | message.text().startsWith("/exit"))
                request = new DeleteMessage(chat_id, message.messageId());
        }
        return request;
    }

    private void AddMemberInChat(Long chat_id, User user, ConnectionPool connector) {
        try {
            new MemberDao().addMember(user.id(), user.firstName(), user.lastName(), user.username(), connector);
            new ChatDao().addMember(user.id(), chat_id, connector);
        } catch (Exception ignored) {
        }
    }

    private void DeleteMemberInChat(Long chat_id, Long id, ConnectionPool connector) {
        try {
            new ChatDao().deleteMember(id, chat_id, connector);
        } catch (Exception ignored) {
        }
    }

}
