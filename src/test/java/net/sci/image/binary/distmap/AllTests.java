package net.sci.image.binary.distmap;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	ChamferDistanceTransform2DFloatTest.class,
	ChamferDistanceTransform2DUInt16Test.class,
	ChamferDistanceTransform3DFloatTest.class,
	ChamferDistanceTransform3DUInt16Test.class,
	})
public class AllTests {
  //nothing
}
