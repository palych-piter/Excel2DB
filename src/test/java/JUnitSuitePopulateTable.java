import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Categories.class)
@Categories.IncludeCategory(JUnitPopulateTableCategory.class)
@Suite.SuiteClasses({PopulateTablePostgresImplTest.class})
public class JUnitSuitePopulateTable {

}
