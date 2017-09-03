package excel2db.service.impl;

import java.sql.SQLException;

import excel2db.excel2db;
import excel2db.service.CloseConnection;

public class CloseConnectionDerbyImpl implements CloseConnection {

    @Override
    public void closeDBConnection() throws SQLException {
        excel2db.connection.close();
    }

}
