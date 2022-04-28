package business;

import domain.Group;
import domain.Member;
import domain.Role;
import jdbcconnector.JdbcConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static business.GroupDao.getGroupFromResultSet;
import static business.MemberDao.*;

public class temporarily {



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
                    result.add(getMemberExtendedFromResultSet(resultSet));
                }
            }
        }
        return result;
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

