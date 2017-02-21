package net.sci.image.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    MetaImageReaderTest.class,
    RawImageReaderTest.class,
	TiffImageReaderTest.class,
	})
public class AllTests {
  //nothing
}