package telegram.operators;

import kotlin.Pair;
import repository.dao.GroupDao;
import repository.dao.MemberDao;
import repository.domain.Member;
import telegram.domain.State;

import java.util.ArrayList;
import java.util.List;

public class OperatorsWhichGeneratesKeyboard {

    public static List<Pair<String, String>> listGroup(State.Data data) {
        return new MemberDao().getAllGroups(data.getIdMember(), data.getConnector());
    }

    public static List<Pair<String, String>> listMembersGroup(State.Data data) {
        ArrayList<Member> members = new GroupDao().getAllMemberInGroup(data.getData().getIdGroup(), data.getConnector());
        List<Pair<String, String>> list = new ArrayList<>();
        for (Member x :
                members) {
            list.add(new Pair<>(Long.toString(x.getId()), x.getFirst_name()));
        }
        return list;
    }


}
