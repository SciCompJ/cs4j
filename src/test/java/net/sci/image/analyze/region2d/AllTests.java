package net.sci.image.analyze.region2d;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    ConvexHullTest.class,
    GeodesicDiameterTest.class,
    MaxFeretDiameterTest.class,
    RegionBoundariesTest.class,
	})
public class AllTests {
  //nothing
}
