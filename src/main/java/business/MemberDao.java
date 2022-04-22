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

    private static Member getMemberFromResultSet(ResultSet resultSet)
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

    private static Member getMemberWithTheRoleFromResultSet(ResultSet resultSet)
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
                    .build();
        }
        catch (SQLException e){
            LOGGER.error("Member creation error");
        }
        return null;
    }

    public static ArrayList<Member> getAllMemberInChat(Integer id_chat) throws IOException, SQLException {
        ArrayList<Member> result = new ArrayList<>();
        String SQL_ALL_MEMBERS_IN_CHAT = """
                 SELECT id, first_name, last_name, user_name FROM members,
                 members_in_chat WHERE members.id=members_in_chat.id_member and id_chat=?""";
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ALL_MEMBERS_IN_CHAT)){
            preparedStatement.setInt(1, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(getMemberFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

    public static ArrayList<Member> getAllMemberInGroup(Integer id_group) throws IOException, SQLException {
        ArrayList<Member> result = new ArrayList<>();
        String SQL_ALL_MEMBERS_IN_GROUP = """
                SELECT members.id, first_name, last_name, user_name,
                id_role, right_to_view, right_ping, right_edit, right_admin
                FROM members, members_in_group, roles
                WHERE members.id=members_in_group.id_member
                and roles.id=members_in_group.id_role and id_group=?""";

        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_ALL_MEMBERS_IN_GROUP)){
            preparedStatement.setInt(1, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(getMemberWithTheRoleFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

    public static ArrayList<Member> getMemberInGroupInChat(Integer id_group, Integer id_chat) throws IOException, SQLException {
        ArrayList<Member> result = new ArrayList<>();
        String SQL_MEMBERS_IN_GROUP_IN_CHAT = """
                SELECT members.id, first_name, last_name, user_name,
                id_role, right_to_view, right_ping, right_edit, right_admin
                FROM members, members_in_group, members_in_chat, roles
                WHERE members.id=members_in_chat.id_member
                and members.id=members_in_group.id_member
                and roles.id=members_in_group.id_role and id_group=? and id_chat=?""";
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_MEMBERS_IN_GROUP_IN_CHAT)){
            preparedStatement.setInt(1, id_group);
            preparedStatement.setInt(2, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(getMemberWithTheRoleFromResultSet(resultSet));
                }
            }
        }
        return result;
    }
}
