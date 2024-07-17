package net.sci.image.segmentation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    HysteresisThresholdTest.class,
    KMeansSegmentationTest.class,
    OtsuThresholdTest.class,
	})
public class AllTests {
  //nothing
}