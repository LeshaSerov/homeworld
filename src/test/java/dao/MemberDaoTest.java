package dao;

import org.junit.Assert;
import org.junit.Test;
import repository.dao.MemberDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MemberDaoTest {

    @Test
    public void memberTest() throws SQLException, IOException {
        try
        {
            Integer id_member = 1;
            Integer id_member2 = 2;

            MemberDao.deleteMember(id_member);
            MemberDao.deleteMember(id_member2);

            boolean result = MemberDao.addMember(id_member,"а","b","c");
            Assert.assertTrue(result);

            result = MemberDao.addMember(id_member,"e","f","g");
            Assert.assertFalse(result);

            result = MemberDao.editMember(id_member, "1", "2", "3");
            Assert.assertTrue(result);


            result = MemberDao.editMember(id_member2,"a","b","c");
            Assert.assertFalse(result);

            result = MemberDao.deleteMember(id_member);
            Assert.assertTrue(result);

            result = MemberDao.deleteMember(id_member2);
            Assert.assertFalse(result);

            //Assert.fail();

        }
        catch (Exception ignored)
        {}
    }

}
