package excel2db.service.impl;

/**
 * Created by Andrey on 6/21/2017.
 */

import java.sql.DriverManager;
import java.sql.SQLException;

import excel2db.excel2db;
import excel2db.service.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;





public class DBConnectionDerbyImpl implements DBConnection {

    // connection parameters
    @Value("${db.derby.server}")
    String dbServer;

    @Value("${db.derby.user}")
    String dbUser;

    @Value("${db.derby.password}")
    String dbPassword;

    @Value("${db.derby.port}")
    String dbPort;

    @Value("${db.derby.database}")
    String dbName;


    public static final Logger logger = LoggerFactory.getLogger(DBConnectionDerbyImpl.class);

    public void establishDBConnection() {
        {

            try {
                excel2db.connection = DriverManager.getConnection(
                        //"jdbc:derby//" + dbServer + ":" + dbPort + "/" + dbName + ";create=true;user=" + dbUser + ";password=" + dbPassword
                        "jdbc:derby://localhost:1527/testDB;create=true"
                );

            } catch (SQLException e) {
                logger.error("Derby Connection Failed! Check output console");
                e.printStackTrace();
                return;
            }
            if (excel2db.connection != null) {
                logger.info("Derby connection is established");
            } else {
                logger.error("Failed to make the Derby connection!");
            }

        }

    }

}
