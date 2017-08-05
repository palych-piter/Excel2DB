package excel2db.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import excel2db.excel2db;
import excel2db.service.PopulateTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopulateTablePostgresImpl implements PopulateTable {

    public static final Logger logger = LoggerFactory.getLogger(PopulateTablePostgresImpl.class);

    private String cellStringValue;
    private Integer numOfProcessedRows = 0;

    public Integer populateTable(Sheet sheet, String tableName)
            throws SQLException {

        Iterator<Row> rowIterator = InitInputFilesImpl.sheet.rowIterator();

        //skip a header
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }


        logger.info("The table {} is being populated", tableName);
        while (rowIterator.hasNext()) {

            Row row = (Row) rowIterator.next();

            //count rows
            numOfProcessedRows++;

            StringBuilder sqlTableInsertStatement = new StringBuilder();
            sqlTableInsertStatement.append("INSERT INTO \"" +
                    tableName + "\"" + "VALUES");

            sqlTableInsertStatement.append("(");

            short minColIdx = row.getFirstCellNum();
            short maxColIdx = row.getLastCellNum();

            for (short colIdx = minColIdx; colIdx < maxColIdx; colIdx = (short) (colIdx + 1)) {
                cellStringValue = "";
                Cell cell = row.getCell(colIdx);
                if (cell == null) cellStringValue = "";
                else cellStringValue = cell.toString();
                sqlTableInsertStatement.append("'" + cellStringValue.replace("'", "''") + "'" + ",");
            }


            sqlTableInsertStatement.setLength(sqlTableInsertStatement.length() - 1);

            sqlTableInsertStatement.append(")");

            PreparedStatement pstmtInsertRow = excel2db.connection.prepareStatement(sqlTableInsertStatement.toString());

            pstmtInsertRow.execute();


        }

        logger.info("The table {} is populated", tableName);

        return numOfProcessedRows;

    }

}
