package repository.dao;

import repository.domain.Role;
import util.jdbcconnector.JdbcConnection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoleDao {
    private static final Logger LOGGER = Logger.getLogger(RoleDao.class);

    protected static Role getRoleFromResultSet(ResultSet resultSet)
    {
        try {
            return Role.builder()
                    .id(resultSet.getInt(1))
                    .right_to_view(resultSet.getBoolean(2))
                    .right_ping(resultSet.getBoolean(3))
                    .right_edit(resultSet.getBoolean(4))
                    .right_admin(resultSet.getBoolean(5))
                    .build();
        }
        catch (SQLException e){
            LOGGER.error("Role creation error");
        }
        return null;
    }

    public static ArrayList<Role> allRole() throws IOException, SQLException {
        ArrayList<Role> result = new ArrayList<>();
        String SQL_ALL_MEMBERS_IN_CHAT = "SELECT * FROM roles";
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ALL_MEMBERS_IN_CHAT)){
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(getRoleFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

}
