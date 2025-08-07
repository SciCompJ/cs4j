package net.sci.geom.geom2d;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
    net.sci.geom.geom2d.curve.AllTests.class,
    net.sci.geom.polygon2d.AllTestsRecurse.class,
	})
public class AllTestsRecurse {
  //nothing
}
