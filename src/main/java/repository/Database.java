package repository;

import repository.dao.RoleDao;
import util.ConnectionPool.ConnectionPool;

import java.io.IOException;
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
                  "id" bigint PRIMARY KEY,
                  "title" char varying(150) NOT NULL,
                  "ping" boolean NOT NULL DEFAULT false
                );
                                
                CREATE TABLE "members_in_chat" (
                  "id_chat" bigint,
                  "id_member" bigint,
                  PRIMARY KEY ("id_chat", "id_member")
                );
                                
                CREATE TABLE "groups" (
                  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                  "title" char varying(150) NOT NULL
                );
                                
                CREATE TABLE "roles" (
                  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                  "title" char varying(150) NOT NULL,
                  "right_ping" boolean NOT NULL,
                  "right_edit" boolean NOT NULL,
                  "right_to_view" boolean NOT NULL,
                  "right_admin" boolean NOT NULL
                );
                                
                CREATE TABLE "members_in_group" (
                  "id_group" integer,
                  "id_member" bigint,
                  "id_role" integer,
                  PRIMARY KEY ("id_group", "id_member")
                );
                             
                CREATE TABLE "observers_in_group" (
                  "id_group" integer,
                  "id_member" id_member,
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
                  "data_create" date NOT NULL,
                  PRIMARY KEY ("id", "id_category")
                );
                                
                CREATE TABLE "warnings" (
                  "id" integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                  "id_group" integer NOT NULL,
                  "id_member" integer NOT NULL,
                  "id_cautioning" integer NOT NULL,
                  "cause" char varying(150) NOT NULL,
                  "date" date,
                  "deadline" integer NOT NULL
                );
                                
                CREATE TABLE "resource_chat" (
                  "id" bigint PRIMARY KEY
                );
                                
                ALTER TABLE "members_in_chat" ADD FOREIGN KEY ("id_chat") REFERENCES "chats" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "members_in_chat" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "members_in_group" ADD FOREIGN KEY ("id_group") REFERENCES "groups" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "members_in_group" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "members_in_group" ADD FOREIGN KEY ("id_role") REFERENCES "roles" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "observers_in_group" ADD FOREIGN KEY ("id_group") REFERENCES "groups" ("id");
                                
                ALTER TABLE "observers_in_group" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id");
                                
                ALTER TABLE "categories" ADD FOREIGN KEY ("id_group") REFERENCES "groups" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "files" ADD FOREIGN KEY ("id_category") REFERENCES "categories" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "warnings" ADD FOREIGN KEY ("id_group") REFERENCES "groups" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "warnings" ADD FOREIGN KEY ("id_member") REFERENCES "members" ("id") ON DELETE CASCADE;
                                
                ALTER TABLE "warnings" ADD FOREIGN KEY ("id_cautioning") REFERENCES "members" ("id") ON DELETE CASCADE;
                """;
        try (Connection connection = connector.CreateConnect();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            try {
                return preparedStatement.executeUpdate() != 0;
            } catch (SQLException e) {
                return false;
            }
        }
    }
    public static Boolean createAllRoles(ConnectionPool connector) {
        try {
            RoleDao roleDao = new RoleDao();
            roleDao.addRole("Участник", false, false, true, false, connector);
            roleDao.addRole("Напоминатель", true, false, true, false, connector);
            roleDao.addRole("Модератор", true, false, true, true, connector);
            roleDao.addRole("Редактор", false, true, true, false, connector);
            roleDao.addRole("Админ", true, true, true, true, connector);
            roleDao.addRole("Создатель", true, true, true, true, connector);
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
        }
    }
}
