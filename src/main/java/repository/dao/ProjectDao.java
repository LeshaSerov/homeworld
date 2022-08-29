package repository.dao;

import kotlin.Pair;
import repository.domain.Member;
import util.ConnectionPool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProjectDao {
    //private final Logger LOGGER = Logger.getLogger(MemberDao.class);

    public Integer addProject(Long id_creator, String title, Integer id_chat, ConnectionPool connector) {
        String SQL = """
                INSERT INTO projects (id_creator, title, id_chat) VALUES (?, ?, ?) RETURNING id;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_creator);
            preparedStatement.setString(2, title);
            preparedStatement.setLong(3, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery();){
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

    public Boolean editProject(Integer id, String title, ConnectionPool connector) {
        String SQL = """
                    UPDATE
                    	Projects
                    SET
                    	Title = ?
                    WHERE
                    	id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, id);
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

    public Boolean deleteProject(Integer id, ConnectionPool connector) {
        String SQL = """
                DELETE FROM
                	Projects
                WHERE
                	id = ?;
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

    //Выдает список проектов, в которых пользователь считается админом.
    public ArrayList<Pair<String,String>> getAllProjects(Long id_member, ConnectionPool connector){
        ArrayList<Pair<String,String>> result = new ArrayList<>();
        String SQL = """
                SELECT
                	projects.id,
                	projects.title,
                	chats.title
                FROM
                	projects
                                
                	JOIN members_in_chat
                	ON members_in_chat.id_chat = projects.id_chat
                                
                	JOIN chats
                	ON chats.id = members_in_chat.id_chat
                WHERE
                	id_member = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
            preparedStatement.setLong(1, id_member);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                {
                    result.add(new Pair<String,String>(resultSet.getString(1), resultSet.getString(2) + " | " + resultSet.getString(3)));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }

    //Выдает список проектов, которые создал пользователь
    public ArrayList<Pair<String,String>> getAllCreatedProjects(Long id_member, ConnectionPool connector){
        ArrayList<Pair<String,String>> result = new ArrayList<>();
        String SQL = """
                SELECT
                	id,
                	title
                FROM
                	projects
                	
                WHERE
                	id_creator = ?;
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

    public ArrayList<Pair<String,String>> getAllChatsUnlinkedProjects(Long id_member, ConnectionPool connector){
        ArrayList<Pair<String,String>> result = new ArrayList<>();
        String SQL = """
                select DISTINCT
                	chats.id,
                	chats.title
                from
                	chats
                	
                	JOIN members_in_chat
                	ON chats.id = members_in_chat.id_chat
                	
                	LEFT JOIN projects
                	ON chats.id = projects.id_chat
                                
                WHERE
                 	projects.id is NULL and
                	members_in_chat.id_member = ?
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

    //Метод просмотр в каких группах есть этот пользователь




}
