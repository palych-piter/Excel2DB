import java.io.File;

import excel2db.ApplicationContextUtils;
import excel2db.excel2db;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
@Category(JUnitPopulateTableCategory.class)
public class PopulateTableImplTest {

    private Sheet sheet;

    @Test
    public void testPopulateTable() throws Exception {

        //initializing application context
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        excel2db app = (excel2db) context.getBean("excel2db");

        //executing the test
        File fileName = new File(app.initConstants.workingDir + "/test.xlsx");

        sheet = app.initInputFiles.initInputFiles(fileName);
        app.dbConnection.establishDBConnection();
        app.createTable.createTable(sheet, "testTable");

        Integer numOfProcessedRows = app.populateTable.populateTable(sheet, "testTable");
        assertEquals(Integer.valueOf(2), numOfProcessedRows);

    }
}