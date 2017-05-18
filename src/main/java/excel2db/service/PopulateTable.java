package excel2db.service;

import java.sql.SQLException;

public interface PopulateTable {

    public Integer populateTable(String tableName) throws SQLException;

}
