package excel2db.service;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBConnection {

    public void establishDBConnection() throws SQLException;

}
