package util;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import util.jdbcconnector.JdbcConnection;

public class JdbcConnectionTest {

    @Test
    public void CreateConnectTest() throws SQLException, IOException {
            Connection connection = new JdbcConnection().CreateConnect();
    }
}
