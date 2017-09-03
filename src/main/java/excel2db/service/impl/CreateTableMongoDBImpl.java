package excel2db.service.impl;

import com.mongodb.DBCollection;
import com.mongodb.client.MongoDatabase;
import excel2db.excel2db;
import excel2db.service.CreateTable;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xwpf.usermodel.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTableMongoDBImpl implements CreateTable {

    public static final Logger logger = LoggerFactory.getLogger(CreateTableMongoDBImpl.class);

    public void createTable(Sheet sheet, String tableName)  {
     logger.info("MongoDB creates collections automatically while inserting documents");
    }

}
