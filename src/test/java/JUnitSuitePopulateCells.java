

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Categories.IncludeCategory(JUnitPopulateCellsCategory.class)
@Suite.SuiteClasses(PopulateCellsImplTest.class)
public class JUnitSuitePopulateCells {

}


