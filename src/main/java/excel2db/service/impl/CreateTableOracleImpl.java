package excel2db.service.impl;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import excel2db.excel2db;
import excel2db.service.CreateTable;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTableOracleImpl implements CreateTable {

    public static final Logger logger = LoggerFactory.getLogger(CreateTableOracleImpl.class);

    public void createTable(Sheet sheet, String tableName) throws SQLException {

        Map<String, Integer> header = new LinkedHashMap<>();
        header = InitInputFilesImpl.readSheetHeader(sheet);

        //dropping statement
        String sqlTableDropStatement = "DROP TABLE \"" +
                tableName + "\"";

        //creating statement
        StringBuilder sqlTableCreateStatement = new StringBuilder();
        sqlTableCreateStatement.append("CREATE TABLE \"" +
                tableName + "\"(");
        for (String headerColumnName : header.keySet()) {
            sqlTableCreateStatement.append("\"" + headerColumnName + "\"" + " VARCHAR2(20), ");
        }

        sqlTableCreateStatement.setLength(sqlTableCreateStatement.length() - 2);
        sqlTableCreateStatement.append(")");


        //Oracle doesn't support the "DROP IF EXISTS" SQL statement
        //Thus, checking the table and dropping if the table exists
        logger.info("Checking if the table " + tableName + " already exists");
        DatabaseMetaData dbm = excel2db.connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        if (tables.next()) {
            // Table exists, dropping
            logger.info("The table " + tableName + " exists, dropping ...");
            PreparedStatement pstmtDrop = excel2db.connection.prepareStatement(sqlTableDropStatement);
            pstmtDrop.execute();
            logger.info("The table " + tableName + " has been dropped");
        }
        else {
            // Table does not exist, skipp dropping
            logger.info("The table " + tableName + " doesn't exist, skip dropping");
        }


        //creating
        logger.info("The table " + tableName + " is being created ...");
        PreparedStatement pstmtCreate = excel2db.connection.prepareStatement(sqlTableCreateStatement.toString());
        pstmtCreate.execute();
        logger.info("The table " + tableName + " has been created");


    }

}


