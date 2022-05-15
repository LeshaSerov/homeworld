package repository.dao;

import repository.domain.Group;
import repository.domain.Member;
import repository.domain.Role;
import repository.domain.Warning;
import util.jdbcconnector.JdbcConnection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class GroupDao {
    private static final Logger LOGGER = Logger.getLogger(GroupDao.class);

    protected static Role getRoleFromResultSet(ResultSet resultSet) {
        try {
            return Role.builder()
                    .id(resultSet.getInt(1))
                    .right_ping(resultSet.getBoolean(2))
                    .right_edit(resultSet.getBoolean(3))
                    .right_to_view(resultSet.getBoolean(4))
                    .right_admin(resultSet.getBoolean(5))
                    .build();
        } catch (SQLException e) {
            LOGGER.error("Role creation error");
        }
        return null;
    }

    protected static Warning getWarningFromResultSet(ResultSet resultSet) {
        try {
            return Warning.builder()
                    .id(resultSet.getInt(1))
                    .id_group(resultSet.getInt(2))
                    .id_member(resultSet.getInt(3))
                    .id_cautioning(resultSet.getInt(4))
                    .cause(resultSet.getString(5))
                    .date(resultSet.getDate(6))
                    .deadline(resultSet.getInt(7))
                    .build();
        } catch (SQLException e) {
            LOGGER.error("Warning creation error");
        }
        return null;
    }

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
                INSERT INTO members_in_groups (id_group, id_member, id_role) VALUES (?, ?, ?)  ON CONFLICT (id_group, id_member) DO NOTHING;
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

    public static ArrayList<Role> getAllRole() throws IOException, SQLException {
        ArrayList<Role> result = new ArrayList<>();
        String SQL = """
                SELECT * FROM roles
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getRoleFromResultSet(resultSet));
                }
            }
        }
        return result;
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

    public static Boolean editGroup(Integer id_group, String title) throws IOException, SQLException {
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
        //deleteAllMembers(id_group);
        //deleteAllWarningsInGroup(id_group);
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

    public static Boolean addWarning(Integer id_member, Integer id_group, Integer id_cautioning, String cause, Integer deadline) throws IOException, SQLException {
        String SQL = """
                INSERT INTO warnings (id_group, id_member, id_cautioning, cause, date, deadline) VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_member);
            preparedStatement.setInt(3, id_cautioning);
            preparedStatement.setString(4, cause);
            preparedStatement.setDate(5, new Date(System.currentTimeMillis()));
            preparedStatement.setInt(6, deadline);
            try {
                return stopWarning(id_group, id_member) && preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                startWarning(id_group, id_member);
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
                return preparedStatement.executeUpdate() != 0 && startWarning(id_group, id_member);
            } catch (SQLException e) {
                return false;
            }
        }
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

    public static Boolean startWarning(Integer id_group, Integer id_member) throws IOException, SQLException {
        String SQL = """
                UPDATE warnings SET date = ?
                WHERE id_group = ? and id_member = ? and id =
                    (select MAX(id)
                    from warnings
                    where id_group = ? and id_member = ?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setDate(1, new Date(System.currentTimeMillis()));
            preparedStatement.setInt(2, id_group);
            preparedStatement.setInt(3, id_member);
            preparedStatement.setInt(4, id_group);
            preparedStatement.setInt(5, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean stopWarning(Integer id_group, Integer id_member) throws IOException, SQLException {
        String SQL = """
                UPDATE warnings SET date = null
                WHERE id_group = ? and id_member = ? and id =
                    (select MAX(id)
                    from warnings
                    where id_group = ? and id_member = ?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_member);
            preparedStatement.setInt(3, id_group);
            preparedStatement.setInt(4, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static ArrayList<Warning> getAllWarnings() throws IOException, SQLException {
        ArrayList<Warning> result = new ArrayList<>();
        String SQL = """
                SELECT * FROM warnings
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getWarningFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

    public static boolean CheckWarning() {
        try {
            ArrayList<Warning> result = getAllWarnings();
            for (Warning object : result) {
                if (object.getDate() != null) {
                    if (object.getDate().before(new Date(System.currentTimeMillis() + object.getDeadline() * 86400000))) {
                        deleteWarning(object.getId_group(), object.getId_member());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static ArrayList<Member> getAllMemberInGroup(Integer id_group) throws IOException, SQLException {
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                SELECT members.id, first_name, last_name, user_name,
                id_role, right_to_view, right_ping, right_edit, right_admin,
                    (select count(id)
                    from warnings
                    where members.id = warnings.id_member
                    and id_group=?) as number_of_warning
                FROM members, members_in_group, roles
                WHERE members.id=members_in_group.id_member
                and roles.id=members_in_group.id_role and id_group=?
                """;

        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setInt(1, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(MemberDao.getMemberExtendedFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

    public static ArrayList<Member> getMemberInGroupInChat(Integer id_group, Integer id_chat) throws IOException, SQLException {
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                SELECT members.id, first_name, last_name, user_name,
                FROM members, members_in_group, members_in_chat,
                WHERE members.id=members_in_chat.id_member
                and members.id=members_in_group.id_member
                and roles.id=members_in_group.id_role and id_group=? and id_chat=?""";
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(MemberDao.getMemberFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

}
