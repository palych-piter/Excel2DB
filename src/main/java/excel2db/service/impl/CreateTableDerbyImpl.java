package excel2db.service.impl;

/**
 * Created by Andrey on 6/21/2017.
 */

import java.sql.SQLException;

import excel2db.service.CreateTable;
import excel2db.service.PopulateTable;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CreateTableDerbyImpl implements CreateTable {

    public static final Logger logger = LoggerFactory.getLogger(PopulateTableOracleImpl.class);

    private Integer numOfProcessedRows = 0;

    public void createTable(Sheet sheet, String tableName)
            throws SQLException {

        logger.info("Create table method : Derby implementation is not covered yet");

    }

}
