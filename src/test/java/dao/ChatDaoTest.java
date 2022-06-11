package dao;

import org.junit.Assert;
import org.junit.Test;
import repository.dao.ChatDao;
import repository.dao.MemberDao;
import repository.domain.Member;

import java.util.ArrayList;

public class ChatDaoTest {

    @Test
    public void ChatTest(){
        try {
            ChatDao.deleteChat(1);

            Integer id_member = 5;
            Integer id_member2 = 6;

            boolean result = ChatDao.addChat(1, "1");
            Assert.assertTrue(result);

            result = ChatDao.editChat(1,"2",false);
            Assert.assertTrue(result);

            MemberDao.addMember(id_member,"a", "b", "c");
            MemberDao.addMember(id_member2,"a", "b", "c");

            result = ChatDao.addMember(id_member,1);
            Assert.assertTrue(result);

            result = ChatDao.addMember(id_member,1);
            Assert.assertFalse(result);

            ChatDao.addMember(id_member2,1);

            result = ChatDao.deleteMember(id_member2,1);
            Assert.assertTrue(result);

            result = ChatDao.deleteAllMembers(1);
            Assert.assertTrue(result);

            result = ChatDao.deleteAllMembers(2);
            Assert.assertFalse(result);

            ChatDao.addMember(id_member2,1);

            ArrayList<Member> members = ChatDao.getAllMemberInChat(1);
            Assert.assertSame(1,members.size());

            result = ChatDao.deleteChat(1);
            Assert.assertTrue(result);

            MemberDao.deleteMember(id_member);
            MemberDao.deleteMember(id_member2);

            //Assert.fail();
        }
        catch (Exception ignored)
        {

        }
    }
}
