package repository.dao;

import repository.domain.Role;
import util.ConnectionPool.ConnectionPool;
//import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoleDao {
//    private final Logger LOGGER = Logger.getLogger(RoleDao.class);

    protected Role getRoleFromResultSet(ResultSet resultSet) {
        try {
            return Role.builder()
                    .id(resultSet.getInt(1))
                    .title(resultSet.getString(2))
                    .right_to_view(resultSet.getBoolean(3))
                    .right_ping(resultSet.getBoolean(4))
                    .right_edit(resultSet.getBoolean(5))
                    .right_admin(resultSet.getBoolean(6))
                    .right_creator(resultSet.getBoolean(7))
                    .build();
        }
        catch (SQLException e){
//            LOGGER.error("Role creation error");
        }
        return null;
    }

    public Integer addRole(String title, Boolean right_to_view, Boolean right_ping, Boolean right_edit,  Boolean right_admin, Boolean right_creator, ConnectionPool connector) {
        String SQL = """
                INSERT INTO roles (title, right_to_view, right_ping, right_edit, right_admin, right_creator) VALUES (?, ?, ?, ?, ?, ?) RETURNING id;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setBoolean(2, right_to_view);
            preparedStatement.setBoolean(3, right_ping);
            preparedStatement.setBoolean(4, right_edit);
            preparedStatement.setBoolean(5, right_admin);
            preparedStatement.setBoolean(6,right_creator);
            try (ResultSet resultSet = preparedStatement.executeQuery();){
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                return -1;
            }
        }
        catch (Exception exception){
            return null;
        }
    }

    public Boolean deleteRole(Integer id, ConnectionPool connector) {
        String SQL = """
                DELETE FROM roles WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
        catch (Exception exception){
            return null;
        }
    }

    public Role getRole(Integer id_role, ConnectionPool connector) {
        String SQL = """
                SELECT * FROM roles
                Where id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_role);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return getRoleFromResultSet(resultSet);

            }
        }
        catch (Exception exception){
            return null;
        }
    }

    public ArrayList<Role> getAllRoles(ConnectionPool connector) {
        ArrayList<Role> result = new ArrayList<>();
        String SQL = """
                SELECT * FROM roles
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getRoleFromResultSet(resultSet));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }

}
