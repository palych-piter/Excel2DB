package excel2db.service;

import java.sql.SQLException;

import org.apache.poi.ss.usermodel.Sheet;

public interface PopulateTable {

    public Integer populateTable(Sheet sheet, String tableName) throws SQLException;

}
