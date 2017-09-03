import java.io.File;

import excel2db.excel2db;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONObject;
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
    private String resultSetValue = "";

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
        JSONObject jsonResultSet = app.getFirstRow.getFirstRow("TEST");

        //compare with the expected result
        assertEquals( "Value 11",  jsonResultSet.getString("Column1"));
        assertEquals( "Value 12",  jsonResultSet.getString("Column2"));
        assertEquals( "Value 13",  jsonResultSet.getString("Column3"));
        assertEquals( "Value 14",  jsonResultSet.getString("Column4"));
        assertEquals( "Value 15",  jsonResultSet.getString("Column5"));
        assertEquals( "Value 16",  jsonResultSet.getString("Column6"));
        assertEquals( "Value 17",  jsonResultSet.getString("Column7"));
        assertEquals( "Value 18",  jsonResultSet.getString("Column8"));
        assertEquals( "Value 19",  jsonResultSet.getString("Column9"));
        assertEquals( "Value 110", jsonResultSet.getString("Column10"));


    }

}
