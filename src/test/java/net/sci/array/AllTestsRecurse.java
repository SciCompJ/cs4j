package net.sci.array;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    net.sci.array.AllTests.class,
    net.sci.array.type.AllTests.class,
    net.sci.array.scalar.AllTests.class,
    net.sci.array.color.AllTests.class,
    net.sci.array.generic.AllTests.class,
    net.sci.array.process.AllTestsRecurse.class,
	})
public class AllTestsRecurse {
  //nothing
}
