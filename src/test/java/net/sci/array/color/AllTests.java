package net.sci.array.color;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    RGB8ArrayTest.class,
    RGB8Array3DTest.class,
    RGB8ArrayChannelViewTest.class,
    RGB16ArrayTest.class,
    RGB16Array3DTest.class,
    RGB16ArrayChannelViewTest.class,
    VectorArrayRGB8ViewTest.class,
    })
public class AllTests {
  //nothing
}
