package net.sci.array.binary;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    // type classes
    BinaryTest.class,
    // 2D abstract classes
    BinaryArray2DTest.class,
    // 3D abstract classes
	BinaryArray3DTest.class,
    // Wrappers and views
    ThresholdedArrayTest.class,
	})
public class AllTests {
  //nothing
}
