package excel2db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import excel2db.ApplicationException;
import excel2db.excel2db;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;

public class Excel2dbTask extends Thread {

    public Excel2dbTask(excel2db app, File fileName) {
        this.app = app;
        this.fileName = fileName;
    }

    public excel2db app;
    public File fileName;
    private Sheet sheet;

    static public Integer numberOfProcessedRows = 0;

    String tableName;

    @Override
    public void run() {
        try {

            sheet = app.initInputFiles.initInputFiles(fileName);
            tableName = FilenameUtils.removeExtension(fileName.getName());
            app.createTable.createTable(sheet, tableName);
            numberOfProcessedRows = app.populateTable.populateTable(sheet, tableName);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

    }
}
