package net.sci.array.scalar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    ScalarArrayTest.class,
    UInt8ArrayTest.class,
    UInt8ArrayWrapperTest.class,
    // 2D implementations
	BufferedDoubleArray2DTest.class,
	BufferedInt16Array2DTest.class,
	BufferedUInt16Array2DTest.class,
	BufferedUInt8Array2DTest.class,
    // 3D implementations
    UInt8Array3DTest.class,
    BufferedUInt8Array3DTest.class,
    SlicedUInt8Array3DTest.class,
    // ND implementations
    BufferedInt32ArrayNDTest.class,
    BufferedUInt8ArrayNDTest.class,
	})
public class AllTests {
  //nothing
}
