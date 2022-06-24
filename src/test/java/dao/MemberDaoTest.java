package dao;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.MemberDao;
import util.ConnectionPool.ConnectionPool;

import java.io.IOException;
import java.sql.SQLException;

public class MemberDaoTest {

    @Test(expected = Test.None.class)
    @SneakyThrows
    public void memberTest() throws SQLException, IOException {
        ConnectionPool connector = new ConnectionPool();

        Long id_member = 1L;
        Long id_member2 = 2L;

        new MemberDao().deleteMember(id_member, connector);
        new MemberDao().deleteMember(id_member2, connector);

        boolean result = new MemberDao().addMember(id_member,"а","b","c", connector);
        Assert.assertTrue(result);

        result = new MemberDao().addMember(id_member,"e","f","g", connector);
        Assert.assertFalse(result);

        result = new MemberDao().editMember(id_member, "1", "2", "3", connector);
        Assert.assertTrue(result);


        result = new MemberDao().editMember(id_member2,"a","b","c", connector);
        Assert.assertFalse(result);

        result = new MemberDao().deleteMember(id_member, connector);
        Assert.assertTrue(result);

        result = new MemberDao().deleteMember(id_member2, connector);
        Assert.assertFalse(result);
    }

}
