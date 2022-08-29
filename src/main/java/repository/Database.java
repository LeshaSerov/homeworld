package repository;

import repository.dao.RoleDao;
import util.ConnectionPool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    public static Boolean recreateDatabase(ConnectionPool connector) {
        String SQL = """
                DROP SCHEMA public CASCADE;
                CREATE SCHEMA public;
                CREATE TABLE "members" (
                    "id" bigint PRIMARY KEY,
                    "first_name" char varying(90) NOT NULL,
                    "last_name" char varying(90),
                    "user_name" char varying(90)
                );
                                
                CREATE TABLE "chats" (
                    "id" int GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    "id_telegram" bigint,
                    "title" char varying(150) NOT NULL
                );
                                
                CREATE TABLE "members_in_chat" (
                    "id_chat" bigint,
                    "id_member" bigint,
                    PRIMARY KEY ("id_chat", "id_member")
                );
                                
                CREATE TABLE "projects" (
                    "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    "id_creator" integer NOT NULL,
                    "title" char varying(150) NOT NULL,
                    "id_chat" bigint NOT NULL
                );
                                
                CREATE TABLE "groups" (
                    "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    "title" char varying(150) NOT NULL,
                    "id_project" integer NOT NULL
                );
                                
                CREATE TABLE "roles" (
                    "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    "title" char varying(150) NOT NULL,
                    "right_to_view" boolean NOT NULL,
                    "right_ping" boolean NOT NULL,
                    "right_edit" boolean NOT NULL,
                    "right_admin" boolean NOT NULL,
                    "right_creator" boolean NOT NULL
                );
                                
                CREATE TABLE "members_in_group" (
                    "id_group" integer,
                    "id_member" bigint,
                    "id_role" integer,
                    PRIMARY KEY ("id_group", "id_member")
                );
                                
                CREATE TABLE "categories" (
                    "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    "id_group" integer,
                    "title" char varying(150) NOT NULL
                );
                                
                CREATE TABLE "files" (
                    "id" integer,
                    "id_category" integer,
                    "title" char varying(150) NOT NULL,
                    "data_create" timestamp without time zone NOT NULL,
                    "id_member" bigint NOT NULL
                    PRIMARY KEY ("id", "id_category")
                );
                                
                CREATE TABLE "warnings" (
                    "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    "id_member" bigint NOT NULL,
                    "id_cautioning" bigint NOT NULL,
                    "id_group" integer NOT NULL,
                    "cause" char varying(150) NOT NULL,
                    "date" timestamp without time zone,
                    "deadline" integer NOT NULL
                );
                                
                CREATE TABLE "resource_chat" (
                    "id" bigint PRIMARY KEY
                );
                                
                ALTER TABLE "members_in_chat" ADD FOREIGN KEY ("id_chat") REFERENCES "chats" ("id") ON DELETE CASCADE;
                ALTER TABLE "members_in_chat" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id") ON DELETE CASCADE;
                ALTER TABLE "projects" ADD FOREIGN KEY ("id_chat") REFERENCES "chats" ("id") ON DELETE CASCADE;
                ALTER TABLE "groups" ADD FOREIGN KEY ("id_project") REFERENCES "projects" ("id") ON DELETE CASCADE;
                ALTER TABLE "members_in_group" ADD FOREIGN KEY ("id_group") REFERENCES "groups" ("id") ON DELETE CASCADE;
                ALTER TABLE "members_in_group" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id") ON DELETE CASCADE;
                ALTER TABLE "members_in_group" ADD FOREIGN KEY ("id_role") REFERENCES "roles" ("id") ON DELETE CASCADE;
                ALTER TABLE "categories" ADD FOREIGN KEY ("id_group") REFERENCES "groups" ("id") ON DELETE CASCADE;
                ALTER TABLE "files" ADD FOREIGN KEY ("id_category") REFERENCES "categories" ("id") ON DELETE CASCADE;
                ALTER TABLE "files" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id") ON DELETE CASCADE;
                ALTER TABLE "warnings" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id") ON DELETE CASCADE;
                ALTER TABLE "warnings" ADD FOREIGN KEY ("id_cautioning") REFERENCES "members" ("id") ON DELETE CASCADE;
                ALTER TABLE "warnings" ADD FOREIGN KEY ("id_group") REFERENCES "groups" ("id") ON DELETE CASCADE;
                                
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            try {
                return preparedStatement.executeUpdate() == 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    public static Boolean createAllRoles(ConnectionPool connector) {
        try {
            RoleDao roleDao = new RoleDao();
            roleDao.addRole("Нарушитель", false, false, false, false, false, connector);
            roleDao.addRole("Участник", true, false, false, false, false, connector);
            roleDao.addRole("Напоминатель", true, true, false, false, false, connector);
            roleDao.addRole("Модератор", true, true, false, true, false, connector);
            roleDao.addRole("Редактор", true, false, true, false, false, connector);
            roleDao.addRole("Админ", true, true, true, true, false, connector);
            roleDao.addRole("Создатель", true, true, true, true, true, connector);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean createResourceChat(ConnectionPool connector) {
        String SQL = """                
                Insert into resource_chat(id) Values(-1001207927836) ON CONFLICT (id) DO NOTHING;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }
}
