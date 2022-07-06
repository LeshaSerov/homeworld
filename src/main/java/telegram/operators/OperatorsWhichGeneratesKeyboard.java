package telegram.operators;

import kotlin.Pair;
import repository.dao.*;
import repository.domain.Category;
import repository.domain.Member;
import repository.domain.Role;
import telegram.domain.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            list.add(new Pair<>(Long.toString(x.getId()), x.getFirst_name()));
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
        ArrayList<Member> membersInGroup = new GroupDao().getAllMemberInGroup(data.getData().getIdGroup(), data.getConnector());
        ArrayList<Pair<String,String>> chats = new MemberDao().getAllChats(data.getIdThisMember(), data.getConnector());
        for (Pair<String,String> idChat: chats) {
            ArrayList<Member> members = new ChatDao()
                    .getAllMemberInChat(Long.valueOf(idChat.component1()), data.getConnector());
            for (Member x : members) {
                boolean a = !list.stream().anyMatch(p-> Objects.equals(p.component2(), x.getFirst_name()));
                boolean b = !membersInGroup.stream().anyMatch(p-> Objects.equals(p.getId(), x.getId()));
                if (a && b)
                    list.add(new Pair<>(Long.toString(x.getId()), x.getFirst_name()));
            }
        }
        return list;
    }

    public static List<Pair<String, String>> listCategories(State.Data data) {
        ArrayList<Category> categories = new FileInGroupDao().getAllCategories(data.getData().getIdGroup(),data.getConnector());
        List<Pair<String, String>> list = new ArrayList<>();
        for (Category x :
                categories) {
            list.add(new Pair<>(Long.toString(x.getId()), x.getTitle()));
        }
        return list;
    }

}
