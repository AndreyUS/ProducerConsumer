package com.andrewusanin;

import com.andrewusanin.common.Constants;
import com.andrewusanin.data_migration.DataMigrationManager;
import com.andrewusanin.db.DatabaseConnection;
import com.andrewusanin.db.JDBCDatabaseConnection;
import com.andrewusanin.pojo.db.Database;
import com.andrewusanin.service.UserService;
import com.andrewusanin.service.UserServiceImpl;

/**
 * This is main class of project.
 *
 */
public class App {
    public static void main( String[] args )
    {
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
            final UserService secondUserService = UserServiceImpl.newInstance(secondDatabase);
            final DataMigrationManager dataMigrationManager = DataMigrationManager.newInstance(20, firstUserService, secondUserService);
            dataMigrationManager.start();
        }
    }
}
