package dao;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.ChatDao;
import repository.dao.MemberDao;
import repository.domain.Member;
import util.jdbcconnector.JdbcConnection;

import java.util.ArrayList;

public class ChatDaoTest {

    @Test(expected = Test.None.class)
    @SneakyThrows
    public void ChatTest(){
        JdbcConnection jdbcConnection = new JdbcConnection();

        new ChatDao().deleteChat(1, jdbcConnection);

        Integer id_member = 5;
        Integer id_member2 = 6;

        boolean result = new ChatDao().addChat(1, "1", jdbcConnection);
        Assert.assertTrue(result);

        result = new ChatDao().editChat(1,"2",false, jdbcConnection);
        Assert.assertTrue(result);

        new MemberDao().addMember(id_member,"a", "b", "c", jdbcConnection);
        new MemberDao().addMember(id_member2,"a", "b", "c", jdbcConnection);

        result = new ChatDao().addMember(id_member,1, jdbcConnection);
        Assert.assertTrue(result);

        result = new ChatDao().addMember(id_member,1, jdbcConnection);
        Assert.assertFalse(result);

        new ChatDao().addMember(id_member2,1, jdbcConnection);

        result = new ChatDao().deleteMember(id_member2,1, jdbcConnection);
        Assert.assertTrue(result);

        result = new ChatDao().deleteAllMembers(1, jdbcConnection);
        Assert.assertTrue(result);

        result = new ChatDao().deleteAllMembers(2, jdbcConnection);
        Assert.assertFalse(result);

        new ChatDao().addMember(id_member2,1, jdbcConnection);

        ArrayList<Member> members = new ChatDao().getAllMemberInChat(1, jdbcConnection);
        Assert.assertSame(1,members.size());

        result = new ChatDao().deleteChat(1, jdbcConnection);
        Assert.assertTrue(result);

        new MemberDao().deleteMember(id_member, jdbcConnection);
        new MemberDao().deleteMember(id_member2, jdbcConnection);

    }
}
