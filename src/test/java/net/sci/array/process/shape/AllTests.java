package net.sci.array.process.shape;

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
	SlicerTest.class,
	})
public class AllTests {
  //nothing
}
