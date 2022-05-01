import repository.dao.GroupDao;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import util.jdbcconnector.JdbcConnection;

public class JdbsConnectionUtilTest {
    @Test
    public void CreateConnectTest() throws SQLException, IOException {
        try {
            Connection connection = new JdbcConnection().CreateConnect();
        }
        catch (SQLException e){

            System.out.println("Connection creation error");
        };
    }

    @Test
    public void a() throws SQLException, IOException {
        try {
            System.out.println(GroupDao.editGroup(47,"qwe"));
        }
        catch (SQLException e){

            System.out.println("Connection error");
        };
    }
}
