package com.andrewusanin.dao;

import com.andrewusanin.data_migration.Status;
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
     * @return List of {@link com.andrewusanin.pojo.User}
     */
    List<User> getAllUsers();

    /**
     * Return a certain amount of {@link @com.andrewusanin.pojo.User} from database
     * @param amount of {@link com.andrewusanin.pojo.User}
     * @return List of {@link com.andrewusanin.pojo.User}
     */
    List<User> getUsers(int amount);

    /**
     * Update status for certain {@link com.andrewusanin.pojo.User}
     * @param userId which need update status
     * @param status {@link com.andrewusanin.data_migration.Status}
     * @return true if successful
     */
    boolean updateStatus(int userId, Status status);
}
