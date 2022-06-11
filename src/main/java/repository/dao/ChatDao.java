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

public class ChatDao {
    private final Logger LOGGER = Logger.getLogger(ChatDao.class);

    public Boolean addMember(Integer id_member, Integer id_chat, JdbcConnection jdbcConnection) throws IOException, SQLException {
        String SQL = """
                INSERT INTO members_in_chat (id_chat, id_member) VALUES (?, ?) ON CONFLICT (id_chat, id_member) DO NOTHING;
                """;
        try (Connection connection = jdbcConnection.CreateConnect();
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

    public Boolean deleteMember(Integer id_member, Integer id_chat, JdbcConnection jdbcConnection) throws IOException, SQLException {
        String SQL = """
                DELETE FROM members_in_chat WHERE id_chat = ? and id_member = ?;
                """;
        try (Connection connection = jdbcConnection.CreateConnect();
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

    public Boolean deleteAllMembers(Integer id_chat, JdbcConnection jdbcConnection) throws IOException, SQLException {
        String SQL = """
                DELETE FROM members_in_chat WHERE id_chat = ?;
                """;
        try (Connection connection = jdbcConnection.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_chat);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public Boolean addChat(Integer id, String title, JdbcConnection jdbcConnection) throws IOException, SQLException {
        String SQL = """
                INSERT INTO chats (id, title) VALUES (?, ?) ON CONFLICT (id) DO NOTHING;
                """;
        try (Connection connection = jdbcConnection.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, title);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }

    }

    public Boolean editChat(Integer id_chat, String title, Boolean ping, JdbcConnection jdbcConnection) throws IOException, SQLException {
        String SQL = """
                UPDATE chats SET title = ?, ping = ? WHERE id = ?;
                """;
        try (Connection connection = jdbcConnection.CreateConnect();
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

    public Boolean deleteChat(Integer id_chat, JdbcConnection jdbcConnection) throws IOException, SQLException {
        String SQL = """
                DELETE FROM chats WHERE id = ?;
                """;
        try (Connection connection = jdbcConnection.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_chat);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public ArrayList<Member> getAllMemberInChat(Integer id_chat, JdbcConnection jdbcConnection) throws IOException, SQLException {
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                SELECT members.id, first_name, last_name, user_name
                FROM members, members_in_chat
                WHERE members.id=members_in_chat.id_member
                and id_chat=?
                """;

        try (Connection connection = jdbcConnection.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setInt(1, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(new MemberDao().getMemberFromResultSet(resultSet));
                }
            }
        }
        return result;
    }

}

