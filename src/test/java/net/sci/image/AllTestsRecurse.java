package net.sci.image;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	net.sci.image.binary.AllTests.class,
	net.sci.image.io.AllTests.class,
	net.sci.image.morphology.AllTestsRecurse.class,
	net.sci.image.process.filter.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
