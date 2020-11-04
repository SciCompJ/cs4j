package net.sci.image.label.geoddist;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    GeodesicDistanceTransform2DUInt16Hybrid5x5Test.class,
    GeodesicDistanceTransform2DFloatHybrid5x5Test.class,
    })
public class AllTests {
  //nothing
}