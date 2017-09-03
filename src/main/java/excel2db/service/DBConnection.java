package excel2db.service;

import java.sql.SQLException;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;

public interface DBConnection {

    public Boolean establishDBConnection() throws SQLException ;

}
