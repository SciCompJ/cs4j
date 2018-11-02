package net.sci.array.vector;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    Float32VectorArray3DTest.class,
    Float32VectorArray2DChannelViewTest.class,
    Float32VectorArray3DChannelViewTest.class,
    Float64VectorArray3DTest.class,
    Float64VectorArray2DChannelViewTest.class,
    Float64VectorArray3DChannelViewTest.class,
	})
public class AllTests {
  //nothing
}
