package excel2db.service;

import java.sql.SQLException;

public interface CreateTable {

    public void createTable(String tableName) throws SQLException;

}
