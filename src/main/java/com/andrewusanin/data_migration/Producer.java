package com.andrewusanin.data_migration;

import com.andrewusanin.pojo.User;
import com.andrewusanin.service.UserService;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * This class reads all data from database and put it into queue.
 */
public class Producer implements Runnable {

    private boolean exit;
    private List<Consumer> consumers;
    private BlockingQueue<User> sharedQueue;
    private UserService userService;

    private Producer() { }

    private Producer(List<Consumer> consumers, BlockingQueue<User> sharedQueue, UserService userService) {
        this.consumers = consumers;
        this.sharedQueue = sharedQueue;
        this.userService = userService;
    }

    public static Producer newInstance(List<Consumer> consumers, BlockingQueue<User> sharedQueue, UserService userService) {
        return new Producer(consumers, sharedQueue, userService);
    }

    public void run() {
        while(!exit) {
            List<User> users = userService.getAllUsers();
            System.out.println("Producer got users - " + users.size());
            for (User user : users) {
                try {
                    sharedQueue.put(user);
                    System.out.println("Producer put user - " + user.getId() + " into queue");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            exit = true;
        }

        while (sharedQueue.size() > 0) {
            try {
                Thread.sleep(200);
                System.out.println("Producer waiting to end.");
            } catch (final InterruptedException e) {
                break;
            }
        }

        for (final Consumer consumer : consumers) {
            consumer.setExit(true);
            System.out.println("Producer sent exit command to consumer -" + consumer.getId());
        }
    }
}
