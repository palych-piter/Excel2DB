package excel2db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class Excel2dbTask extends Thread {

    public static final Logger logger = LoggerFactory.getLogger(excel2db.class);


    public Excel2dbTask(excel2db app, File fileName, HashSet<String> sheetNames) {
        this.app = app;
        this.fileName = fileName;
        this.sheetNames = sheetNames;
    }

    public excel2db app;
    public File fileName;
    private HashSet<String> sheetNames;
    private Sheet[] sheets;

    static public Integer numberOfProcessedRows = 0;

    String tableName;

    @Override
    public void run() {
        try {

            sheets = app.initInputFiles.initInputFiles(fileName);
            tableName = FilenameUtils.removeExtension(fileName.getName());
            for(Sheet toProcess: sheets) {
                String sheetName = toProcess.getSheetName();
                if(sheetNames.isEmpty() || sheetNames.contains(sheetName)) {
                    logger.info("Processing sheet " + sheetName + " from file " + fileName);
                    app.createTable.createTable(toProcess, StringUtils.isEmpty(sheetName) ?
                            tableName : sheetName);
                    numberOfProcessedRows += app.populateTable.populateTable(toProcess, StringUtils.isEmpty(sheetName) ?
                            tableName : sheetName);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

    }
}
