package excel2db.service.impl;

import excel2db.excel2db;
import excel2db.service.PopulateTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

public class PopulateTablePostgresImpl implements PopulateTable {

    private String cellStringValue;


    public void populateTable()
            throws SQLException
    {
        Iterator<Row> rowIterator = excel2db.sheet.rowIterator();

        if (rowIterator.hasNext()) {
            rowIterator.next();
        }


        while (rowIterator.hasNext())
        {
            Row row = (Row)rowIterator.next();

            Short localShort1 = excel2db.numberProcessedRecords;Short localShort2 = excel2db.numberProcessedRecords = Short.valueOf((short)(excel2db.numberProcessedRecords.shortValue() + 1));

            StringBuilder sqlTableInsertStatement = new StringBuilder();
            sqlTableInsertStatement.append("INSERT INTO excel2db VALUES");

            sqlTableInsertStatement.append("(");


            short minColIdx = row.getFirstCellNum();
            short maxColIdx = row.getLastCellNum();

            for (short colIdx = minColIdx; colIdx < maxColIdx; colIdx = (short)(colIdx + 1)) {
                cellStringValue = "";
                Cell cell = row.getCell(colIdx);
                if (cell == null) cellStringValue = ""; else cellStringValue = cell.toString();
                sqlTableInsertStatement.append("'" + cellStringValue.replace("'", "''") + "'" + ",");
            }

            sqlTableInsertStatement.setLength(sqlTableInsertStatement.length() - 1);

            sqlTableInsertStatement.append(")");

            PreparedStatement pstmtInsertRow = excel2db.connection.prepareStatement(sqlTableInsertStatement.toString());
            pstmtInsertRow.execute();

        }
    }


}
