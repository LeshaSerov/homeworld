package util;

import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import util.ConnectionPool.ConnectionPool;

public class ConnectionPoolTest {

    @Test
    public void CreateConnectTest() throws SQLException, IOException {
            Connection connection = new ConnectionPool().CreateConnect();
    }
}
