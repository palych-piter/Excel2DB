package excel2db.service;

import java.sql.SQLException;

import org.apache.poi.ss.usermodel.Sheet;

public interface CreateTable {

    public void createTable(Sheet sheet, String tableName) throws SQLException;

}
