package telegram.operators;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import repository.dao.*;
import repository.domain.Member;
import repository.domain.Role;
import telegram.domain.Access;
import telegram.domain.State;

import java.util.ArrayList;
import java.util.List;


public class OperatorsWhichRunsAtStartup {

    public static List<BaseRequest> deleteMember(State.Data data) {
        List<BaseRequest> list = new ArrayList<>();

        Role roleThisMember = new GroupDao().getMembersRole(data.getIdThisMember(), data.getData().getIdGroup(), data.getConnector());
        Role roleOtherMember = new GroupDao().getMembersRole(data.getData().getIdOtherMember(), data.getData().getIdGroup(), data.getConnector());

        if (Access.checkAccess(roleThisMember, roleOtherMember)) {

            Boolean r = new GroupDao().deleteMember(
                    data.getData().getIdOtherMember(),
                    data.getData().getIdGroup(),
                    data.getConnector());

            if (r)
                list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Пользователь удален."));
            else
                list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Ошибка при удалении пользователя"));
        }
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Недостаточно прав для выполнения операции."));
        return list;
    }

    public static List<BaseRequest> editRole(State.Data data) {
        List<BaseRequest> list = new ArrayList<>();

        Role roleThisMember = new GroupDao().getMembersRole(data.getIdThisMember(), data.getData().getIdGroup(), data.getConnector());
        Role roleOtherMember = new GroupDao().getMembersRole(data.getData().getIdOtherMember(), data.getData().getIdGroup(), data.getConnector());
        Role nextRoleOtherMember = new RoleDao().getRole(data.getData().getIdRoleOtherMember(), data.getConnector());

        if (Access.checkAccess(roleThisMember, roleOtherMember) && Access.checkAccess(roleThisMember, nextRoleOtherMember)) {

            Boolean r = new GroupDao().editRoleMembers(
                    data.getData().getIdOtherMember(),
                    data.getData().getIdGroup(),
                    data.getData().getIdRoleOtherMember(),
                    data.getConnector());

            if (r)
                list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Роль пользователя изменена."));
            else
                list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                        .text("Ошибка при изменении роли пользователя."));

        }
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Недостаточно прав для выполнения операции."));
        return list;
    }

    public static List<BaseRequest> addMember(State.Data data) {
        Boolean r = new GroupDao().addMember(
                data.getData().getIdOtherMember(),
                data.getData().getIdGroup(),
                2,
                data.getConnector());

        List<BaseRequest> list = new ArrayList<>();

        if (r)
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Пользователь добавлен."));
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id()
            ).text("Ошибка при добавлении пользователя в группу."));

        return list;
    }

    public static List<BaseRequest> deleteCategory(State.Data data) {
        Boolean r = new FileInGroupDao().deleteCategory(
                data.getData().getIdCategory(),
                data.getConnector());

        List<BaseRequest> list = new ArrayList<>();

        if (r)
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Категория удалена."));
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Ошибка при удалении категории."));

        return list;
    }

    public static List<BaseRequest> deleteWarning(State.Data data) {
        Boolean r = new GroupDao().deleteWarning(
                data.getData().getIdWarning(),
                data.getData().getIdOtherMember(),
                data.getConnector());

        List<BaseRequest> list = new ArrayList<>();

        if (r)
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Предупреждение удалено."));
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Ошибка при удалении предупреждения."));

        return list;
    }

    public static List<BaseRequest> Ping(State.Data data) {
        ArrayList<Member> members = new GroupDao().getAllMemberInGroup(
                data.getData().getIdGroup(),
                data.getConnector()
        );

        boolean r = members != null;
        List<BaseRequest> list = new ArrayList<>();

        if (r)
        {
            String text = "Пинг группы: "
                    + new GroupDao().getTitleGroup(data.getData().getIdGroup(), data.getConnector())
                    + " \\(" + new MemberDao().getName(data.getIdThisMember(), data.getConnector()) + "\\)";
            list.add(new SendMessage(new ChatDao().getIdTelegram(data.getData().getIdChat(), data.getConnector()), text).parseMode(ParseMode.MarkdownV2));



            int count = 0;
            text = "";
            for (Member x: members) {
                count++;
                text += "[" + x.getFirst_name()+ "]" + "(tg://user?id=" + x.getId() + ")\n";
                if (count == 5) {
                    list.add(new SendMessage(new ChatDao().getIdTelegram(data.getData().getIdChat(), data.getConnector()), text).parseMode(ParseMode.MarkdownV2));
                    count = 0;
                    text = "";
                }
            }
            list.add(new SendMessage(new ChatDao().getIdTelegram(data.getData().getIdChat(), data.getConnector()), text).parseMode(ParseMode.MarkdownV2));
        }

        if (r)
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("⁉"));
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id()
            ).text("Ошибка при получении списка пользователей группы."));

        return list;
    }

    public static List<BaseRequest> File(State.Data data){
        List<BaseRequest> list = new ArrayList();

        list.add(new SendMessage(
                data.getIdThisMember(),
                new FileInGroupDao().getTitleFile(data.getData().getIdFile(), data.getConnector())
        ));
        list.add(new ForwardMessage(
                data.getIdThisMember(),
                new FileInGroupDao().getIdResourceChat(data.getConnector()),
                data.getData().getIdFile()
        ));

        return list;
    }

    public static List<BaseRequest> deleteFile(State.Data data) {
        Boolean r = new FileInGroupDao().deleteFile(
                data.getData().getIdFile(),
                data.getConnector());

        List<BaseRequest> list = new ArrayList<>();

        list.add(new DeleteMessage(
                new FileInGroupDao().getIdResourceChat(data.getConnector()),
                data.getData().getIdFile()
                ));

        if (r)
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Файл удален."));
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Ошибка при удалении файла."));

        return list;
    }

    public static List<BaseRequest> deleteProject(State.Data data) {
        Boolean r = new ProjectDao().deleteProject(
                data.getData().getIdProject(),
                data.getConnector()
                );
        List<BaseRequest> list = new ArrayList<>();

        if (r)
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Проект удален."));
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Ошибка при удалении проекта."));
        return list;
    }

    public static List<BaseRequest> DeleteGroup(State.Data data) {
        Boolean r = new GroupDao().deleteGroup(
                data.getData().getIdGroup(),
                data.getConnector()
        );
        List<BaseRequest> list = new ArrayList<>();

        if (r)
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Группа удалена."));
        else
            list.add(new AnswerCallbackQuery(data.getUpdate().callbackQuery().id())
                    .text("Ошибка при удалении группы."));
        return list;
    }
}