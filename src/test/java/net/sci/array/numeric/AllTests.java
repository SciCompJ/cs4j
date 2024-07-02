package net.sci.array.numeric;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.sci.array.numeric.impl.BufferedDoubleArray2DTest;
import net.sci.array.numeric.impl.BufferedInt16Array2DTest;
import net.sci.array.numeric.impl.BufferedInt32ArrayNDTest;
import net.sci.array.numeric.impl.BufferedUInt16Array2DTest;
import net.sci.array.numeric.impl.BufferedUInt8Array2DTest;
import net.sci.array.numeric.impl.BufferedUInt8Array3DTest;
import net.sci.array.numeric.impl.BufferedUInt8ArrayNDTest;
import net.sci.array.numeric.impl.FileMappedFloat32Array3DTest;
import net.sci.array.numeric.impl.FileMappedUInt16Array3DTest;
import net.sci.array.numeric.impl.FileMappedUInt8Array3DTest;
import net.sci.array.numeric.impl.FloatBufferFloat32Array2DTest;
import net.sci.array.numeric.impl.ScalarArrayUInt8ViewTest;
import net.sci.array.numeric.impl.ShortBufferUInt16Array2DTest;
import net.sci.array.numeric.impl.SlicedUInt8Array3DTest;
import net.sci.array.numeric.impl.UInt8Array_ScalarArrayWrapperTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    // generic array classes
    ScalarArrayTest.class,
    UInt8ArrayTest.class,
    // 2D abstract classes
    ScalarArray2DTest.class,
    Float32Array2DTest.class,
    UInt8Array2DTest.class,
    // 2D implementations
	BufferedDoubleArray2DTest.class,
	BufferedInt16Array2DTest.class,
	BufferedUInt16Array2DTest.class,
	BufferedUInt8Array2DTest.class,
    FloatBufferFloat32Array2DTest.class,
    ShortBufferUInt16Array2DTest.class,
    // 3D abstract classes
    ScalarArray3DTest.class,
    UInt8Array3DTest.class,
    UInt16Array3DTest.class,
    Int16Array3DTest.class,
    Int32Array3DTest.class,
    Float32Array3DTest.class,
    Float64Array3DTest.class,
    // 3D implementations
    BufferedUInt8Array3DTest.class,
    SlicedUInt8Array3DTest.class,
    FileMappedUInt8Array3DTest.class,
    FileMappedUInt16Array3DTest.class,
    FileMappedFloat32Array3DTest.class,
    // ND implementations
    BufferedInt32ArrayNDTest.class,
    BufferedUInt8ArrayNDTest.class,
    // Wrappers and views
    UInt8Array_ScalarArrayWrapperTest.class,
    ScalarArrayUInt8ViewTest.class,
	})
public class AllTests {
  //nothing
}
