package excel2db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import excel2db.service.CreateTable;
import excel2db.service.DBConnection;
import excel2db.service.GenerateFileList;
import excel2db.service.GetFirstRow;
import excel2db.service.InitConstants;
import excel2db.service.InitInputFiles;
import excel2db.service.PopulateTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


public class excel2db
{
    public static final Logger logger = LoggerFactory.getLogger(excel2db.class);

    // declaring the constructor to meet to the Spring framework convention
    // constructor injection method (CI))
    public excel2db(CreateTable createTable, PopulateTable populateTable) {
        this.createTable = createTable;
        this.populateTable = populateTable;
    }

    //vars are used in interface implementations
    public static Connection connection = null;


    // declaring variables to meet to the Spring framework convention
    public DBConnection dbConnection;
    public InitInputFiles initInputFiles;
    public GenerateFileList generateFileList;
    public CreateTable createTable;
    public PopulateTable populateTable;
    public InitConstants initConstants;
    public GetFirstRow getFirstRow;



    public static ThreadPoolTaskExecutor taskExecutor;

    // declaring setters to meet to the Spring framework convention.
    // Setter injection method (SI))
    public void setDbConnection(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    public void setInitInputFiles(InitInputFiles initInputFiles) {
        this.initInputFiles = initInputFiles;
    }
    public void setGenerateFileList(GenerateFileList generateFileList) {
        this.generateFileList = generateFileList;
    }
    public void setInitConstants(InitConstants initConstants) {
        this.initConstants = initConstants;
    }
    public void setGetFirstRow(GetFirstRow getFirstRow) {
        this.getFirstRow = getFirstRow;
    }


    public static void main(String[] args) {

        try {

            //for profiling, to launch the VisualVM first,
            // then start the application by pressing the Enter
            //System.in.read();

            ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
            excel2db app = (excel2db) context.getBean("excel2db");

            taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");

            //check if the file list is not empty
            HashSet<String> fileList = app.generateFileList.generateFileList();
            if (fileList == null){
                throw new Exception("File list is empty in property file");

            }

            //this way we call methods for objects that is already initialized by the Spring
            app.dbConnection.establishDBConnection();

            // in case the connection established
            // iterate through file name values in properties file, for each file start a new thread
            if (connection != null) {
                for (String fileNameValue : fileList) {

                    File fileName = new File(fileNameValue);
                    //check if the file really exists
                    if (fileName.exists() == false) {
                        logger.error("The file " + fileNameValue + " doesn't exist");
                    } else {
                        taskExecutor.execute(new Excel2dbTask(app, fileName));
                    }
                }


                //TODO : This is just as a workaround to wait till the thread has been really started
                //TODO : Need to use the ScheduledThreadPoolExecutor to customize delays on starting threads
                TimeUnit.SECONDS.sleep(1);
                // Wait until all threads are finished
                logger.info("Waiting until active threads exist");
                while (taskExecutor.getActiveCount() != 0) {
                }
                logger.info("All threads are inactive");

                app.closeConnections();
            }
        } catch (Exception e) {
            logger.error("An exception occurred while running application", e);
            System.exit(1);
        }

    }

    public void closeConnections() throws ApplicationException, SQLException {

        connection.close();
        logger.info("Connections are closed");

        taskExecutor.shutdown();
        logger.info("Task executor is stopped");

        logger.info("The process is completed. Number of processed records: " + Excel2dbTask.numberOfProcessedRows.toString());

    }

}