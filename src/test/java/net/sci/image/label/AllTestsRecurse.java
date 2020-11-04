package net.sci.image.label;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	AllTests.class,
    net.sci.image.label.distmap.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
