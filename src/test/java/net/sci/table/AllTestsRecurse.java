package net.sci.table;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    net.sci.table.cluster.AllTests.class,
    net.sci.table.io.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}