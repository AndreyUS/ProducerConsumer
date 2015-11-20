package com.andrewusanin.data_migration;

import com.andrewusanin.dao.UserDao;
import com.andrewusanin.dao.UserDaoImpl;
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
        final List<Consumer> consumers = new ArrayList<Consumer>(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            final DatabaseConnection firstDatabase = createDatabaseConnection(this.firstDatabase);
            final DatabaseConnection secondDatabase = createDatabaseConnection(this.secondDatabase);
            final boolean firstResultConnection = firstDatabase.connectionToDatabase();
            final boolean secondResultConnection = secondDatabase.connectionToDatabase();
            if (firstResultConnection && secondResultConnection) {
                System.out.println("Create consumer with id - " + i);
                final UserDao firstUserDao = UserDaoImpl.newInstance(firstDatabase);
                final UserDao secondUserDao = UserDaoImpl.newInstance(firstDatabase);
                final ConsumerImpl consumer = ConsumerImpl.newInstance(i, sharedQueue, UserServiceImpl.newInstance(firstUserDao),
                        UserServiceImpl.newInstance(secondUserDao));
                final Thread thread = new Thread(consumer);
                thread.start();
                consumers.add(consumer);
            }
        }

        final DatabaseConnection firstDatabase = createDatabaseConnection(this.firstDatabase);
        final boolean firstResultConnection = firstDatabase.connectionToDatabase();
        if (firstResultConnection) {
            System.out.println("Create producer");
            final UserDao firstUserDao = UserDaoImpl.newInstance(firstDatabase);
            final Producer producer = Producer.newInstance(consumers, sharedQueue, UserServiceImpl.newInstance(firstUserDao), recordLimit);
            new Thread(producer).start();
        }

    }

    private DatabaseConnection createDatabaseConnection(final Database database) {
        return new JDBCDatabaseConnection<Database>(database);
    }
}
