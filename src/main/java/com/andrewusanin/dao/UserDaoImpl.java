package com.andrewusanin.dao;

import com.andrewusanin.db.DatabaseConnection;
import com.andrewusanin.pojo.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private List<User> convertResultToList(ResultSet resultSet) {
        final List<User> users = new ArrayList<User>();
        try {
            while (resultSet.next()) {
                final int id = resultSet.getInt("id");
                final String name = resultSet.getString("name");
                users.add(new User(id, name));
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addUser(User user) {
        final String query = String.format("INSERT INTO user (id, name) values(%s, \"%s\")", user.getId(), user.getName());
        database.insert(query);
    }

    public List<User> getAllUsers() {
        final ResultSet resultSet = database.query("SELECT * FROM user");
        return convertResultToList(resultSet);
    }
}
