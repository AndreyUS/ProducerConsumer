package com.andrewusanin.db;

import com.andrewusanin.pojo.db.BaseDatabase;

import java.sql.*;

public class JDBCDatabaseConnection<T extends BaseDatabase> implements DatabaseConnection {

    private T object;
    private Connection connection;
    private Statement statement;

    public JDBCDatabaseConnection(T object) {
        this.object = object;
    }

    private Statement getStatement() {
        if (statement == null) {
            try {
                this.statement = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statement;
    }

    public boolean connectionToDatabase() {
        try {
            connection = DriverManager.getConnection(object.getUrl(), object.getUser(), object.getPassword());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean insert(String query) {
        boolean result = false;
        try {
            if (getStatement() != null) {
                result = getStatement().execute(query);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public ResultSet query(String query) {
        ResultSet resultSet = null;
        if (getStatement() != null) {
            try {
                resultSet = getStatement().executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return resultSet;
    }
}
