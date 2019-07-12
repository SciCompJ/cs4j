package net.sci.geom.geom3d;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
    net.sci.geom.geom3d.surface.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
