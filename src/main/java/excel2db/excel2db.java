
package excel2db;


import com.ge.mdm.tools.common.ApplicationException;
import com.ge.mdm.tools.common.SheetEntityManager;
import excel2db.service.DBConnection;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;


public class excel2db
{
    public static final Logger logger = LoggerFactory.getLogger(excel2db.class);

    private Workbook workbook;

    private SheetEntityManager sheetEntityManager;
    private Sheet sheet;
    private File inputSheetFile;
    private File outputSheetFile;
    private String cellStringValue;
    private String fileExtension;
    private static Short numberProcessedRecords = Short.valueOf((short)0);

    public static Connection connection = null;

    public DBConnection dbConnection;

    public excel2db() {}

    public File getInputSheetFile() { return inputSheetFile; }

    public void setInputSheetFile(File inputSheetFile)
    {
        this.inputSheetFile = inputSheetFile;
    }

    public File getOutputSheetFile() {
        return outputSheetFile;
    }

    public void setOutputSheetFile(File outputSheetFile) {
        this.outputSheetFile = outputSheetFile;
    }

    public void setDbConnection(DBConnection dbConnection)
    {
        this.dbConnection = dbConnection;
    }

    public static void main(String[] args)
    {
        if (args.length < 1) {
            logger.error("Need file name as an argument.");
            System.exit(1);
        }

        logger.info("excel2db : Passing file name as argument ; " + String.join(" ", args));

        File inputFile = new File(args[0]);

        try
        {

            ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

            excel2db app = (excel2db)context.getBean("excel2db");

            //DBConnection dbConnection = (DBConnection) context.getBean("dbConnection");


            app.setInputSheetFile(inputFile);

            app.initAndValidate();

            app.dbConnection.establishDBConnection();

            app.createTable();
            app.populateTable();
            app.closeConnections();

        }
        catch (Exception e) {
            logger.error("An exception occurred while running application", e);
            System.exit(1);
        }
    }


    public void initAndValidate()
            throws ApplicationException, IOException
    {
        logger.info("Reading input file '{}'...", inputSheetFile.getAbsolutePath());


        fileExtension = FilenameUtils.getExtension(inputSheetFile.getName());
        if (fileExtension.equalsIgnoreCase("xlsx")) {
            workbook = createWorkbook(openOPCPackage(inputSheetFile));
        } else {
            workbook = createWorkbook(openNPOIFSFileSystemPackage(inputSheetFile));
        }

        sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new ApplicationException("Workbook does not contain a sheet with index 0");
        }

        sheetEntityManager = new SheetEntityManager(sheet);
    }


    private static OPCPackage openOPCPackage(File file)
            throws ApplicationException
    {
        try
        {
            return OPCPackage.open(file);
        } catch (InvalidFormatException e) {
            throw new ApplicationException("Unable to open OPC package from " + file.getAbsolutePath(), e);
        }
    }

    private static NPOIFSFileSystem openNPOIFSFileSystemPackage(File file) throws ApplicationException
    {
        try {
            return new NPOIFSFileSystem(file);
        } catch (IOException e) {
            throw new ApplicationException("Unable to open NPOIFSFileSystem package from " + file.getAbsolutePath(), e);
        }
    }

    private static Workbook createWorkbook(OPCPackage pkg) throws ApplicationException
    {
        try {
            return new XSSFWorkbook(pkg);
        } catch (IOException e) {
            throw new ApplicationException("Unable to create workbook from OPC package", e);
        }
    }

    private static Workbook createWorkbook(NPOIFSFileSystem pkg) throws ApplicationException
    {
        try {
            return new HSSFWorkbook(pkg);
        } catch (IOException e) {
            throw new ApplicationException("Unable to create workbook from NPOIFSFileSystem package", e);
        }
    }

    public void createTable()
            throws SQLException
    {
        String sqlTableDropStatement = "DROP TABLE IF EXISTS excel2db;";



        StringBuilder sqlTableCreateStatement = new StringBuilder();

        sqlTableCreateStatement.append("CREATE TABLE excel2db(");


        for (String headerColumnName : sheetEntityManager.header.keySet()) {
            sqlTableCreateStatement.append("\"" + headerColumnName + "\"" + " text, ");
        }


        sqlTableCreateStatement.setLength(sqlTableCreateStatement.length() - 2);

        sqlTableCreateStatement.append(") WITH (OIDS=FALSE);");


        PreparedStatement pstmtDrop = connection.prepareStatement(sqlTableDropStatement);
        pstmtDrop.execute();

        PreparedStatement pstmtCreate = connection.prepareStatement(sqlTableCreateStatement.toString());
        pstmtCreate.execute();
    }


    public void populateTable()
            throws SQLException
    {
        Iterator<Row> rowIterator = sheet.rowIterator();

        if (rowIterator.hasNext()) {
            rowIterator.next();
        }


        while (rowIterator.hasNext())
        {
            Row row = (Row)rowIterator.next();

            Short localShort1 = numberProcessedRecords;Short localShort2 = excel2db.numberProcessedRecords = Short.valueOf((short)(numberProcessedRecords.shortValue() + 1));

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



            PreparedStatement pstmtInsertRow = connection.prepareStatement(sqlTableInsertStatement.toString());
            pstmtInsertRow.execute();
        }
    }

    public void closeConnections() throws ApplicationException, SQLException {
        logger.info("Closing connections");
        connection.close();
        logger.info("The Process is Completed");
        logger.info("Number of Processed Records: " + numberProcessedRecords.toString());
    }
}