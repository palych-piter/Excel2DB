package excel2db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.ge.mdm.tools.common.ApplicationException;
import org.apache.commons.io.FilenameUtils;

public class Excel2dbTask extends Thread {

    public Excel2dbTask(excel2db app, File fileName) {
        this.app = app;
        this.fileName = fileName;
    }

    public excel2db app;
    public File fileName;

    String tableName;

    @Override
    public void run() {
        try {
            app.initInputFiles.initInputFiles(fileName);

            tableName = FilenameUtils.removeExtension(fileName.getName());
            app.createTable.createTable(tableName);
            app.populateTable.populateTable(tableName);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

    }
}
