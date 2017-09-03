package excel2db.service.impl;

import java.sql.Connection;
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


    public Boolean establishDBConnection() throws SQLException {

        //TODO : try to use the try-catch-resource construction, connection object should be closed as soon as the program stops working with this object
        try {
            excel2db.connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + dbServer + ":" + dbPort + "/" + dbDatabase, dbUser, dbPassword);
            logger.info("Postgres connection is established");
        } catch (SQLException e) {
            logger.error("Postgres Connection Failed! Check output console.");
            return false;
        }

        return true;
    }

}
