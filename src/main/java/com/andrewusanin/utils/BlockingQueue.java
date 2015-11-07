package com.andrewusanin.utils;

public interface BlockingQueue<T> {
    /**
     * Insert object into queue, waiting if necessary
     * @param t object which insert
     * @throws InterruptedException
     */
    void put(T t) throws InterruptedException;

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty. Waiting if necessary
     * @return the head of this queue, or {@code null} if this queue is empty
     * @throws InterruptedException
     */
    T poll() throws InterruptedException;

    /**
     * @return size of this queue
     */
    int size();

    /**
     * If true it will be signal all threads that wait and interrupt their execution.
     * @param finished true if work finished
     */
    void workWasFinished(boolean finished);
}
