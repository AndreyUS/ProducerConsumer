package com.andrewusanin.data_migration;

import com.andrewusanin.pojo.User;
import com.andrewusanin.service.UserService;
import com.andrewusanin.utils.BlockingQueue;

import java.util.List;
/**
 * This class reads all data from database and put it into queue.
 */
public class Producer implements Runnable {

    private boolean exit;
    private int amountOfUsers;
    private List<Consumer> consumers;
    private BlockingQueue<User> sharedQueue;
    private UserService userService;

    private Producer() { }

    private Producer(List<Consumer> consumers, BlockingQueue<User> sharedQueue, UserService userService, int amountOfUser) {
        this.consumers = consumers;
        this.sharedQueue = sharedQueue;
        this.userService = userService;
        this.amountOfUsers = amountOfUser;
    }

    public static Producer newInstance(List<Consumer> consumers, BlockingQueue<User> sharedQueue, UserService userService,
                                       int amountOfUser) {
        return new Producer(consumers, sharedQueue, userService, amountOfUser);
    }

    public void run() {
        while(!exit) {
            final List<User> users = userService.getUsers(amountOfUsers);
            System.out.println("Producer got users - " + users.size());
            for (final User user : users) {
                final int userId = user.getId();
                try {
                    userService.updateStatus(userId, Status.IN_PROGRESS);
                    System.out.println("Producer updated status to" + Status.IN_PROGRESS.toString() + " for user - " + userId);
                    sharedQueue.put(user);
                    System.out.println("Producer put user - " + userId + " into queue");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    userService.updateStatus(user.getId(), Status.FAILED);
                    System.out.println("Producer updated status to" + Status.FAILED.toString() + " for user - " + userId);
                }
            }
            // If size < amountOfUsers it means that we got last rows from db
            exit = users.size() != amountOfUsers;
        }

        while (sharedQueue.size() > 0) {
            try {
                Thread.sleep(2000);
                System.out.println("Producer waiting to end.");
            } catch (final InterruptedException e) {
                break;
            }
        }

        for (final Consumer consumer : consumers) {
            consumer.setExit(true);
            System.out.println("Producer sent exit command to consumer -" + consumer.getId());
        }

        sharedQueue.workWasFinished(true);
    }
}
