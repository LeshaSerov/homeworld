package repository.dao;

//import org.apache.log4j.Logger;
import repository.domain.Category;
import repository.domain.File;
import util.ConnectionPool.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;

public class FileInGroupDao {
    //private final Logger LOGGER = Logger.getLogger(FileInGroupDao.class);

    protected File getFileFromResultSet(ResultSet resultSet) {
        try {
            return File.builder()
                    .id(resultSet.getInt(1))
                    .title(resultSet.getString(2))
                    .data_create(resultSet.getTimestamp(3))
                    .nameMember(resultSet.getString(4))
                    .build();
        } catch (SQLException e) {
            //LOGGER.error("Role creation error");
        }
        return null;
    }

    protected File getFileExtendedFromResultSet(ResultSet resultSet) {
        try {
            return File.builder()
                    .id(resultSet.getInt(1))
                    .title(resultSet.getString(2))
                    .data_create(resultSet.getTimestamp(3))
                    .nameMember(resultSet.getString(4))
                    .titleCategory(resultSet.getString(5))
                    .build();
        } catch (SQLException e) {
            //LOGGER.error("Role creation error");
        }
        return null;
    }

    protected Category getCategoryFromResultSet(ResultSet resultSet) {
        try {
            return Category.builder()
                    .id(resultSet.getInt(1))
                    .id_group(resultSet.getInt(2))
                    .title(resultSet.getString(3))
                    .build();
        } catch (SQLException e) {
            //LOGGER.error("Warning creation error");
        }
        return null;
    }

    public Boolean addFile(Integer id, Integer id_category, String title, Long id_member, ConnectionPool connector) {
        String SQL = """
                INSERT INTO files (id, id_category, title, data_create, id_member) VALUES (?, ?, ?, ?, ?) ON CONFLICT (id, id_category) DO NOTHING;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id_category);
            preparedStatement.setString(3, title);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setLong(5, id_member);
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

    public Boolean editFile(Integer id, String title, ConnectionPool connector) {
        String SQL = """
                UPDATE files SET title = ? WHERE id = ?;
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

    public Boolean deleteFile(Integer id, ConnectionPool connector) {
        String SQL = """
                DELETE FROM files WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
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

    public Boolean deleteAllFiles(Integer id_category, ConnectionPool connector) {
        String SQL = """
                DELETE FROM files WHERE id_category = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_category);
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

    public Boolean addCategory(Integer id_group, String title, ConnectionPool connector) {
        String SQL = """
                INSERT INTO categories (id_group, title) VALUES (?, ?);
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setString(2, title);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
        catch (Exception exception){
            return false;
        }
    }

    public Boolean editCategory(Integer id, String title, ConnectionPool connector) {
        String SQL = """
                UPDATE categories SET title = ? WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(2, id);
            preparedStatement.setString(1, title);
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

    public Boolean deleteCategory(Integer id, ConnectionPool connector) {
        String SQL = """
                DELETE FROM categories WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
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

    public Boolean deleteAllCategories(Integer id_group, ConnectionPool connector) {
        String SQL = """
                DELETE FROM categories WHERE id_group = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
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

    public ArrayList<Category> getAllCategories(Integer id_group, ConnectionPool connector) {
        ArrayList<Category> result = new ArrayList<>();
        String SQL = """
                SELECT * FROM categories WHERE id_group = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getCategoryFromResultSet(resultSet));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }

    public ArrayList<File> getAllFiles(Integer id_group, ConnectionPool connector) {
        ArrayList<File> result = new ArrayList<>();
        String SQL = """
                SELECT DISTINCT
                	files.id,
                    files.title,
                    files.data_create,
                    members.first_name,
                    categories.title
                FROM
                	files
                	
                	JOIN categories
                	ON files.id_category = categories.id
                	
                	JOIN groups
                	ON categories.id_group = groups.id
                	
                	JOIN members
                	ON files.id_member = members.id
                	
                WHERE groups.id = ?""";
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getFileExtendedFromResultSet(resultSet));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }

    public ArrayList<File> getAllFilesInCategory(Integer id_category, ConnectionPool connector) {
        ArrayList<File> result = new ArrayList<>();
        String SQL = """
                SELECT DISTINCT
                    files.id,
                    files.title,
                    files.data_create,
                    members.first_name
                FROM
                    files
                    
                    JOIN categories
                    ON files.id_category = categories.id
                    
                    JOIN members
                    ON files.id_member = members.id
                    
                WHERE categories.id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_category);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getFileFromResultSet(resultSet));
                }
            }
        }
        catch (Exception exception){
            return null;
        }
        return result;
    }

    public Long getIdResourceChat(ConnectionPool connector) {
        String SQL = """
            select id
            from resource_chat
            LIMIT 1;
            """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        }
        catch (Exception exception){
            return null;
        }
    }

    public String getTitleFile(Integer id, ConnectionPool connector) {
        String SQL = """
                SELECT DISTINCT
                    title
                FROM
                    files
                  WHERE id = ?
                  LIMIT 1;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getString(1);
            }
        }
        catch (Exception exception){
            return null;
        }
    }
}
