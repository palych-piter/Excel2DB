package excel2db.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

//import com.ge.mdm.tools.common.SheetEntityManager;
import excel2db.excel2db;
import excel2db.service.CreateTable;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTablePostgresImpl implements CreateTable {

    public static final Logger logger = LoggerFactory.getLogger(CreateTablePostgresImpl.class);
    //private SheetEntityManager sheetEntityManager;

    public void createTable(Sheet sheet, String tableName) throws SQLException {

        //sheetEntityManager = new SheetEntityManager(sheet);
        //initializing var for a header
        Map<String, Integer> header = new LinkedHashMap<>();
        header = InitInputFilesImpl.readSheetHeader(sheet);


        //dropping statement
        String sqlTableDropStatement = "DROP TABLE IF EXISTS \"" +
                tableName + "\"";

        //creating statement
        StringBuilder sqlTableCreateStatement = new StringBuilder();
        sqlTableCreateStatement.append("CREATE TABLE \"" +
                tableName + "\"(");
        for (String headerColumnName : header.keySet()) {
            sqlTableCreateStatement.append("\"" + headerColumnName + "\"" + " text, ");
        }


        sqlTableCreateStatement.setLength(sqlTableCreateStatement.length() - 2);

        sqlTableCreateStatement.append(") WITH (OIDS=FALSE);");

        //dropping
        logger.info("The table " + tableName + " is being dropped");
        PreparedStatement pstmtDrop = excel2db.connection.prepareStatement(sqlTableDropStatement);
        pstmtDrop.execute();
        logger.info("The table " + tableName + " has been dropped");

        //creating
        logger.info("The table " + tableName + " is being created");
        PreparedStatement pstmtCreate = excel2db.connection.prepareStatement(sqlTableCreateStatement.toString());
        pstmtCreate.execute();
        logger.info("The table " + tableName + " has been created");

    }

}
