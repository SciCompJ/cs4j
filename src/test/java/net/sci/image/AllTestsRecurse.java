package net.sci.image;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    AllTests.class,
    net.sci.image.analyze.AllTests.class,
	net.sci.image.binary.AllTestsRecurse.class,
    net.sci.image.io.AllTests.class,
    net.sci.image.label.AllTests.class,
	net.sci.image.morphology.AllTestsRecurse.class,
	net.sci.image.process.AllTestsRecurse.class,
	})
public class AllTestsRecurse {
  //nothing
}
