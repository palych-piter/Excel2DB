import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;

import excel2db.excel2db;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
@Category(JUnitPopulateCellsCategory.class)
@TestPropertySource("/excel2db.properties")

public class PopulateCellsImplTest {

    private Sheet sheet;
    private String resultSetValue;

    @Test
    public void testPopulateCellsImpl() throws Exception {

        //initializing application context
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        excel2db app = (excel2db) context.getBean("excel2db");
        //executing the test
        File fileName = new File(app.initConstants.workingDir + "/test.xlsx");

        sheet = app.initInputFiles.initInputFiles(fileName);

        //create and populate a table
        app.dbConnection.establishDBConnection();
        app.createTable.createTable(sheet, "TEST");
        app.populateTable.populateTable(sheet, "TEST");
        ResultSet rs = app.getFirstRow.getFirstRow("TEST");

        //concatenate values in the result set row
        while (rs.next()){
            resultSetValue =
                            rs.getString("Column1") + " " +
                            rs.getString("Column2") + " " +
                            rs.getString("Column3") + " " +
                            rs.getString("Column4") + " " +
                            rs.getString("Column5") + " " +
                            rs.getString("Column6") + " " +
                            rs.getString("Column7") + " " +
                            rs.getString("Column8") + " " +
                            rs.getString("Column9") + " " +
                            rs.getString("Column10")
            ;
        }

        //compare with the expected result
        assertEquals("Value 11 Value 12 Value 13 Value 14 Value 15 Value 16 Value 17 Value 18 Value 19 Value 110", resultSetValue);

    }

}
