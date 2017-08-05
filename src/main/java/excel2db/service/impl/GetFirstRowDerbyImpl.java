package excel2db.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import excel2db.excel2db;
import excel2db.service.GetFirstRow;

public class GetFirstRowDerbyImpl implements GetFirstRow {

    ResultSet firstRow ;

    @Override
    public ResultSet getFirstRow(String tableName) throws SQLException {

        String sqlSelectFirstRow = "SELECT * FROM " +  tableName + " FETCH FIRST ROW ONLY";
        PreparedStatement pstmtSelectFirstRow = excel2db.connection.prepareStatement(sqlSelectFirstRow);
        firstRow = pstmtSelectFirstRow.executeQuery();
        return firstRow;

    }

}
