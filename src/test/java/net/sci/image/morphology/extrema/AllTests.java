package net.sci.image.morphology.extrema;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    MaximaImpositionTest.class,
    MinimaImpositionTest.class,
    RegionalExtrema2DTest.class,
    RegionalExtrema2DGenericTest.class,
    RegionalExtrema3DTest.class,
	})
public class AllTests {
  //nothing
}