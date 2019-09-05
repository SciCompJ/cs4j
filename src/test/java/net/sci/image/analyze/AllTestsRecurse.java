package net.sci.image.analyze;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
	net.sci.image.analyze.region2d.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
