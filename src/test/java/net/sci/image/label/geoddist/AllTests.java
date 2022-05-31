package net.sci.image.label.geoddist;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    GeodesicDistanceTransform2DUInt16HybridTest.class,
    GeodesicDistanceTransform2DFloat32HybridTest.class,
    })
public class AllTests {
  //nothing
}