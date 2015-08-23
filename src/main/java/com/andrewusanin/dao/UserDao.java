package com.andrewusanin.dao;

import com.andrewusanin.pojo.User;

import java.util.List;

/**
 * This class writes and reads data from database
 */
public interface UserDao {
    /**
     * This method writes User model to database
     * @param user
     */
    void addUser(User user);

    /**
     * This method return list of users that contain in database
     * @return
     */
    List<User> getAllUsers();
}
