package dao;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import repository.dao.MemberDao;
import util.jdbcconnector.JdbcConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MemberDaoTest {

    @Test(expected = Test.None.class)
    @SneakyThrows
    public void memberTest() throws SQLException, IOException {
        JdbcConnection jdbcConnection = new JdbcConnection();

        Integer id_member = 1;
        Integer id_member2 = 2;

        new MemberDao().deleteMember(id_member, jdbcConnection);
        new MemberDao().deleteMember(id_member2, jdbcConnection);

        boolean result = new MemberDao().addMember(id_member,"а","b","c", jdbcConnection);
        Assert.assertTrue(result);

        result = new MemberDao().addMember(id_member,"e","f","g", jdbcConnection);
        Assert.assertFalse(result);

        result = new MemberDao().editMember(id_member, "1", "2", "3", jdbcConnection);
        Assert.assertTrue(result);


        result = new MemberDao().editMember(id_member2,"a","b","c", jdbcConnection);
        Assert.assertFalse(result);

        result = new MemberDao().deleteMember(id_member, jdbcConnection);
        Assert.assertTrue(result);

        result = new MemberDao().deleteMember(id_member2, jdbcConnection);
        Assert.assertFalse(result);
    }

}
