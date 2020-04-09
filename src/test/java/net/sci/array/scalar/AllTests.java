package net.sci.array.scalar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    // type classes
    BooleanTest.class,
    // generic array classes
    ScalarArrayTest.class,
    UInt8ArrayTest.class,
    // 2D abstract classes
    ScalarArray2DTest.class,
    BinaryArray2DTest.class,
    Float32Array2DTest.class,
    UInt8Array2DTest.class,
    // 2D implementations
	BufferedDoubleArray2DTest.class,
	BufferedInt16Array2DTest.class,
	BufferedUInt16Array2DTest.class,
	BufferedUInt8Array2DTest.class,
    // 3D abstract classes
    ScalarArray3DTest.class,
	BinaryArray3DTest.class,
    UInt8Array3DTest.class,
    UInt16Array3DTest.class,
    Int16Array3DTest.class,
    Int32Array3DTest.class,
    Float32Array3DTest.class,
    Float64Array3DTest.class,
    // 3D implementations
    BufferedUInt8Array3DTest.class,
    SlicedUInt8Array3DTest.class,
    // ND implementations
    BufferedInt32ArrayNDTest.class,
    BufferedUInt8ArrayNDTest.class,
    // Wrappers and views
    UInt8ArrayWrapperTest.class,
    ScalarArrayUInt8ViewTest.class,
    ScalarArrayThresholdViewTest.class,
    ThresholdedArrayTest.class,
	})
public class AllTests {
  //nothing
}
