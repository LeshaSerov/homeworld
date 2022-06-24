package dao;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.ChatDao;
import repository.dao.MemberDao;
import repository.domain.Member;
import util.ConnectionPool.ConnectionPool;

import java.util.ArrayList;

public class ChatDaoTest {

    @Test(expected = Test.None.class)
    @SneakyThrows
    public void ChatTest(){
        ConnectionPool connector = new ConnectionPool();

        new ChatDao().deleteChat(1L, connector);

        Long id_member = 5L;
        Long id_member2 = 6L;

        boolean result = new ChatDao().addChat(1L, "1", connector);
        Assert.assertTrue(result);

        result = new ChatDao().editChat(1L,"2",false, connector);
        Assert.assertTrue(result);

        new MemberDao().addMember(id_member,"a", "b", "c", connector);
        new MemberDao().addMember(id_member2,"a", "b", "c", connector);

        result = new ChatDao().addMember(id_member,1L, connector);
        Assert.assertTrue(result);

        result = new ChatDao().addMember(id_member,1L, connector);
        Assert.assertFalse(result);

        new ChatDao().addMember(id_member2,1L, connector);

        result = new ChatDao().deleteMember(id_member2,1L, connector);
        Assert.assertTrue(result);

        result = new ChatDao().deleteAllMembers(1L, connector);
        Assert.assertTrue(result);

        result = new ChatDao().deleteAllMembers(2L, connector);
        Assert.assertFalse(result);

        new ChatDao().addMember(id_member2,1L, connector);

        ArrayList<Member> members = new ChatDao().getAllMemberInChat(1L, connector);
        Assert.assertSame(1,members.size());

        result = new ChatDao().deleteChat(1L, connector);
        Assert.assertTrue(result);

        new MemberDao().deleteMember(id_member, connector);
        new MemberDao().deleteMember(id_member2, connector);

    }
}
