package net.sci.geom.geom2d;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// 0-dimensional geometric elements
	Point2DTest.class,
    Vector2DTest.class,
    AffineTransform2DTest.class,
    
    // 1-dimensional geometric elements
    LinearGeometry2DTest.class,
    LineSegment2DTest.class,
    StraightLine2DTest.class,
	})
public class AllTests {
  //nothing
}
