package com.andrewusanin.data_migration;

import com.andrewusanin.db.DatabaseConnection;
import com.andrewusanin.db.JDBCDatabaseConnection;
import com.andrewusanin.pojo.User;
import com.andrewusanin.pojo.db.Database;
import com.andrewusanin.service.UserServiceImpl;
import com.andrewusanin.utils.BlockingQueue;
import com.andrewusanin.utils.BlockingQueueImpl;

import java.util.ArrayList;
import java.util.List;

public class DataMigrationManagerImpl implements DataMigrationManager {

    private int numberOfThreads = 10;
    private int recordLimit = 100;

    private Database firstDatabase;
    private Database secondDatabase;

    private DataMigrationManagerImpl() { }

    private DataMigrationManagerImpl(int numberOfThreads, Database firstDatabase, Database secondDatabase, int recordLimit) {
        this.numberOfThreads = numberOfThreads;
        this.firstDatabase = firstDatabase;
        this.secondDatabase = secondDatabase;
        this.recordLimit = recordLimit;
    }

    public static DataMigrationManagerImpl newInstance(int numberOfThreads, Database firstDatabase,
                                                   Database secondDatabase, int recordLimit) {
        if (numberOfThreads <= 0 || numberOfThreads > 100) {
            throw new IllegalArgumentException("The number of threads should be a number between 1 and 100");
        }
        return new DataMigrationManagerImpl(numberOfThreads, firstDatabase, secondDatabase, recordLimit);
    }

    public void start() {
        final BlockingQueue<User> sharedQueue = new BlockingQueueImpl<User>(recordLimit);
        final List<ConsumerImpl> consumers = new ArrayList<ConsumerImpl>(numberOfThreads);
        final List<Thread> consumersThreads = new ArrayList<Thread>(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            final DatabaseConnection firstDatabase = createDatabaseConnection(this.firstDatabase);
            final DatabaseConnection secondDatabase = createDatabaseConnection(this.secondDatabase);
            final boolean firstResultConnection = firstDatabase.connectionToDatabase();
            final boolean secondResultConnection = secondDatabase.connectionToDatabase();
            if (firstResultConnection && secondResultConnection) {
                System.out.println("Create consumer with id - " + i);
                final ConsumerImpl consumer = ConsumerImpl.newInstance(i, sharedQueue, UserServiceImpl.newInstance(firstDatabase),
                        UserServiceImpl.newInstance(secondDatabase));
                final Thread thread = new Thread(consumer);
                thread.start();
                consumersThreads.add(thread);
                consumers.add(consumer);
            }
        }

        final DatabaseConnection firstDatabase = createDatabaseConnection(this.firstDatabase);
        final boolean firstResultConnection = firstDatabase.connectionToDatabase();
        if (firstResultConnection) {
            System.out.println("Create producer");
            final Producer producer = Producer.newInstance(consumers, sharedQueue, UserServiceImpl.newInstance(firstDatabase), recordLimit);
            producer.consumersThreads = consumersThreads;
            new Thread(producer).start();
        }

    }

    private DatabaseConnection createDatabaseConnection(final Database database) {
        return new JDBCDatabaseConnection<Database>(database);
    }
}
