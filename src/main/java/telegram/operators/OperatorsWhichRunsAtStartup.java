package telegram.operators;

import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import repository.dao.FileInGroupDao;
import repository.dao.GroupDao;
import telegram.domain.State;

import java.util.ArrayList;
import java.util.List;

public class OperatorsWhichRunsAtStartup {

    public static List<BaseRequest> deleteMember(State.Data data) {
        new GroupDao().deleteMember(data.getData().getIdMember(), data.getData().getIdGroup(), data.getConnector());
        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Пользователь удален.").showAlert(true)
        );
        return list;
    }

    public static List<BaseRequest> changeRole(State.Data data) {
        new GroupDao().editMember(data.getIdThisMember(),
                data.getData().getIdGroup(),
                data.getData().getIdRole(), data.getConnector());
        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Роль Пользователя изменена.").showAlert(true)
        );
        return list;
    }

    public static List<BaseRequest> addMember(State.Data data) {
        new GroupDao().addMember(data.getData().getIdMember(),
                data.getData().getIdGroup(),
                1,
                data.getConnector());
        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Пользователь Добавлен.").showAlert(true)
        );
        return list;
    }

    public static List<BaseRequest> deleteCategory(State.Data data) {
        new FileInGroupDao()
                .deleteCategory(data.getData().getIdCategory(), data.getConnector());
        List<BaseRequest> list = new ArrayList<>();
        list.add(
                new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Категория удалена.").showAlert(true)
        );
        return list;
    }
}