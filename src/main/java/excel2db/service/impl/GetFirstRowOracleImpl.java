package excel2db.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import excel2db.excel2db;
import excel2db.service.GetFirstRow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetFirstRowOracleImpl implements GetFirstRow {

    ResultSet firstRow ;
    JSONObject jsonResultSet = new JSONObject();
    int numColumns;


    public JSONObject getFirstRow(String tableName) throws SQLException, JSONException {

        String sqlSelectFirstRow = "SELECT * FROM " +  tableName + " WHERE ROWNUM = 1";
        PreparedStatement pstmtSelectFirstRow = excel2db.connection.prepareStatement(sqlSelectFirstRow);
        firstRow = pstmtSelectFirstRow.executeQuery();

        if (firstRow.next()) {
            ResultSetMetaData rsmd = firstRow.getMetaData();
            numColumns = rsmd.getColumnCount();

            for (int i=numColumns; i>0; i--) {
                String column_name = rsmd.getColumnName(i);
                jsonResultSet.put(column_name, firstRow.getObject(column_name));
            }
        }

        return jsonResultSet;


    }


}
