package com.andrewusanin.data_migration;

import com.andrewusanin.pojo.User;
import com.andrewusanin.service.UserService;

import java.util.concurrent.BlockingQueue;

/**
 * This class take user model from queue and writes into database
 */
public class Consumer implements Runnable {

    private boolean exit;
    private int id;
    private BlockingQueue<User> sharedQueue;
    private UserService userService;

    private Consumer() { }

    private Consumer(int id, BlockingQueue<User> sharedQueue, UserService userService) {
        this.id = id;
        this.sharedQueue = sharedQueue;
        this.userService = userService;
    }

    public static Consumer newInstance(int id, BlockingQueue<User> sharedQueue, UserService userService) {
        return new Consumer(id, sharedQueue, userService);
    }

    public int getId() {
        return id;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void run() {
        while (!exit) {
            try {
                if (sharedQueue.size() > 0) {
                    final User user = sharedQueue.take();
                    System.out.println("Consumer : " + id + " take user " + user.getId());
                    userService.addUser(user);
                    System.out.println("User with id - " + user.getId() + " was added by consumer " + id);
                }
            }  catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Consumer " + id + " will finish work." );
    }

}
