package repository.dao;

import kotlin.Pair;
import repository.domain.Member;
import repository.domain.Role;
import util.ConnectionPool.ConnectionPool;
//import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MemberDao {
    //private final Logger LOGGER = Logger.getLogger(MemberDao.class);

    protected Member getMemberFromResultSet(ResultSet resultSet) {
        try {
            return Member.builder()
                    .id(resultSet.getLong(1))
                    .first_name(resultSet.getString(2))
                    .last_name(resultSet.getString(3))
                    .user_name(resultSet.getString(4))
                    .build();
        }
        catch (SQLException e){
            //LOGGER.error("Member creation error");
        }
        return null;
    }

    protected Member getMemberExtendedFromResultSet(ResultSet resultSet) {
        try {
            return Member.builder()
                    .id(resultSet.getLong(1))
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
            //LOGGER.error("Member creation error");
        }
        return null;
    }

    public Boolean addMember(Long id, String first_name, String last_name, String user_name, ConnectionPool connector) {
        String SQL = """
                INSERT INTO members (id, first_name, last_name, user_name) VALUES (?, ?, ?, ?) ON CONFLICT (id) DO NOTHING;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, first_name);
            preparedStatement.setString(3, last_name);
            preparedStatement.setString(4, user_name);
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

    public Boolean editMember(Long id, String first_name, String last_name, String user_name, ConnectionPool connector) {
        String SQL = """
                UPDATE members SET first_name = ?, last_name = ?, user_name = ? WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(4, id);
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, last_name);
            preparedStatement.setString(3, user_name);
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

    public Boolean deleteMember(Long id, ConnectionPool connector) {
        String SQL = """
                DELETE FROM members WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id);
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

    public ArrayList<Pair<String,String>> getAllGroups(Long id_member, ConnectionPool connector){
        ArrayList<Pair<String,String>> result = new ArrayList<>();
        String SQL = """
                select groups.id, groups.title
                from members, members_in_group, groups
                where members.id=members_in_group.id_member and
                groups.id = members_in_group.id_group
                and members.id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setLong(1, id_member);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(new Pair<String,String>(resultSet.getString(1), resultSet.getString(2)));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }

    public ArrayList<Pair<String,String>> getAllChats(Long id_member, ConnectionPool connector){
        ArrayList<Pair<String,String>> result = new ArrayList<>();
        String SQL = """
                select chats.id, chats.title
                from members, members_in_chat, chats
                where members.id=members_in_chat.id_member and
                chats.id = members_in_chat.id_chat
                and members.id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setLong(1, id_member);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(new Pair<String,String>(resultSet.getString(1), resultSet.getString(2)));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }


   //?????????? ???????????????? ?? ?????????? ?????????????? ???????? ???????? ????????????????????????




}
