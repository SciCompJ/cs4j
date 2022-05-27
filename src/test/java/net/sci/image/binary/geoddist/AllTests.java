package net.sci.image.binary.geoddist;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    GeodesicDistanceTransform2DUInt16HybridTest.class,
    GeodesicDistanceTransform2DUInt16Scanning5x5Test.class,
    GeodesicDistanceTransform2DFloat32HybridTest.class,
    GeodesicDistanceTransform2DFloat32Scanning5x5Test.class,
    GeodesicDistanceTransform3DFloat32HybridTest.class,
    })
public class AllTests {
  //nothing
}