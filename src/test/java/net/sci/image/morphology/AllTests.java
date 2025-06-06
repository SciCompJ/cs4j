package net.sci.image.morphology;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    FloodFillTest.class,
    MinimaAndMaximaTest.class,
    MorphologicalReconstructionTest.class,
	})
public class AllTests {
  //nothing
}