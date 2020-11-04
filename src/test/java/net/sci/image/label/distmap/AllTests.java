package net.sci.image.label.distmap;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    ChamferDistanceTransform2DFloat32Test.class,
    ChamferDistanceTransform2DUInt16Test.class,
	})
public class AllTests {
  //nothing
}