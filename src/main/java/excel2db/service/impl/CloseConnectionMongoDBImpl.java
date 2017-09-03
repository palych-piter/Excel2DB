package excel2db.service.impl;

import excel2db.excel2db;
import excel2db.service.CloseConnection;

/**
 * Created by Andrey on 8/15/2017.
 */
public class CloseConnectionMongoDBImpl implements CloseConnection{

    @Override
    public void closeDBConnection() {
        excel2db.mongoClient.close();
    }

}
