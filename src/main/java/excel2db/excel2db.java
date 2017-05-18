package excel2db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.ge.mdm.tools.common.ApplicationException;
import excel2db.service.CreateTable;
import excel2db.service.DBConnection;
import excel2db.service.GenerateFileList;
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
    public static Short numberProcessedRecords = Short.valueOf((short) 0);


    // declaring variables to meet to the Spring framework convention
    public DBConnection dbConnection;
    public InitInputFiles initInputFiles;
    public GenerateFileList generateFileList;
    public CreateTable createTable;
    public PopulateTable populateTable;
    public InitConstants initConstants;

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



    public static void main(String[] args) {

        try {

            ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
            excel2db app = (excel2db) context.getBean("excel2db");

            taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");

            //this way we call methods for objects that is already initialized by the Spring
            app.dbConnection.establishDBConnection();

            // iterate through file name values inn properties file
            for (String fileNameValue : app.generateFileList.generateFileList()) {
                File fileName = new File(fileNameValue);
                taskExecutor.execute(new Excel2dbTask(app, fileName));
            }

            // Wait until all threads are finished
            logger.info("Waiting until active threads exist");
            while (excel2db.taskExecutor.getActiveCount() != 0) {
            }
            logger.info("All threads are inactive");

            app.closeConnections();

        } catch (Exception e) {
            logger.error("An exception occurred while running application", e);
            System.exit(1);
        }

    }


    public void closeConnections() throws ApplicationException, SQLException {
        logger.info("Closing connections");
        connection.close();

        logger.info("Closing task executor ");
        taskExecutor.shutdown();
        logger.info("Task executor is stopped");

        logger.info("The process is completed");
        logger.info("Number of processed records: " + numberProcessedRecords.toString());

    }

}