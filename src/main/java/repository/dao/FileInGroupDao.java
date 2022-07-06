package repository.dao;

//import org.apache.log4j.Logger;
import repository.domain.Category;
import repository.domain.File;
import util.ConnectionPool.ConnectionPool;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class FileInGroupDao {
    //private final Logger LOGGER = Logger.getLogger(FileInGroupDao.class);

    protected File getFileFromResultSet(ResultSet resultSet) {
        try {
            return File.builder()
                    .id(resultSet.getInt(1))
                    .id_category(resultSet.getInt(2))
                    .title(resultSet.getString(3))
                    .data_create(resultSet.getTimestamp(4))
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

    public Boolean addFile(Integer id, Integer id_category, String title, ConnectionPool connector) {
        String SQL = """
                INSERT INTO files (id, id_category, title, data_create) VALUES (?, ?, ?, ?) ON CONFLICT (id, id_category) DO NOTHING;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id_category);
            preparedStatement.setString(3, title);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
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

    public Boolean editFile(Integer id, Integer id_category, String title, ConnectionPool connector) {
        String SQL = """
                UPDATE files SET id_category = ?, title = ? WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(3, id);
            preparedStatement.setInt(1, id_category);
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

    public Integer addCategory(Integer id_group, String title, ConnectionPool connector) {
        String SQL = """
                INSERT INTO categories (id_group, title) VALUES (?, ?) RETURNING id;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setString(2, title);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                return -1;
            }
        }
        catch (Exception exception){
            return null;
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
                SELECT files.id, files.id_category, files.title, files.data_create FROM files, categories, groups WHERE categories.id_group=groups.id and groups.id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
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

    public ArrayList<File> getAllFilesInCategory(Integer id_category, ConnectionPool connector) {
        ArrayList<File> result = new ArrayList<>();
        String SQL = """
                SELECT * FROM files WHERE id_category = ?
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

}
