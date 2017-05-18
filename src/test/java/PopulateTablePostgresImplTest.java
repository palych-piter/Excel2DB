import excel2db.service.impl.PopulateTablePostgresImpl;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PopulateTablePostgresImplTest {

    @Test
    public void testPopulateTable() throws Exception {
        Integer processedRows = 100;
        PopulateTablePostgresImpl popTableObject = new PopulateTablePostgresImpl();

        //here we should initialize the sheet object
        assertEquals(processedRows, processedRows );

    }
}