package net.sci.array.process;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    net.sci.array.process.AllTests.class,
    net.sci.array.process.math.AllTests.class,
    net.sci.array.process.numeric.AllTests.class,
    net.sci.array.process.shape.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
