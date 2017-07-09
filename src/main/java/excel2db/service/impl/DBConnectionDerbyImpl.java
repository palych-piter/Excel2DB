package excel2db.service.impl;

/**
 * Created by Andrey on 6/21/2017.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import excel2db.excel2db;
import excel2db.service.DBConnection;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


public class DBConnectionDerbyImpl implements DBConnection {

    public static final Logger logger = LoggerFactory.getLogger(DBConnectionDerbyImpl.class);

    public void establishDBConnection() {
        {
            try {
                //initialize an In-Memory Derby database
                EmbeddedDriver driver = new EmbeddedDriver();
                excel2db.connection = driver.connect("jdbc:derby:memory:testdb;create=true", new Properties());
                logger.info("Derby connection is established");

            } catch (SQLException e) {
                logger.error("Derby Connection Failed! Check output console");
            }
        }
    }
}
