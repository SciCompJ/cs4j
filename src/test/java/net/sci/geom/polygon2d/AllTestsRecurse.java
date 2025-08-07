package net.sci.geom.polygon2d;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
    net.sci.geom.polygon2d.process.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
