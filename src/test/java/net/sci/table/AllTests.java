package net.sci.table;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    NumericColumnTest.class,
    FloatColumnTest.class,
    IntegerColumnTest.class,
    LogicalColumnTest.class,
    TableTest.class,
    NumericTableTest.class,
	})
public class AllTests {
  //nothing
}