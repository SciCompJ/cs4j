package net.sci.image.process;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
    net.sci.image.process.filter.AllTests.class,
    net.sci.image.process.segment.AllTests.class,
    net.sci.image.process.shape.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
