package business;

import domain.Chat;
import domain.Member;
import domain.Role;
import jdbcconnector.JdbcConnection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MemberDao {
    private static final Logger LOGGER = Logger.getLogger(MemberDao.class);

    protected static Member getMemberFromResultSet(ResultSet resultSet)
    {
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

    protected static Member getMemberExtendedFromResultSet(ResultSet resultSet)
    {
        try {
            return Member.builder()
                    .id(resultSet.getInt(1))
                    .first_name(resultSet.getString(2))
                    .last_name(resultSet.getString(3))
                    .user_name(resultSet.getString(4))
                    .role(Role.builder()
                            .id(resultSet.getInt(5))
                            .right_to_view(resultSet.getBoolean(6))
                            .right_ping(resultSet.getBoolean(7))
                            .right_edit(resultSet.getBoolean(8))
                            .right_admin(resultSet.getBoolean(9))
                            .build())
                    .number_of_warning(resultSet.getInt(10))
                    .build();
        }
        catch (SQLException e){
            LOGGER.error("Member creation error");
        }
        return null;
    }

    public static Boolean addMember(Integer id, String first_name, String last_name, String user_name) throws IOException, SQLException {
        String SQL = """
                INSERT INTO members (id, first_name, last_name, user_name) VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, first_name);
            preparedStatement.setString(3, last_name);
            preparedStatement.setString(4, user_name);
            try {
                Integer result = preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean editMember(Integer id, String first_name, String last_name, String user_name) throws IOException, SQLException {
        String SQL = """
                UPDATE members SET first_name = ? AND last_name = ? AND user_name = ? WHERE id = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
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

}
