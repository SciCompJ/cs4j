package net.sci.image.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    BinaryDataReaderTest.class,
    ImageBinaryDataReaderTest.class,
    MetaImageReaderTest.class,
    MetaImageWriterTest.class,
    PgmImageReaderTest.class,
    RawImageReaderTest.class,
    TiffImageReaderTest.class,
	})
public class AllTests {
  //nothing
}