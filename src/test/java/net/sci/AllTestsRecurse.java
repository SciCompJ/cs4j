package net.sci;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	net.sci.array.AllTestsRecurse.class,
	net.sci.image.AllTestsRecurse.class,
	net.sci.optim.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
