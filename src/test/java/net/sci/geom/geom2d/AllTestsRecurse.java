package net.sci.geom.geom2d;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
    net.sci.geom.geom2d.curve.AllTests.class,
    net.sci.geom.geom2d.graph.AllTests.class,
	net.sci.geom.geom2d.line.AllTests.class,
	net.sci.geom.geom2d.polygon.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
