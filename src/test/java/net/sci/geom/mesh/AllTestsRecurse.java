package net.sci.geom.mesh;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    net.sci.geom.mesh.AllTests.class,
    net.sci.geom.mesh.io.AllTests.class,
    net.sci.geom.mesh.process.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
