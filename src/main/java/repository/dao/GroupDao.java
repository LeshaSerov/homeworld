package repository.dao;

import repository.domain.Group;
import repository.domain.Member;
import repository.domain.Role;
import repository.domain.Warning;
import util.ConnectionPool.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class GroupDao {
    //private final Logger LOGGER = Logger.getLogger(GroupDao.class);

    protected Role getRoleFromResultSet(ResultSet resultSet) {
        try {
            return Role.builder()
                    .id(resultSet.getInt(1))
                    .title(resultSet.getString(2))
                    .right_to_view(resultSet.getBoolean(3))
                    .right_ping(resultSet.getBoolean(4))
                    .right_edit(resultSet.getBoolean(5))
                    .right_admin(resultSet.getBoolean(6))
                    .right_creator(resultSet.getBoolean(7))
                    .build();
        } catch (SQLException e) {
            //LOGGER.error("Role creation error");
        }
        return null;
    }

    protected Warning getWarningFromResultSet(ResultSet resultSet) {
        try {
            return Warning.builder()
                    .id(resultSet.getInt(1))
                    .id_member(resultSet.getLong(2))
                    .id_cautioning(resultSet.getLong(3))
                    .id_group(resultSet.getInt(4))
                    .cause(resultSet.getString(5))
                    .date(resultSet.getTimestamp(6))
                    .deadline(resultSet.getInt(7))
                    .build();
        } catch (SQLException e) {
            //LOGGER.error("Warning creation error");
        }
        return null;
    }

    protected Group getGroupFromResultSet(ResultSet resultSet) {
        try {
            return Group.builder()
                    .id(resultSet.getInt(1))
                    .title(resultSet.getString(2))
                    .build();
        } catch (SQLException e) {
            //LOGGER.error("Group creation error");
        }
        return null;
    }

    public String getTitleGroup(Integer id_group, ConnectionPool connector) {
        String result = null;
        String SQL = """
                SELECT
                	title
                FROM
                	groups
                	
                WHERE
                	groups.id = ?;
                """;

        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result = resultSet.getString(1);
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return result;
    }


    public Boolean addMember(Long id_member, Integer id_group, Integer id_role, ConnectionPool connector) {
        String SQL = """
                INSERT INTO members_in_group (id_group, id_member, id_role) VALUES (?, ?, ?)  ON CONFLICT (id_group, id_member) DO NOTHING;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setLong(2, id_member);
            preparedStatement.setInt(3, id_role);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public Boolean editRoleMembers(Long id_member, Integer id_group, Integer id_role, ConnectionPool connector) {
        String SQL = """
                UPDATE members_in_group SET id_role = ? WHERE id_member = ? and id_group = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_role);
            preparedStatement.setLong(2, id_member);
            preparedStatement.setInt(3, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public Boolean deleteMember(Long id_member, Integer id_group, ConnectionPool connector) {
        String SQL = """
                DELETE FROM members_in_group WHERE id_group = ? and id_member = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setLong(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0 && deleteAllWarnings(id_group, id_member, connector);
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public Boolean deleteAllMembers(Integer id_group, ConnectionPool connector) {
        String SQL = """
                DELETE members_in_groups WHERE id_group = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public Integer addGroup(String title, Integer idProject, ConnectionPool connector) {
        String SQL = """
                INSERT INTO groups (title, id_project) VALUES (?, ?) RETURNING id;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, idProject);
            try (ResultSet resultSet = preparedStatement.executeQuery();) {
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                return -1;
            }
        } catch (Exception exception) {
            return -1;
        }
    }

    public Boolean editGroup(Integer id_group, String title, ConnectionPool connector) {
        String SQL = """
                UPDATE groups SET title = ? WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public Boolean deleteGroup(Integer id_group, ConnectionPool connector) {
        String SQL = """
                DELETE FROM groups WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    //TODO: НЕ ЗНАЮ РАБОТАЕТ ЛИ
    public Boolean addWarning(Long id_member, Long id_cautioning, Integer id_group, String cause, Integer deadline, ConnectionPool connector) {
        Integer id_project = getProjectGroup(id_group, connector);
        String SQL = """
                INSERT INTO warnings (id_member, id_cautioning, id_group, cause, date, deadline) VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_member);
            preparedStatement.setLong(2, id_cautioning);
            preparedStatement.setInt(3, id_group);
            preparedStatement.setString(4, cause);
            preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setInt(6, deadline);
            try {
                Integer numbersWarnings = new GroupDao().getCountWarningsFromMemberInProject(id_member, id_project, connector);
                if (numbersWarnings == 0)
                    return (preparedStatement.executeUpdate() != 0);
                else if (numbersWarnings > 0)
                    return stopWarning(id_project, id_member, connector) && (preparedStatement.executeUpdate() != 0);
                else return false;
            } catch (SQLException e) {
                startWarning(id_project, id_member, 0, connector);
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public Boolean editWarning(Integer id_warning, String cause, ConnectionPool connector) {
        String SQL = """
                UPDATE warnings SET cause = ? WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setString(1, cause);
            preparedStatement.setInt(2, id_warning);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }

    }

    public Boolean deleteWarning(Integer id_warning, Long id_member, ConnectionPool connector) {
        Integer id_project = getProjectGroupWarning(id_warning, connector);
        String SQL = """
                DELETE FROM warnings WHERE id = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_warning);
            try {
                Integer count = new GroupDao().getCountWarningsFromMemberInProject(id_member, id_project, connector);
                if (count > 1)
                    return preparedStatement.executeUpdate() != 0 && startWarning(id_project, id_member, id_warning, connector);
                else
                    return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public Boolean deleteAllWarnings(Integer id_group, Long id_member, ConnectionPool connector) {
        String SQL = """
                DELETE FROM warnings WHERE id_group = ? and id_member = ?;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setLong(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public Boolean deleteAllWarningsInGroup(Integer id_group, ConnectionPool connector) {
        try {
            ArrayList<Warning> result = getAllWarnings(connector);
            for (Warning object : result) {
                if (Objects.equals(object.getId_group(), id_group)) {
                    new GroupDao().deleteAllWarnings(id_group, object.getId_member(), connector);
                }
            }
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private Boolean startWarning(Integer id_project, Long id_member, Integer id_warning, ConnectionPool connector) {
        String SQL = """
                UPDATE warnings SET date = ?
                WHERE id =
                    (SELECT
                        Max(warnings.id)
                    FROM
                        warnings
                            
                        JOIN members
                        ON warnings.id_member = members.id
                            
                        JOIN members_in_group
                        ON members.id = members_in_group.id_member
                            
                        JOIN groups
                        ON members_in_group.id_group = groups.id
                            
                        JOIN projects
                        ON groups.id_project = projects.id
                            
                    WHERE
                        members.id = ?
                        and projects.id = ?
                        and warnings.id != ?)
                    """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setLong(2, id_member);
            preparedStatement.setInt(3, id_project);
            preparedStatement.setInt(4, id_warning);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    private Boolean stopWarning(Integer id_project, Long id_member, ConnectionPool connector) {
        String SQL = """
                UPDATE warnings\s
                SET date = null
                WHERE id =
                    (
                	select\s
                		MAX(warnings.id)
                    from
                		warnings
                	 	
                		LEFT JOIN groups
                		ON groups.id = warnings.id_group
                		
                		LEFT JOIN projects
                		ON groups.id_project = projects.id
                    where
                		id_project = ?
                		and id_member = ?)
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_project);
            preparedStatement.setLong(2, id_member);
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    public ArrayList<Warning> getAllWarnings(ConnectionPool connector) {
        ArrayList<Warning> result = new ArrayList<>();
        String SQL = """
                SELECT * FROM warnings
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getWarningFromResultSet(resultSet));
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return result;
    }

    public Integer getCountWarningsFromMemberInProject(Long id_member, Integer id_project, ConnectionPool connector) {
        String SQL = """
                SELECT
                    Count(warnings.id)
                FROM
                    warnings
                        
                    JOIN members
                    ON warnings.id_member = members.id
                        
                    JOIN members_in_group
                    ON members.id = members_in_group.id_member
                        
                    JOIN groups
                    ON members_in_group.id_group = groups.id
                        
                    JOIN projects
                    ON groups.id_project = projects.id
                        
                WHERE
                    members.id = ? and projects.id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_member);
            preparedStatement.setInt(2, id_project);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            } catch (SQLException e) {
                return -1;
            }
        } catch (Exception exception) {
            return -1;
        }
    }

    public static Boolean CheckWarning(ConnectionPool connector) {
        try {
            ArrayList<Warning> result = new GroupDao().getAllWarnings(connector);
            for (Warning object : result) {
                if (object.getDate() != null) {
                    Timestamp deadline = new Timestamp(System.currentTimeMillis() + ((long) object.getDeadline() * 86400000));
                    if (object.getDate().after(deadline)) {
                        boolean resultDeleted = new GroupDao().deleteWarning(object.getId(), object.getId_member(), connector);
                        if (!resultDeleted)
                            return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Integer getProjectGroupWarning(Integer id_warning, ConnectionPool connector) {
        int result = -1;
        String SQL = """
                SELECT
                	projects.id
                FROM
                	warnings
                                
                	JOIN members
                	ON warnings.id_member = members.id
                                
                	JOIN members_in_group
                	ON members.id = members_in_group.id_member
                                
                	JOIN groups
                	ON members_in_group.id_group = groups.id
                                
                	JOIN projects
                	ON groups.id_project = projects.id
                                
                WHERE
                	warnings.id = ?;
                """;

        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_warning);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result = resultSet.getInt(1);
                }
            }
        } catch (Exception exception) {
            return -1;
        }
        return result;
    }

    public Integer getProjectGroup(Integer id_group, ConnectionPool connector) {
        int result = -1;
        String SQL = """
                SELECT
                	projects.id
                FROM
                	groups
                	
                	JOIN projects
                	ON groups.id_project = projects.id
                	
                WHERE
                	groups.id = ?;
                """;

        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result = resultSet.getInt(1);
                }
            }
        } catch (Exception exception) {
            return -1;
        }
        return result;
    }


    public ArrayList<Member> getAllMemberInGroup(Integer id_group, ConnectionPool connector) {
        Integer id_project = getProjectGroup(id_group, connector);
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                SELECT
                    members.id,
                    first_name,
                    last_name,
                    user_name,
                	COUNT(warnings.id)
                                
                FROM
                	members
                                
                    JOIN members_in_group
                    ON members.id = members_in_group.id_member
                	
                	JOIN groups
                	ON members_in_group.id_group = groups.id
                	
                	LEFT JOIN warnings
                	ON warnings.id_member = members.id
                	
                	LEFT JOIN projects
                	ON projects.id = groups.id_project\s
                                
                WHERE
                	projects.id = ?
                    and groups.id = ?
                GROUP BY members.id;
                """;

        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_project);
            preparedStatement.setInt(2, id_group);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new MemberDao().getMemberFromResultSet(resultSet));
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return result;
    }

    public ArrayList<Member> getAllMemberNotInGroup(Long id_member, Integer id_group, ConnectionPool connector) {
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                                 
                SELECT
                     members.id,
                     members.first_name,
                     members.last_name,
                     members.user_name
                               
                FROM
                     members
                               
                     JOIN members_in_chat
                     ON members_in_chat.id_member = members.id
                               
                     JOIN chats
                     ON chats.id = members_in_chat.id_chat
                               
                WHERE
                     members.id NOT IN
                     (
                         SELECT
                             id
                         FROM
                             members
                               
                             JOIN members_in_group
                             ON members_in_group.id_member = members.id
                               
                         WHERE
                             members_in_group.id_group = ?
                         )
                 
                 and chats.id in
                         (
                         SELECT
                             id
                         FROM
                             chats
                               
                             JOIN members_in_chat
                             ON members_in_chat.id_chat = chats.id
                               
                         WHERE
                             members_in_chat.id_member = ?
                         )
                   GROUP BY members.id;
                  \s
                 	
                 """;

        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setLong(2, id_member);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new MemberDao().getMemberBriefFromResultSet(resultSet));
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return result;
    }

    public ArrayList<Warning> getAllWarningsMemberFromProject(Long id_member, Integer id_project, ConnectionPool connector) {
        ArrayList<Warning> result = new ArrayList<>();
        String SQL = """
                SELECT DISTINCT
                	warnings.*
                FROM
                	warnings
                        
                	JOIN members
                	ON warnings.id_member = members.id
                	
                	JOIN members_in_group
                	ON members.id = members_in_group.id_member
                        
                	JOIN groups
                	ON members_in_group.id_group = groups.id
                        
                	JOIN projects
                	ON groups.id_project = projects.id
                        
                WHERE
                	warnings.id_member = ? and projects.id = ?
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, id_member);
            preparedStatement.setInt(2, id_project);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(getWarningFromResultSet(resultSet));
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return result;
    }

    //TODO: НЕ РАБОТАЕТ!!!
    public ArrayList<Member> getMemberInGroupInChat(Integer id_group, Long id_chat, ConnectionPool connector) {
        ArrayList<Member> result = new ArrayList<>();
        String SQL = """
                SELECT members.id, first_name, last_name, user_name,
                FROM members, members_in_group, members_in_chat,
                WHERE members.id=members_in_chat.id_member
                and members.id=members_in_group.id_member
                and roles.id=members_in_group.id_role and id_group=? and id_chat=?""";
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1, id_group);
            preparedStatement.setLong(2, id_chat);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new MemberDao().getMemberFromResultSet(resultSet));
                }
            }
        } catch (Exception exception) {
            return null;
        }
        return result;
    }

    public Role getMembersRole(Long idMember, Integer idGroup, ConnectionPool connector) {
        Role role = null;
        String SQL = """
                SELECT
                    id_role,
                    title,
                    right_to_view,
                    right_ping,
                    right_edit,
                    right_admin,
                    right_creator
                FROM
                    members_in_group
                    
                    JOIN roles
                    ON roles.id = members_in_group.id_role
                    
                WHERE
                    id_member = ?
                    And id_group = ?
                """;

        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setLong(1, idMember);
            preparedStatement.setInt(2, idGroup);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                role = getRoleFromResultSet(resultSet);
            }
        } catch (Exception exception) {
            return null;
        }
        return role;
    }
}
