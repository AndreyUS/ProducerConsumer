package com.andrewusanin.data_migration;

import com.andrewusanin.pojo.User;
import com.andrewusanin.service.UserService;
import com.andrewusanin.utils.BlockingQueue;


/**
 * This class take user model from queue and writes into database
 */
public class ConsumerImpl implements Consumer {

    private boolean exit;
    private int id;
    private BlockingQueue<User> sharedQueue;
    private UserService secondUserService;
    private UserService firstUserService;

    private ConsumerImpl() { }

    private ConsumerImpl(int id, BlockingQueue<User> sharedQueue, UserService firstUserService, UserService secondUserService) {
        this.id = id;
        this.sharedQueue = sharedQueue;
        this.firstUserService = firstUserService;
        this.secondUserService = secondUserService;
    }

    public static ConsumerImpl newInstance(int id, BlockingQueue<User> sharedQueue, UserService firstUserService,
                                       UserService secondUserService) {
        return new ConsumerImpl(id, sharedQueue, firstUserService, secondUserService);
    }

    public int getId() {
        return id;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    private boolean isExit() {
        return exit;
    }

    public void run() {
        while (!isExit()) {
            try {
                if (sharedQueue.size() > 0) {
                    final User user  = sharedQueue.poll();
                    System.out.println("ConsumerImpl : " + id + " take user " + user.getId());
                    firstUserService.updateStatus(user.getId(), Status.PROCESSING);
                    secondUserService.addUser(user);
                    firstUserService.updateStatus(user.getId(), Status.SUCCESSFUL);
                    System.out.println("User with id - " + user.getId() + " was added by consumer " + id);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ConsumerImpl " + id + " will finish work." );
    }

}
