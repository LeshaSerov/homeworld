package telegram.Events;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import repository.dao.ChatDao;
import repository.dao.MemberDao;
import util.ConnectionPool.ConnectionPool;

import java.util.Arrays;

public class HandlerEventsInGroups {
    public static BaseRequest process(Message message, ConnectionPool connector) {
        BaseRequest request = null;
        Long chat_id = message.chat().id();
        User[] updateNewUser = message.newChatMembers();
        User updateLeftUser = message.leftChatMember();

        if (updateNewUser != null && Arrays.stream(updateNewUser).noneMatch(User::isBot)) {
            try {
                User user = message.from();
                new MemberDao().addMember(user.id(), user.firstName(), user.lastName(), user.username(), connector);
                new ChatDao().addMember(user.id(), chat_id, connector);
            }
            catch (Exception ignored)
            {

            }
            request = new SendMessage(chat_id, "Пользователь пришел в чат.");
        }
        else if (updateLeftUser != null) {
            try {
                new ChatDao().deleteMember(chat_id, updateLeftUser.id(), connector);
            }
            catch (Exception ignored){

            }
            request = new SendMessage(chat_id, "Пользователь вышел из чата.");
        }
        else try {
                User user = message.from();
                new MemberDao().addMember(user.id(), user.firstName(), user.lastName(), user.username(), connector);
                new ChatDao().addChat(message.chat().id(), message.chat().title(), connector);
                new ChatDao().addMember(user.id(), message.chat().id(), connector);
            }
        catch (Exception ignored)
        {
        }
        return request;
    }
}
