package telegram.operators;

import kotlin.Pair;
import repository.dao.*;
import repository.domain.*;
import telegram.domain.State;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OperatorsWhichGeneratesKeyboard {

    public static List<Pair<String, String>> listGroup(State.Data data) {
        return new MemberDao().getAllGroups(data.getIdThisMember(), data.getConnector());
    }

    public static List<Pair<String, String>> listMembersGroup(State.Data data) {
        ArrayList<Member> members = new GroupDao()
                .getAllMemberInGroup(data.getData().getIdGroup(), data.getConnector());
        List<Pair<String, String>> list = new ArrayList<>();
        for (Member x :
                members) {
            String warns = "";
            if (x.getNumber_of_warning() != 0) {
                for (int i = 0; i < x.getNumber_of_warning(); i++) {
                    warns += "❌";
                }
            }
            String text = x.getFirst_name() + " | " + new GroupDao().getMembersRole(x.getId(), data.getData().getIdGroup(), data.getConnector()).getTitle() + warns;
            list.add(new Pair<String, String>(x.getId().toString(), text));
        }
        return list;
    }

    public static List<Pair<String, String>> listRoles(State.Data data) {
        ArrayList<Role> roles = new RoleDao().getAllRoles(data.getConnector());
        List<Pair<String, String>> list = new ArrayList<>();
        for (Role x :
                roles) {
            list.add(new Pair<>(Long.toString(x.getId()), x.getTitle()));
        }
        return list;
    }

    public static List<Pair<String, String>> listNonMembers(State.Data data) {
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        ArrayList<Member> membersNotInGroup = new GroupDao().getAllMemberNotInGroup(data.getIdThisMember(), data.getData().getIdGroup(), data.getConnector());
        for (Member x : membersNotInGroup) {
            if (x.getLast_name() != null && x.getUser_name() != null)
                list.add(new Pair<String, String>(x.getId().toString(), x.getFirst_name() + ' ' + x.getLast_name() + " (" + x.getUser_name() + ")"));
            else if (x.getLast_name() != null)
                list.add(new Pair<String, String>(x.getId().toString(), x.getFirst_name() + ' ' + x.getLast_name()));
            else if (x.getUser_name() != null)
                list.add(new Pair<String, String>(x.getId().toString(), x.getFirst_name() + " (" + x.getUser_name() + ")"));
            else
                list.add(new Pair<String, String>(x.getId().toString(), x.getFirst_name()));
        }
        return list;
    }

    public static List<Pair<String, String>> listCategories(State.Data data) {
        ArrayList<Category> categories = new FileInGroupDao().getAllCategories(data.getData().getIdGroup(), data.getConnector());
        List<Pair<String, String>> list = new ArrayList<>();
        for (Category x :
                categories) {
            list.add(new Pair<>(Long.toString(x.getId()), x.getTitle()));
        }
        return list;
    }

    public static List<Pair<String, String>> listWarns(State.Data data) {
        Integer id_project = new GroupDao().getProjectGroup(data.getData().getIdGroup(), data.getConnector());
        ArrayList<Warning> warnings = new GroupDao().getAllWarningsMemberFromProject(data.getData().getIdOtherMember(), id_project, data.getConnector());
        List<Pair<String, String>> list = new ArrayList<>();
        for (Warning x : warnings) {
            list.add(new Pair<>(Long.toString(x.getId()),
                    x.getCause()
                    + " | " + new MemberDao().getName(x.getId_cautioning(), data.getConnector())
            ));
        }
        return list;
    }

    public static List<Pair<String, String>> listFiles(State.Data data) {
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        ArrayList<File> files;

        files = new FileInGroupDao().getAllFiles(
                data.getData().getIdGroup(),
                data.getConnector()
        );

        for (File x : files) {
            list.add(new Pair<String, String>(x.getId().toString(),
                    x.getTitle()
                            + " | " + x.getNameMember()
                            + " | " + x.getTitleCategory()
                            + " | " + new SimpleDateFormat("d MMM").format(x.getData_create())

            ));
        }

        return list;
    }

    public static List<Pair<String, String>> listFilesInCategory(State.Data data) {
        ArrayList<Pair<String, String>> list = new ArrayList<>();
        ArrayList<File> files;

        files = new FileInGroupDao().getAllFilesInCategory(
                data.getData().getIdCategory(),
                data.getConnector()
        );

        for (File x : files) {
            list.add(new Pair<String, String>(x.getId().toString(),
                    x.getTitle()
                            + " | " + x.getNameMember()
                            + " | " + new SimpleDateFormat("d MMM").format(x.getData_create())
            ));
        }

        return list;
    }

    public static List<Pair<String, String>> listProjects(State.Data data) {
        List<Pair<String, String>> list = new ProjectDao().getAllProjects(data.getIdThisMember(), data.getConnector());
        return list;
    }

    public static List<Pair<String, String>> listCreatedProjects(State.Data data) {
        List<Pair<String, String>> list = new ProjectDao().getAllCreatedProjects(data.getIdThisMember(), data.getConnector());
        return list;
    }

    public static List<Pair<String, String>> listChats(State.Data data) {
        return new MemberDao().getAllChats(data.getIdThisMember(), data.getConnector());
    }

    public static List<Pair<String, String>> listChatsUnlinkedProjects(State.Data data) {
        return new ProjectDao().getAllChatsUnlinkedProjects(data.getIdThisMember(), data.getConnector());
    }
}
