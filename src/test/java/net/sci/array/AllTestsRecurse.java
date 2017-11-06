package net.sci.array;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	net.sci.array.data.AllTestsRecurse.class,
    net.sci.array.process.AllTests.class,
    net.sci.array.type.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
