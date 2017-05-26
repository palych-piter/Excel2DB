package excel2db.service.impl;

import java.sql.SQLException;
import excel2db.service.PopulateTable;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopulateTableOracleImpl implements PopulateTable {

    public static final Logger logger = LoggerFactory.getLogger(PopulateTablePostgresImpl.class);

    private Integer numOfProcessedRows = 0;

    public Integer populateTable(Sheet sheet, String tableName)
            throws SQLException {

        logger.info("Populate table method : Oracle implementation is not covered yet");

        return numOfProcessedRows;

    }

}
