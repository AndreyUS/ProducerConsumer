package com.andrewusanin.service;

import com.andrewusanin.data_migration.Status;
import com.andrewusanin.pojo.User;

import java.util.List;

public interface UserService {
    /**
     * Take model and write all data into database
     * @param user
     */
    void addUser(User user);

    /**
     * Take models from database
     * @param amount of models
     * @return list of models
     */
    List<User> getUsers(int amount);

    /**
     * Execute update of status for user.
     * @param userId user id which need update
     * @param status which status need set
     * @return result of execute
     */
    boolean updateStatus(int userId, Status status);
}
