package com.andrewusanin.data_migration;

import com.andrewusanin.pojo.User;
import com.andrewusanin.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DataMigrationManager {

    private int numberOfThreads = 10;
    private UserService firstUserService;
    private UserService secondUserService;

    private DataMigrationManager () { }

    private DataMigrationManager(int numberOfThreads, UserService firstUserService, UserService secondUserService) {
        this.numberOfThreads = numberOfThreads;
        this.firstUserService = firstUserService;
        this.secondUserService = secondUserService;
    }

    public static DataMigrationManager newInstance(int numberOfThreads, UserService firstUserService, UserService secondUserService) {
        if (numberOfThreads <= 0 || numberOfThreads > 100) {
            throw new IllegalArgumentException("The number of threads should be a number between 1 and 100");
        }
        return new DataMigrationManager(numberOfThreads, firstUserService, secondUserService);
    }

    public void start() {
        final BlockingQueue<User> sharedQueue = new LinkedBlockingDeque<User>();
        final List<Consumer> consumers = new ArrayList<Consumer>(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            System.out.println("Create consumer with id - " + i);
            final Consumer consumer = Consumer.newInstance(i, sharedQueue, secondUserService);
            new Thread(consumer).start();
            consumers.add(consumer);
        }
        final Producer producer = Producer.newInstance(consumers, sharedQueue, firstUserService);
        new Thread(producer).start();
    }
}
