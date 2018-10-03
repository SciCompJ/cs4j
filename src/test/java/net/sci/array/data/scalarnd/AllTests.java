package net.sci.array.data.scalarnd;

import net.sci.array.scalar.BufferedInt32ArrayNDTest;
import net.sci.array.scalar.BufferedUInt8ArrayNDTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
	BufferedInt32ArrayNDTest.class,
	BufferedUInt8ArrayNDTest.class,
	})
public class AllTests {
  //nothing
}
