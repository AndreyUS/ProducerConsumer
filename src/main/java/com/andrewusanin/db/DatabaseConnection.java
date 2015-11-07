package com.andrewusanin.db;

import java.sql.ResultSet;
import java.sql.Statement;

public interface DatabaseConnection {
    /**
     * Try make connection to database.
     * @return result of connection.
     */
    boolean connectionToDatabase();

    /**
     * Close connection with database.
     */
    void closeConnection();

    /**
     * Executes the given SQL statement as insert into database.
     * @param query SQL statement
     * @return result of executing
     */
    boolean insert(String query);

    /**
     * Executes the given SQL statement, which returns a single ResultSet object.
     * @param query
     * @return
     */
    ResultSet query(String query);
}
