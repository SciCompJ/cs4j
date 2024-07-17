package net.sci.image.morphology;



import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	net.sci.image.morphology.AllTests.class,
    net.sci.image.morphology.strel.AllTests.class,
    net.sci.image.morphology.filtering.AllTests.class,
    net.sci.image.morphology.extrema.AllTests.class,
    net.sci.image.morphology.reconstruction.AllTests.class,
    net.sci.image.morphology.watershed.AllTests.class,
	})
public class AllTestsRecurse {
  //nothing
}
