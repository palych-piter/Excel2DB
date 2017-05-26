// verify if the application reads the Excel sheet by checking sheet name

import java.io.File;

import excel2db.ApplicationContextUtils;
import excel2db.excel2db;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

//specify category of the class + runner
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring.xml")
@Category(JUnitInitFileCategory.class)
@TestPropertySource("/excel2db.properties")
public class InitInputFilesImplTest {

    @Test
    public void testInitInputFilesImpl() throws Exception {

        //initializing application context
        //need to check if we really need to get the spring.xml if the config is in "locations"
//        AbstractApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
//        ApplicationContextUtils appcontext= (ApplicationContextUtils) context.getBean("applicationContextUtils");
//        ApplicationContext appCon = appcontext.getApplicationContext();
//        excel2db app = (excel2db) appCon.getBean("excel2db");

        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        excel2db app = (excel2db) context.getBean("excel2db");

        //executing the test
        File fileName = new File(app.initConstants.workingDir + "/unitTest.xls");

        //here we should initialize the sheet object
        assertEquals("UnitTestSheet", app.initInputFiles.initInputFiles(fileName).getSheetName());

    }

}