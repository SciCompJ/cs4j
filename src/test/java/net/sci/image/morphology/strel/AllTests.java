package net.sci.image.morphology.strel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    // Utility  classes
    LocalHistogramDoubleTest.class,
    
	// 2D structuring element classes
    BoxDilationNaiveTest.class,
    SquareStrelTest.class,
    NaiveDiskStrelTest.class,
    SlidingDiskStrelTest.class,
    
    // 3D structuring element classes
    LinearZStrel3DTest.class,
    CubeStrel3DTest.class,
    NaiveBallStrel3DTest.class,
	})
public class AllTests {
  //nothing
}