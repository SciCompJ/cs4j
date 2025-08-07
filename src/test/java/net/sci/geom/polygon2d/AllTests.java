package net.sci.geom.polygon2d;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// generic classes
    Box2DTest.class,
    DefaultLinearRing2DTest.class,
    DefaultLineString2DTest.class,
    DefaultPolygon2DTest.class,
    LinearRing2DTest.class,
    LineString2DTest.class,
    Polygons2DTest.class,
    })
public class AllTests {
  //nothing
}
