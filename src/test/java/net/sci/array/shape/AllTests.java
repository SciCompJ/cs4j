package net.sci.array.shape;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    ConcatenateTest.class,
    CropTest.class,
    FlipTest.class,
	OrthogonalProjectionTest.class,
	PermuteDimensionsTest.class,
    Rotate90Test.class,
    Rotate3D90Test.class,
    SimpleSlicerTest.class,
    SlicerTest.class,
    SqueezeTest.class,
    SubSampleTest.class,
	})
public class AllTests {
  //nothing
}
