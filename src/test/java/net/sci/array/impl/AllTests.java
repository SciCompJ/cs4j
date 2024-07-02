package net.sci.array.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    BufferedGenericArray2DTest.class,
    BufferedGenericArray3DTest.class,
    DefaultPositionIteratorTest.class,
    ReverseOrderPositionIteratorTest.class,
    })
public class AllTests {
  //nothing
}
