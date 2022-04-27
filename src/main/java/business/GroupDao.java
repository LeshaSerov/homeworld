package business;

import domain.Group;
import jdbcconnector.JdbcConnection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class GroupDao {
    private static final Logger LOGGER = Logger.getLogger(MemberDao.class);

    protected static Group getGroupFromResultSet(ResultSet resultSet) {
        try {
            return Group.builder()
                    .id(resultSet.getInt(1))
                    .title(resultSet.getString(2))
                    .build();
        } catch (SQLException e) {
            LOGGER.error("Group creation error");
        }
        return null;
    }

    public static Boolean addMember(Integer id_member, Integer id_group) throws IOException, SQLException {
        String SQL = """
                INSERT INTO members_in_groups (id_group, id_member, id_role) VALUES (?, ?, ?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_member);
            preparedStatement.setInt(3, 1);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean editMember(Integer id_member, Integer id_group, Integer id_role) throws IOException, SQLException {
        String SQL = """
                UPDATE members_in_groups SET id_role = ? WHERE id_member = ? and id_group = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_role);
            preparedStatement.setInt(2, id_member);
            preparedStatement.setInt(3, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean deleteMember(Integer id_member, Integer id_group) throws IOException, SQLException {
        String SQL = """
                DELETE FROM members_in_groups WHERE id_group = ? and id_member = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean deleteAllMembers(Integer id_group) throws IOException, SQLException {
        String SQL = """
                DELETE members_in_groups WHERE id_group = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean addGroup(String title) throws IOException, SQLException {
        String SQL = """
                INSERT INTO groups (title) VALUES (?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean editGroup(Integer id_group,String title) throws IOException, SQLException {
        String SQL = """
                UPDATE groups SET title = ? WHERE id = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean deleteGroup(Integer id_group) throws IOException, SQLException {
        deleteAllMembers(id_group);
        deleteAllWarningsInGroup(id_group);
        //*удалить все файлы
        //*удалить все категории

        String SQL = """
                DELETE FROM groups WHERE id_group = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean addWarning(Integer id_member, Integer id_group, Integer id_cautioning, String cause, Date date, Integer deadline) throws IOException, SQLException {
        String SQL = """
                INSERT INTO warnings (id_group, id_member, id_cautioning, cause, date, deadline) VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_member);
            preparedStatement.setInt(3, id_cautioning);
            preparedStatement.setString(4, cause);
            preparedStatement.setDate(5, date);
            preparedStatement.setInt(6, deadline);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean deleteWarning(Integer id_group, Integer id_member) throws IOException, SQLException {
        String SQL = """
                DELETE FROM warnings WHERE id_group = ? and id_member = ? and Date != null;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
        //запустить предупреждение у которое позже
    }

    public static Boolean deleteAllWarning(Integer id_group, Integer id_member) throws IOException, SQLException {
        String SQL = """
                DELETE FROM warnings WHERE id_group = ? and id_member = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }



    public static Boolean deleteAllWarningsInGroup(Integer id_group) throws IOException, SQLException {
        String SQL = """
                DELETE FROM warnings WHERE id_group = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
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
