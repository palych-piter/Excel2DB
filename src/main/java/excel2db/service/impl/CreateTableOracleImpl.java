package excel2db.service.impl;

import java.sql.SQLException;
import excel2db.service.CreateTable;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTableOracleImpl implements CreateTable {

    public static final Logger logger = LoggerFactory.getLogger(CreateTableOracleImpl.class);

    public void createTable(Sheet sheet, String tableName) throws SQLException {

        logger.info("Create table method : Oracle implementation is not covered yet");

    }

}
