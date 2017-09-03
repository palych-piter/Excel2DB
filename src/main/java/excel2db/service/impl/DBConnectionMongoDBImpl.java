package excel2db.service.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import excel2db.excel2db;
import excel2db.service.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


public class DBConnectionMongoDBImpl implements DBConnection {

    // connection parameters
    @Value("${db.mongo.server}")
    String dbServer;

    @Value("${db.mongo.database}")
    String dbDatabase;

    @Value("${db.mongo.user}")
    String dbUser;

    @Value("${db.mongo.password}")
    String dbPassword;

    @Value("${db.mongo.port}")
    String dbPort;

    public static final Logger logger = LoggerFactory.getLogger(DBConnectionMongoDBImpl.class);

    public Boolean establishDBConnection() {
        try {
                MongoClientURI mongoClientURI = new MongoClientURI(
                        "mongodb://" + dbUser + ":" + dbPassword + "@" + dbServer + ":" + dbPort + "/" + dbDatabase);
                excel2db.mongoClient = new MongoClient(mongoClientURI);
                //point to the database
                excel2db.mongoDB = excel2db.mongoClient.getDatabase(dbDatabase);

                //this way we initialize the lazy MongoDB connection which wasn't actually established, known feature
                excel2db.mongoClient.getAddress();

                logger.info("MongoDB connection is established");

        } catch (Exception  e ) {
            logger.info("Error while connecting to MongoDB instance");
            return false;
        }
        return true;
    }
}
