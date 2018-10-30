package net.sci.geom;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    net.sci.geom.geom2d.AllTestsRecurse.class,
    net.sci.geom.geom3d.AllTestsRecurse.class,
	})
public class AllTestsRecurse {
  //nothing
}
