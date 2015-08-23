package com.andrewusanin.service;

import com.andrewusanin.pojo.User;

import java.util.List;

public interface UserService {

    void addUser(User user);
    List<User> getAllUsers();

}
