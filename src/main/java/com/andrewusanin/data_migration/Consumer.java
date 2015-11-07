package com.andrewusanin.data_migration;

public interface Consumer {
    /**
     * @return id of current consumer
     */
    int getId();

    /**
     * Set true if you want stop work of this consumer.
     * @param exit true - stop work, false - continue work
     */
    void setExit(boolean exit);
}
