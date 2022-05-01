package repository.dao;

import repository.domain.Member;
import util.jdbcconnector.JdbcConnection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static repository.dao.MemberDao.getMemberExtendedFromResultSet;

public class ChatDao {
    private static final Logger LOGGER = Logger.getLogger(ChatDao.class);

    public static Boolean addMember(Integer id_member, Integer id_chat) throws IOException, SQLException {
        String SQL = """
                INSERT INTO members_in_chats (id_chat, id_member) VALUES (?, ?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_chat);
            preparedStatement.setInt(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean deleteMember(Integer id_member, Integer id_chat) throws IOException, SQLException {
        String SQL = """
                DELETE FROM members_in_chats WHERE id_chat = ? and id_member = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_chat);
            preparedStatement.setInt(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean deleteAllMembers(Integer id_chat) throws IOException, SQLException {
        String SQL = """
                DELETE members_in_chats WHERE id_chat = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_chat);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean addChat(String title) throws IOException, SQLException {
        String SQL = """
                INSERT INTO chats (title. ping) VALUES (?);
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setBoolean(2, false);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static Boolean editChat(Integer id_chat, String title, Boolean ping) throws IOException, SQLException {
        String SQL = """
                UPDATE chats SET title = ? and ping = ? WHERE id = ?;
                """;
        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setBoolean(2, ping);
            preparedStatement.setInt(3, id_chat);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static ArrayList<Member> getAllMemberInChat(Integer id_chat) throws IOException, SQLException {
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                SELECT members.id, first_name, last_name, user_name,
                FROM members, members_in_chats
                WHERE members.id=members_in_chats.id_member
                and id_chat=?
                """;

        try (Connection connection = new JdbcConnection().CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setInt(1, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(getMemberExtendedFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

}

