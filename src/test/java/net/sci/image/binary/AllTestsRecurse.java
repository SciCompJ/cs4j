package net.sci.image.binary;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
    net.sci.image.binary.distmap.AllTests.class,
    net.sci.image.binary.geoddist.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
