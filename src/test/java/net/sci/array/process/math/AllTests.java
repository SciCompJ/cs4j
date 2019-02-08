package net.sci.array.process.math;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.sci.array.process.math.PowerOfTwoTest;
import net.sci.array.process.math.SqrtTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    FiniteDifferencesTest.class,
    GradientTest.class,
    PowerOfTwoTest.class,
	SqrtTest.class,
	})
public class AllTests {
  //nothing
}
