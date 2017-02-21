package net.sci.image.morphology;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	FloodFill2DTest.class,
    FloodFill3DTest.class,
    LabelImagesTest.class,
	})
public class AllTests {
  //nothing
}