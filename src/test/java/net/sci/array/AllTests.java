package net.sci.array;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    Array2DTest.class,
    Array3DTest.class,
    ArrayNDTest.class,
    DefaultPositionIteratorTest.class,
	})
public class AllTests {
  //nothing
}
