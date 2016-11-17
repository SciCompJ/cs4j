package net.sci.array.data;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
	net.sci.array.data.scalar2d.AllTests.class,
	net.sci.array.data.scalar3d.AllTests.class,
	net.sci.array.data.scalarnd.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
