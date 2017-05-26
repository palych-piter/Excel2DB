package excel2db.service.impl;

import java.sql.DriverManager;
import java.sql.SQLException;

import excel2db.excel2db;
import excel2db.service.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class DBConnectionPostgresImpl implements DBConnection {

    // connection parameters
    @Value("${db.server}")
    String dbServer;

    @Value("${db.user}")
    String dbUser;

    @Value("${db.password}")
    String dbPassword;

    @Value("${db.port}")
    String dbPort;

    @Value("${db.database}")
    String dbDatabase;

    public static final Logger logger = LoggerFactory.getLogger(DBConnectionPostgresImpl.class);

    public void establishDBConnection() {
        try {
            excel2db.connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + dbServer + ":" + dbPort + "/" + dbDatabase, dbUser, dbPassword)
            ;

        } catch (SQLException e) {
            logger.error("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
        if (excel2db.connection != null) {
            logger.info("Postgres connection is established");
        } else {
            logger.error("Failed to make connection!");
        }
    }
}
