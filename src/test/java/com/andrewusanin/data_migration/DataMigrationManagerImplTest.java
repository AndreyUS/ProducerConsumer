package com.andrewusanin.data_migration;

import com.andrewusanin.dao.UserDaoImpl;
import com.andrewusanin.pojo.User;
import com.andrewusanin.service.UserServiceImpl;
import com.andrewusanin.utils.BlockingQueue;
import com.andrewusanin.utils.BlockingQueueImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class DataMigrationManagerImplTest {

    @Test
    public void producer_consumer_data_migration() {
        final int recordLimit = 50;
        final UserDaoImpl userDaoProducer = mock(UserDaoImpl.class);
        final UserDaoImpl firstUserDaoConsumer = mock(UserDaoImpl.class);
        final UserDaoImpl secondUserDaoConsumer = mock(UserDaoImpl.class);
        final List<User> users = new ArrayList<User>();
        final BlockingQueue<User> sharedQueue = new BlockingQueueImpl<User>(recordLimit);
        for (int i = 1; i <= recordLimit; i++) {
            users.add(new User(i, "User name " + i));
        }
        when(firstUserDaoConsumer.getUsers(anyInt())).thenReturn(users);
        final Consumer consumer = ConsumerImpl.newInstance(1, sharedQueue, UserServiceImpl.newInstance(firstUserDaoConsumer),
                UserServiceImpl.newInstance(secondUserDaoConsumer));
        final List<Consumer> consumers = new ArrayList<Consumer>();
        consumers.add(consumer);
        new Thread(consumer).start();

        final Producer producer = Producer.newInstance(consumers, sharedQueue, UserServiceImpl.newInstance(userDaoProducer),
                recordLimit);
        new Thread(producer).start();
        for (int i = 1; i <= recordLimit; i++) {
            verify(secondUserDaoConsumer, Mockito.times(0)).addUser(new User(i, "User name " + i));
        }
    }

}