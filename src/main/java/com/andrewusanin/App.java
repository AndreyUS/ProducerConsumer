package com.andrewusanin;

import com.andrewusanin.common.Constants;
import com.andrewusanin.data_migration.DataMigrationManagerImpl;
import com.andrewusanin.db.DatabaseConnection;
import com.andrewusanin.db.JDBCDatabaseConnection;
import com.andrewusanin.pojo.User;
import com.andrewusanin.pojo.db.Database;
import com.andrewusanin.service.UserService;
import com.andrewusanin.service.UserServiceImpl;

/**
 * This is main class of project.
 *
 */
public class App {
    public static void main(String[] args) {
        final Database firstJdbc = Database.createDatabase(Constants.firstJdbcDatabase, Constants.user,
                Constants.password);
        final Database secondJdbc = Database.createDatabase(Constants.secondJdbcDatabase, Constants.user,
                Constants.password);
        final DatabaseConnection firstDatabase = new JDBCDatabaseConnection<Database>(firstJdbc);
        final DatabaseConnection secondDatabase = new JDBCDatabaseConnection<Database>(secondJdbc);
        final boolean firstResultConnection = firstDatabase.connectionToDatabase();
        final boolean secondResultConnection = secondDatabase.connectionToDatabase();
        if (firstResultConnection && secondResultConnection) {
            final UserService firstUserService = UserServiceImpl.newInstance(firstDatabase);
            for (int i = 1; i <= 200; i++) {
                firstUserService.addUser(new User(i, "User name " + i));
            }
            final DataMigrationManagerImpl dataMigrationManager = DataMigrationManagerImpl.newInstance(16, firstJdbc, secondJdbc, 20);
            dataMigrationManager.start();
        }
    }
}
