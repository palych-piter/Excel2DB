package excel2db.service.impl;

import java.util.ArrayList;
import java.util.Iterator;

import com.mongodb.client.MongoCollection;
import excel2db.service.PopulateTable;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static excel2db.excel2db.mongoDB;


public class PopulateTableMongoDBImpl implements PopulateTable {

    public static final Logger logger = LoggerFactory.getLogger(PopulateTableMongoDBImpl.class);

    private Integer numOfProcessedRows = 0;


    public Integer populateTable(Sheet sheet, String tableName){

        Iterator<Row> rowIterator = sheet.rowIterator();

        Row row = (Row) rowIterator.next();

        //get column names from a header
        short minColIdx = row.getFirstCellNum();
        short maxColIdx = row.getLastCellNum();

        ArrayList<String> coulumnNameList = new ArrayList();
        String columnName;

        logger.info("The table {} is being populated", tableName);

        //populate a list of column names
        for (short colIdx = minColIdx; colIdx < maxColIdx; colIdx = (short) (colIdx + 1)) {
            coulumnNameList.add(row.getCell(colIdx) == null? "": row.getCell(colIdx).toString());
        }

        while (rowIterator.hasNext()) {

            Document document = new Document();
            Row rowData = (Row) rowIterator.next();

            numOfProcessedRows++;
            for (short colIdx = minColIdx; colIdx < maxColIdx; colIdx = (short) (colIdx + 1)) {
                document.put(coulumnNameList.get(colIdx), rowData.getCell(colIdx).toString());
            }

            //save the document into a collection, point to the database
            MongoCollection mongoCollection = mongoDB.getCollection(tableName);
            mongoCollection.insertOne(document);

        }
        return numOfProcessedRows;
    }
}
