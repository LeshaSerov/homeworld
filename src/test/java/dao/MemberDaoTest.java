package dao;

import org.junit.Assert;
import org.junit.Test;
import repository.dao.MemberDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MemberDaoTest {

    @Test
    public void addMemberTest() throws SQLException, IOException {
        try
        {;
            boolean result = MemberDao.addMember(1,"а","b","c");
            Assert.assertTrue(result);

            result = MemberDao.addMember(1,"e","f","g");
                Assert.assertFalse(result);

            result = MemberDao.deleteMember(1);
            Assert.assertTrue(result);

            result = MemberDao.deleteMember(2);
            Assert.assertFalse(result);
        }
        catch (Exception ignored)
        {}
    }

    @Test
    public void editMemberTest() {

    }
}
