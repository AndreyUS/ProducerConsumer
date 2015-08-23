package com.andrewusanin.service;

import com.andrewusanin.dao.UserDao;
import com.andrewusanin.dao.UserDaoImpl;
import com.andrewusanin.db.DatabaseConnection;
import com.andrewusanin.pojo.User;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserDao userDao;

    private UserServiceImpl() { }

    private UserServiceImpl(DatabaseConnection databaseConnection) {
        this.userDao = UserDaoImpl.newInstance(databaseConnection);
    }

    public static UserServiceImpl newInstance(DatabaseConnection databaseConnection) {
        return new UserServiceImpl(databaseConnection);
    }

    public void addUser(User user) {
        userDao.addUser(user);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }
}
