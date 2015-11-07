package com.andrewusanin.utils;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueImpl<T> implements BlockingQueue<T> {

    private ReentrantLock lock = new ReentrantLock();
    private Condition lockOnPut = lock.newCondition();
    private Condition lockOnPoll = lock.newCondition();

    private Queue<T> queue = new ArrayDeque<T>();
    private int limit;
    private boolean finished;

    public BlockingQueueImpl(int limit) {
        this.limit = limit;
    }

    public void put(T t) throws InterruptedException {
        final Lock lock = this.lock;
        lock.lock();
        try {
            if (isFull()) {
                lockOnPut.await();
            } else {
                queue.add(t);
                lockOnPoll.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public T poll() throws InterruptedException {
        final Lock lock = this.lock;
        lock.lock();
        try {
            while (queue.isEmpty()) {
                lockOnPoll.await();
                if (finished) {
                    throw new RuntimeException("Work was finished");
                }
            }
            lockOnPut.signalAll();
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    private boolean isFull() {
        return queue.size() >= limit;
    }

    public int size() {
        final Lock lock = this.lock;
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public void workWasFinished(final boolean finished) {
        final Lock lock = this.lock;
        lock.lock();
        try {
            this.finished = finished;
            lockOnPoll.signalAll();
        } finally {
            lock.unlock();
        }

    }

}
