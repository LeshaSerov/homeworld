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
                    .title(resultSet.getString(2))
                    .right_to_view(resultSet.getBoolean(3))
                    .right_ping(resultSet.getBoolean(4))
                    .right_edit(resultSet.getBoolean(5))
                    .right_admin(resultSet.getBoolean(6))
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

    public static Integer addRole(String title, Boolean right_ping, Boolean right_edit, Boolean right_to_view, Boolean right_admin) throws IOException, SQLException {
        String SQL = """
                INSERT INTO roles (title, right_ping, right_edit, right_to_view, right_admin) VALUES (?, ?, ?, ?, ?) RETURNING id;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setBoolean(2, right_ping);
            preparedStatement.setBoolean(3, right_edit);
            preparedStatement.setBoolean(4, right_to_view);
            preparedStatement.setBoolean(5, right_admin);
            try (ResultSet resultSet = preparedStatement.executeQuery();){
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                return -1;
            }
        }
    }

    public static Boolean deleteRole(Integer id) throws IOException, SQLException {
        String SQL = """
                DELETE FROM roles WHERE id = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

}
