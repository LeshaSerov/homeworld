package business;

import domain.Group;
import jdbcconnector.JdbcConnection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GroupDao {
    private static final Logger LOGGER = Logger.getLogger(MemberDao.class);

    private static Group getGroupFromResultSet(ResultSet resultSet) {
        try {
            return Group.builder()
                    .id(resultSet.getInt(1))
                    .name(resultSet.getString(2))
                    .build();
        } catch (SQLException e) {
            LOGGER.error("Member creation error");
        }
        return null;
    }

    public static Boolean addGroup(String title) throws IOException, SQLException {
        String SQL_ADD_GROUP = """
                INSERT INTO groups (name) VALUES (?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ADD_GROUP)) {
            preparedStatement.setString(1, title);
            try {
                Integer result = preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean renameGroup(String name, Integer id) throws IOException, SQLException {
        String SQL_ADD_GROUP = """
                INSERT INTO groups (name) VALUES (?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ADD_GROUP)) {
            preparedStatement.setString(1, name);
            try {
                Integer result = preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                return false;
            }
        }
    }


    public static ArrayList<Group> getGroupsWithMember(Integer id_chat) throws IOException, SQLException {
        ArrayList<Group> result = new ArrayList<>();
        String SQL_ALL_GROUPS_WITH_MEMBER = """
                SELECT groups.id, name FROM members,
                members_in_chat, groups WHERE members.id=members_in_group.id_member and members_in_group.id_group=groups.id and id_member=?""";
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ALL_GROUPS_WITH_MEMBER)) {
            preparedStatement.setInt(1, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getGroupFromResultSet(resultSet));
                }
            }
        }
        return result;
    }


}
