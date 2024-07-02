package net.sci;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	net.sci.array.AllTests.class,
    net.sci.geom.AllTestsRecurse.class,
    net.sci.image.AllTestsRecurse.class,
    net.sci.optim.AllTests.class,
    net.sci.table.AllTestsRecurse.class,
    })
public class AllTestsRecurse {
  //nothing
}
