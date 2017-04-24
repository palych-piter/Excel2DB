package excel2db.service.impl;

import excel2db.service.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class DBConnectionOracleImpl implements DBConnection {

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
         {
             logger.info("Oracle connection is not implemented yet");
         }
    }

}
