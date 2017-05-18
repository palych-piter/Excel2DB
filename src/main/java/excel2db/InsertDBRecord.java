package excel2db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class InsertDBRecord extends Thread {

    public InsertDBRecord(PreparedStatement insertDBRecordStatement) {
        this.insertDBRecordStatement = insertDBRecordStatement;
    }

    public PreparedStatement insertDBRecordStatement;

    public static final Logger logger = LoggerFactory.getLogger(InsertDBRecord.class);

    @Override
    public void run() {
        try {
            logger.info("Thread " + getName() + " started");
            insertDBRecordStatement.execute();
            logger.info("Thread " + getName() + " finished");
        } catch (SQLException e) {
            //Log the error somehow
        }
//        catch (InterruptedException e) {
//            //Log the error somehow
//        }

    }

}
