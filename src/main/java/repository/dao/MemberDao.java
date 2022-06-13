package repository.dao;

import repository.domain.Member;
import repository.domain.Role;
import util.ConnectionPool.ConnectionPool;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDao {
    private final Logger LOGGER = Logger.getLogger(MemberDao.class);

    protected Member getMemberFromResultSet(ResultSet resultSet) {
        try {
            return Member.builder()
                    .id(resultSet.getInt(1))
                    .first_name(resultSet.getString(2))
                    .last_name(resultSet.getString(3))
                    .user_name(resultSet.getString(4))
                    .build();
        }
        catch (SQLException e){
            LOGGER.error("Member creation error");
        }
        return null;
    }

    protected Member getMemberExtendedFromResultSet(ResultSet resultSet) {
        try {
            return Member.builder()
                    .id(resultSet.getInt(1))
                    .first_name(resultSet.getString(2))
                    .last_name(resultSet.getString(3))
                    .user_name(resultSet.getString(4))
                    .role(Role.builder()
                            .id(resultSet.getInt(5))
                            .title(resultSet.getString(6))
                            .right_to_view(resultSet.getBoolean(7))
                            .right_ping(resultSet.getBoolean(8))
                            .right_edit(resultSet.getBoolean(9))
                            .right_admin(resultSet.getBoolean(10))
                            .build())
                    .number_of_warning(resultSet.getInt(11))
                    .build();
        }
        catch (SQLException e){
            LOGGER.error("Member creation error");
        }
        return null;
    }

    public Boolean addMember(Integer id, String first_name, String last_name, String user_name, ConnectionPool connector) throws IOException, SQLException {
        String SQL = """
                INSERT INTO members (id, first_name, last_name, user_name) VALUES (?, ?, ?, ?) ON CONFLICT (id) DO NOTHING;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, first_name);
            preparedStatement.setString(3, last_name);
            preparedStatement.setString(4, user_name);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public Boolean editMember(Integer id, String first_name, String last_name, String user_name, ConnectionPool connector) throws IOException, SQLException {
        String SQL = """
                UPDATE members SET first_name = ?, last_name = ?, user_name = ? WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(4, id);
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, last_name);
            preparedStatement.setString(3, user_name);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public Boolean deleteMember(Integer id, ConnectionPool connector) throws IOException, SQLException {
        String SQL = """
                DELETE FROM members WHERE id = ?;
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
    }

    //Метод просмотр в каких группах есть этот пользователь




}
