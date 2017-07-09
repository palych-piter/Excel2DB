package excel2db.service.impl;

import java.sql.DriverManager;
import java.sql.SQLException;

import excel2db.excel2db;
import excel2db.service.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import oracle.jdbc.driver.OracleDriver;


public class DBConnectionOracleImpl implements DBConnection {

    // connection parameters
    @Value("${db.oracle.server}")
    String dbServer;

    @Value("${db.oracle.user}")
    String dbUser;

    @Value("${db.oracle.password}")
    String dbPassword;

    @Value("${db.oracle.port}")
    String dbPort;

    //it could be either sid or service name,
    // depending what is provided to connect to the DB
    @Value("${db.oracle.sid}")
    String dbSid;

    public static final Logger logger = LoggerFactory.getLogger(DBConnectionOracleImpl.class);

    public void establishDBConnection() {
        {

            try {
                excel2db.connection = DriverManager.getConnection(
                        "jdbc:oracle:thin:@//" + dbServer + ":" + dbPort + "/" + dbSid, dbUser, dbPassword);
                logger.info("Oracle connection is established");
            } catch (SQLException e) {
                logger.error("Oracle Connection Failed! Check output console");
            }
        }

    }

}
