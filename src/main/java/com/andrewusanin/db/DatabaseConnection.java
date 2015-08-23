package com.andrewusanin.db;

import java.sql.ResultSet;
import java.sql.Statement;

public interface DatabaseConnection {
    boolean connectionToDatabase();
    void closeConnection();
    boolean insert(String query);
    ResultSet query(String query);
}
