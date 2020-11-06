package net.sci.image.binary.geoddist;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    GeodesicDistanceTransform2DUInt16Hybrid5x5Test.class,
    GeodesicDistanceTransform2DUInt16Scanning5x5Test.class,
    GeodesicDistanceTransform2DFloat32Hybrid5x5Test.class,
    GeodesicDistanceTransform2DFloat32Scanning5x5Test.class,
    GeodesicDistanceTransform3DFloat32Hybrid3x3Test.class,
    })
public class AllTests {
  //nothing
}