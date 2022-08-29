package repository.dao;

import repository.domain.Member;
import util.ConnectionPool.ConnectionPool;
//import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatDao {
    //private final Logger LOGGER = Logger.getLogger(ChatDao.class);

    public Boolean addMember(Long id_member, Long id_chat, ConnectionPool connector) {
        String SQL = """
                INSERT INTO\s
                	members_in_chat (id_chat, id_member)\s
                SELECT
                	id,
                	?
                FROM\s
                	chats\s
                WHERE
                	id_telegram = ?
                ON CONFLICT (id_chat, id_member) DO NOTHING
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_member);
            preparedStatement.setLong(2, id_chat);
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

    public Boolean deleteMember(Long id_member, Long id_chat,  ConnectionPool connector) {
        String SQL = """
                DELETE FROM
                	members_in_chat
                WHERE
                	id_chat =\s
                	(SELECT id FROM chats WHERE id_telegram = ?	LIMIT 1)
                	and id_member = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_chat);
            preparedStatement.setLong(2, id_member);
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

    public Boolean deleteAllMembers(Long id_chat, ConnectionPool connector) {
        String SQL = """
                  DELETE FROM
                    members_in_chat
                  WHERE
                    id_chat = (
                        SELECT
                            id
                        FROM
                            chats
                        WHERE
                            id_telegram = ?
                    );
                  
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_chat);
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

    public Integer addChat(Long id, String title, ConnectionPool connector) {
        String SQL = """             
                INSERT INTO chats (id_telegram, title)
                	SELECT
                		?, ?
                	WHERE
                		NOT EXISTS (
                        	SELECT
                				id
                			FROM
                				chats
                			WHERE
                				id_telegram = ?)
                    RETURNING id;
                                
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, title);
            preparedStatement.setLong(3, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                return -1;
            }
        }
        catch (Exception exception){
            return -1;
        }
    }

    public Boolean editChat(Long id_chat, String title, ConnectionPool connector) {
        String SQL = """
                UPDATE chats SET id_telegram = ?, title = ? WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_chat);
            preparedStatement.setString(2, title);
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

    public Boolean deleteChat(Long id_chat, ConnectionPool connector) {
        String SQL = """
                DELETE FROM
                	chats\s
                WHERE id = (SELECT id FROM chats WHERE id_telegram = ?	LIMIT 1);
                
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_chat);
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

    public Long getIdTelegram(Integer id_group, ConnectionPool connector) {
        String SQL = """
                SELECT
                	chats.id_telegram
                FROM
                	chats
                WHERE
                	chats.id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        }
        catch (Exception exception){
            return null;
        }
    }

    public ArrayList<Member> getAllMemberInChat(Long id_chat,  ConnectionPool connector) {
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                SELECT
                    members.id,
                    first_name,
                    last_name,
                    user_name
                 FROM
                    members
                    
                    JOIN members_in_chat
                    ON members.id = members_in_chat.id_member
                 WHERE
                    id_chat=?
                """;

        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setLong(1, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(new MemberDao().getMemberFromResultSet(resultSet));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }

}

