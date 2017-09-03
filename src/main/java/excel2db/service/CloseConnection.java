package excel2db.service;

import java.sql.SQLException;

public interface CloseConnection {

    public void closeDBConnection() throws SQLException;

}
