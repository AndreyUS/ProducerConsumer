package com.andrewusanin.dao;

import com.andrewusanin.data_migration.Status;
import com.andrewusanin.db.DatabaseConnection;
import com.andrewusanin.pojo.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private DatabaseConnection database;

    private UserDaoImpl() { }

    private UserDaoImpl(DatabaseConnection database) {
        this.database = database;
    }

    public static UserDaoImpl newInstance(DatabaseConnection database) {
        return new UserDaoImpl(database);
    }

    private synchronized List<User> convertResultToList(ResultSet resultSet) {
        final List<User> users = new ArrayList<User>();
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                final int idIndex =  resultSet.findColumn("id");
                final int nameIndex = resultSet.findColumn("name");
                while (resultSet.next()) {
                    final int id = resultSet.getInt(idIndex);
                    final String name = resultSet.getString(nameIndex);
                    users.add(new User(id, name));
                }
                resultSet.close();
                return users;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void addUser(User user) {
        final String query = String.format("INSERT INTO user (id, name) values(%s, \"%s\")", user.getId(), user.getName());
        database.insert(query);
    }

    public List<User> getAllUsers() {
        final ResultSet resultSet = database.query("SELECT * FROM user");
        return convertResultToList(resultSet);
    }

    public List<User> getUsers(int amount) {
        final ResultSet resultSet = database.query("SELECT * FROM user WHERE status IS NULL LIMIT " + amount);
        return convertResultToList(resultSet);
    }

    public boolean updateStatus(final int userId, Status status) {
        return  database.insert("UPDATE user SET status = \'" + status.toString() + "\' WHERE ID = " + userId);
    }
}
