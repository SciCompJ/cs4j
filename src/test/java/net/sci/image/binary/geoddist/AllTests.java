package net.sci.image.binary.geoddist;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    GeodesicDistanceTransform2DShort5x5ScanningTest.class,
    GeodesicDistanceTransform2DFloat5x5ScanningTest.class,
	})
public class AllTests {
  //nothing
}