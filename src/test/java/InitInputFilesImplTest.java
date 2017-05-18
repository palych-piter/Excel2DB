import java.io.File;

import excel2db.excel2db;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;

public class InitInputFilesImplTest {

    @Test
    public void testInitInputFilesImpl() throws Exception {

        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        excel2db app = (excel2db) context.getBean("excel2db");
        File fileName = new File(app.initConstants.workingDir + "/unitTest.xls");

        //here we should initialize the sheet object
        assertEquals("UnitTestSheet", app.initInputFiles.initInputFiles(fileName).getSheetName());

    }

}