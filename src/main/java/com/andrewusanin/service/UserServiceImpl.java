package com.andrewusanin.service;

import com.andrewusanin.dao.UserDao;
import com.andrewusanin.dao.UserDaoImpl;
import com.andrewusanin.data_migration.Status;
import com.andrewusanin.db.DatabaseConnection;
import com.andrewusanin.pojo.User;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserDao userDao;

    private UserServiceImpl() { }

    private UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public static UserService newInstance(final UserDao userDao) {
        return new UserServiceImpl(userDao);
    }

    public void addUser(User user) {
        userDao.addUser(user);
    }

    public List<User> getUsers(int amount) {
        return userDao.getUsers(amount);
    }

    public boolean updateStatus(final int userId, final Status status) {
        return userDao.updateStatus(userId, status);
    }
}
