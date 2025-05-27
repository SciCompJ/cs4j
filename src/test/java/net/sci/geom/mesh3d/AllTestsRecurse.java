package net.sci.geom.mesh3d;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    net.sci.geom.mesh3d.AllTests.class,
    net.sci.geom.mesh3d.io.AllTests.class,
    net.sci.geom.mesh3d.process.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
