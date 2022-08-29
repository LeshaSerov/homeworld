package telegram.operators;

import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import repository.dao.FileInGroupDao;
import repository.dao.GroupDao;
import repository.dao.ProjectDao;
import repository.domain.File;
import repository.domain.Role;
import telegram.domain.Access;
import telegram.domain.State;

import java.util.ArrayList;
import java.util.List;


public class OperatorsWhoProcessesMessages {

    public static List<BaseRequest> addGroup(State.Data data) {
        Integer id = new GroupDao().addGroup(
                data.getUpdate().message().text(),
                data.getData().getIdProject(),
                data.getConnector());
        Boolean r = new GroupDao().addMember(
                data.getIdThisMember(), id, 7, data.getConnector());

        List<BaseRequest> list = new ArrayList<>();

        if (r) {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Добавлена группа."));
        } else {
            list.add(new SendMessage
                    (data.getUpdate().message().chat().id(),
                            "Ошибка при добавлении группы в базу данных."));
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> addCategory(State.Data data) {
        Boolean r = new FileInGroupDao().addCategory(
                data.getData().getIdGroup(),
                data.getUpdate().message().text(),
                data.getConnector());

        List<BaseRequest> list = new ArrayList<>();

        if (r) {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Добавлена категория.")
            );
        } else {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Ошибка при добавлении категории в базу данных.")
            );
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> editCategory(State.Data data) {
        Boolean r = new FileInGroupDao().editCategory(
                data.getData().getIdCategory(),
                data.getUpdate().message().text(),
                data.getConnector());
        List<BaseRequest> list = new ArrayList<>();

        if (r)
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Изменена категория.")
            );
        else {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Ошибка при изменении категории")
            );
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> addTitleFile(State.Data data) {
        List<BaseRequest> list = new ArrayList<>();
        try {
            if (data.getUpdate().message().text() == null)
                throw new Exception();
            data.getData().setTitleFile(data.getUpdate().message().text());
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Название файла сохранено.")
            );
        } catch (Exception exception) {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Ошибка при обработке названия файла.")
            );
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> addFile(State.Data data) {
        SendResponse response = data.getBot().execute(new ForwardMessage(
                new FileInGroupDao().getIdResourceChat(data.getConnector()),
                data.getUpdate().message().chat().id(),
                data.getUpdate().message().messageId()));
        Boolean r1 = response != null; //НАДО ТЕСТИТЬ МОЖЕТ НЕ РАБОТАТЬ
        Boolean r2 = new FileInGroupDao().addFile(
                response.message().messageId(),
                data.getData().getIdCategory(),
                data.getData().getTitleFile(),
                data.getIdThisMember(),
                data.getConnector()
        );

        List<BaseRequest> list = new ArrayList<>();
        if (r1 && r2) {
            list.add(
                    new SendMessage(data.getUpdate().message().chat().id(),
                            "Добавлен Файл."));
        } else if (r2) {
            list.add(
                    new SendMessage(data.getUpdate().message().chat().id(),
                            "Ошибка при отправке файла в хранилище."));
            data.getData().setIsReturn(true);
        } else {

            list.add(
                    new SendMessage(data.getUpdate().message().chat().id(),
                            "Ошибка при добавлении файла в базу данных."));
            list.add(
                    new DeleteMessage(
                            new FileInGroupDao().getIdResourceChat(data.getConnector()),
                            response.message().messageId()));
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> addWarning(State.Data data) {
        List<BaseRequest> list = new ArrayList<>();

        Role roleThisMember = new GroupDao().getMembersRole(data.getIdThisMember(), data.getData().getIdGroup(), data.getConnector());
        Role roleOtherMember = new GroupDao().getMembersRole(data.getData().getIdOtherMember(), data.getData().getIdGroup(), data.getConnector());

        if (Access.checkAccess(roleThisMember, roleOtherMember)) {

            Boolean r = new GroupDao().addWarning(
                    data.getData().getIdOtherMember(),
                    data.getIdThisMember(),
                    data.getData().getIdGroup(),
                    data.getUpdate().message().text(),
                    30,
                    data.getConnector()
            );

            if (r) {
                list.add(new SendMessage(data.getUpdate().message().chat().id(),
                        "Добавлено Предупреждение.")
                );
                Integer id_project = new GroupDao().getProjectGroup(data.getData().getIdGroup(), data.getConnector());
                Integer count = new GroupDao().getCountWarningsFromMemberInProject(data.getData().getIdOtherMember(), id_project, data.getConnector());
                if (count >= 3)
                    new GroupDao().editRoleMembers(
                            data.getData().getIdOtherMember(),
                            data.getData().getIdGroup(),
                            1,
                            data.getConnector());
            } else {
                list.add(new SendMessage(data.getUpdate().message().chat().id(),
                        "Ошибка при добавлении предупреждения в базу данных.")
                );
                data.getData().setIsReturn(true);
            }
        } else {
            list.add(new SendMessage(data.getUpdate().message().chat().id(), "Недостаточно прав для выполнения операции."));
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> editWarning(State.Data data) {
        List<BaseRequest> list = new ArrayList<>();

        Role roleThisMember = new GroupDao().getMembersRole(data.getIdThisMember(), data.getData().getIdGroup(), data.getConnector());
        Role roleOtherMember = new GroupDao().getMembersRole(data.getData().getIdOtherMember(), data.getData().getIdGroup(), data.getConnector());

        if (Access.checkAccess(roleThisMember, roleOtherMember)) {

            Boolean r = new GroupDao().editWarning(
                    data.getData().getIdWarning(),
                    data.getUpdate().message().text(),
                    data.getConnector());

            if (r) {
                list.add(new SendMessage(data.getUpdate().message().chat().id(),
                        "Изменено Предупреждение.")
                );
            } else {
                list.add(new SendMessage(data.getUpdate().message().chat().id(),
                        "Ошибка при изменении предупреждения")
                );
                data.getData().setIsReturn(true);
            }
        } else {
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Недостаточно прав для выполнения операции."));
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> editFile(State.Data data) {
        Boolean r = new FileInGroupDao().editFile(
                data.getData().getIdFile(),
                data.getUpdate().message().text(),
                data.getConnector());
        List<BaseRequest> list = new ArrayList<>();

        if (r) {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Изменено название файла.")
            );
        } else {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Ошибка при изменении названия файла")
            );
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> searchFile(State.Data data) {
        ArrayList<File> files = new FileInGroupDao().getAllFiles(
                data.getData().getIdGroup(),
                data.getConnector()
        );

        Boolean r = files == null;
        List<BaseRequest> list = new ArrayList<>();

        if (r) {
            Integer count = 0;
            for (File x : files) {
                if (count <= 10)
                    if (x.getTitle().contains(data.getUpdate().message().text())) {
                        count++;
                        list.add(new ForwardMessage(
                                new FileInGroupDao().getIdResourceChat(data.getConnector()),
                                data.getIdThisMember(),
                                data.getData().getIdFile()
                        ));
                    }
            }
        }

        if (r) {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Изменено Предупреждение.")
            );
        } else {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Ошибка при изменении предупреждения")
            );
            data.getData().setIsReturn(true);
        }
        return list;
    }


    public static List<BaseRequest> addProject(State.Data data) {
        Integer idProject = new ProjectDao().addProject(
                data.getIdThisMember(),
                data.getUpdate().message().text(),
                data.getData().getIdChat(),
                data.getConnector());
        List<BaseRequest> list = new ArrayList<>();

        if (idProject > 0) {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Добавлен проект.")
            );
        } else {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Ошибка при добавлении проекта")
            );
            data.getData().setIsReturn(true);
        }
        return list;
    }

    public static List<BaseRequest> editProject(State.Data data) {
        Boolean r = new ProjectDao().editProject(
                data.getData().getIdProject(),
                data.getUpdate().message().text(),
                data.getConnector());
        List<BaseRequest> list = new ArrayList<>();

        if (r) {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Изменен Проект.")
            );
        } else {
            list.add(new SendMessage(data.getUpdate().message().chat().id(),
                    "Ошибка при изменении Проекта")
            );
            data.getData().setIsReturn(true);
        }
        return list;
    }

}
