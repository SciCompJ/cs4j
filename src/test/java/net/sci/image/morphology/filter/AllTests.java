package net.sci.image.morphology.filter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    BoxDilationNaiveTest.class,
    CubeStrel3DTest.class,
    LinearZStrel3DTest.class,
    SquareStrelTest.class,
	})
public class AllTests {
  //nothing
}