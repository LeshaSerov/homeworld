package telegram.operators;

import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import repository.dao.FileInGroupDao;
import repository.dao.GroupDao;
import telegram.domain.State;

import java.util.ArrayList;
import java.util.List;

public class OperatorsWhoProcessesMessages {

    public static List<BaseRequest> addGroup(State.Data data) {
        Integer id = new GroupDao().addGroup(
                data.getUpdate().message().text(),
                data.getConnector()
        );
        new GroupDao().addMember(
                data.getIdThisMember(), id, 6, data.getConnector());

        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new SendMessage(data.getUpdate().message().chat().id(),
                        "Добавлена группа." )
        );
        return list;
    }

    public static List<BaseRequest> addCategory(State.Data data) {
        new FileInGroupDao().addCategory(
                data.getData().getIdGroup(),
                data.getUpdate().message().text(),
                data.getConnector()
        );
        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new SendMessage(data.getUpdate().message().chat().id(),
                        "Добавлена категория." )
        );
        return list;
    }

    public static List<BaseRequest> editCategory(State.Data data) {
        new FileInGroupDao().editCategory(
                data.getData().getIdGroup(),
                data.getUpdate().message().text(),
                data.getConnector()
        );
        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new SendMessage(data.getUpdate().message().chat().id(),
                        "Изменена категория.")
        );
        return list;
    }

    public static List<BaseRequest> addFile(State.Data data) {
        SendResponse response = data.getBot().execute(new ForwardMessage(
                new FileInGroupDao().getIdResourceChat(data.getConnector()),
                data.getUpdate().message().chat().id(),
                data.getUpdate().message().messageId()
        ));

        new FileInGroupDao().addFile(
                response.message().messageId(),
                data.getData().getIdCategory(),
                data.getUpdate().message().text(),
                data.getConnector()
        );
        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new SendMessage(data.getUpdate().message().chat().id(),
                        "Добавлен Файл." )
        );
        return list;
    }


}
