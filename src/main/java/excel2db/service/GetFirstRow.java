package excel2db.service;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface GetFirstRow {

    public ResultSet getFirstRow (String tableName) throws SQLException;

}
