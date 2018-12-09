package net.sci.image;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    CalibrationTest.class,
    ImageAxisXTest.class,
    ImageAxisYTest.class,
    ImageAxisZTest.class,
    ImageAxisTTest.class,
    })
public class AllTests {
  //nothing
}
