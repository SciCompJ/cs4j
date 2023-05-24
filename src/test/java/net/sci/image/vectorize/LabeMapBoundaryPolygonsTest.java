/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.image.data.Connectivity2D;
import net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction;
import net.sci.image.vectorize.LabelMapBoundaryPolygons.Position;

/**
 * @author dlegland
 *
 */
public class LabeMapBoundaryPolygonsTest
{
    @Test
    public final void test_Down_turnLeft()
    {
        Direction direction = Direction.DOWN;
        Position pos = new Position(1, 2, Direction.DOWN);
        
        Position pos2 = direction.turnLeft(pos);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.RIGHT, pos2.direction);
    }
    
    @Test
    public final void test_Down_turnLeft2()
    {
        Direction direction = Direction.DOWN;
        Position pos = new Position(1, 1, direction);
        
        Position pos2 = direction.turnLeft(pos);
        assertEquals(1, pos2.x);
        assertEquals(1, pos2.y);
        assertEquals(Direction.RIGHT, pos2.direction);
    }
    
    @Test
    public final void test_Down_forward()
    {
        Direction direction = Direction.DOWN;
        Position pos = new Position(1, 1, direction);
        
        Position pos2 = direction.forward(pos);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.DOWN, pos2.direction);
    }

    @Test
    public final void test_Down_turnRight()
    {
        Direction direction = Direction.DOWN;
        Position pos = new Position(2, 1, direction);
        
        Position pos2 = direction.turnRight(pos);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.LEFT, pos2.direction);
    }
    
    @Test
    public final void test_Down_turnRight2()
    {
        Direction direction = Direction.DOWN;
        Position pos = new Position(2, 1, direction);
        
        Position pos2 = direction.turnRight(pos);
        assertEquals(1, pos2.x);
        assertEquals(2, pos2.y);
        assertEquals(Direction.LEFT, pos2.direction);
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_trackBoundary_Binary_singleSquare()
    {
        BinaryArray2D array = BinaryArray2D.create(4, 4);
        array.setBoolean(1, 1, true);
        array.setBoolean(2, 1, true);
        array.setBoolean(1, 2, true);
        array.setBoolean(2, 2, true);
        
        int x0 = 1;
        int y0 = 1;
        LabelMapBoundaryPolygons.Direction initialDirection = Direction.DOWN;
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons();
        ArrayList<Point2D> vertices = algo.trackBoundary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(8, vertices.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_trackBoundary_ExpandedCorners_C4()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 8);
        fillRect(array, 2, 2, 4, 4, 255);
        fillRect(array, 1, 1, 2, 2, 255);
        fillRect(array, 5, 1, 2, 2, 255);
        fillRect(array, 1, 5, 2, 2, 255);
        fillRect(array, 5, 5, 2, 2, 255);
        
        int x0 = 1;
        int y0 = 1;
        LabelMapBoundaryPolygons.Direction initialDirection = Direction.DOWN;
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons();
        ArrayList<Point2D> vertices = algo.trackBoundary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
   public final void test_trackBoundary_ExpandedCorners_C4_TouchBorders()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 6);
        
        fillRect(array, 1, 1, 4, 4, 255);
        fillRect(array, 0, 0, 2, 2, 255);
        fillRect(array, 4, 0, 2, 2, 255);
        fillRect(array, 0, 4, 2, 2, 255);
        fillRect(array, 4, 4, 2, 2, 255);
        
        int x0 = 0;
        int y0 = 0;
        LabelMapBoundaryPolygons.Direction initialDirection = Direction.DOWN;
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons();
        ArrayList<Point2D> vertices = algo.trackBoundary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_trackBoundary_ExpandedCorners_C8()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 8);
        fillRect(array, 2, 2, 4, 4, 255);
        fillRect(array, 1, 1, 2, 2, 255);
        fillRect(array, 5, 1, 2, 2, 255);
        fillRect(array, 1, 5, 2, 2, 255);
        fillRect(array, 5, 5, 2, 2, 255);
        
        int x0 = 1;
        int y0 = 1;
        LabelMapBoundaryPolygons.Direction initialDirection = Direction.DOWN;
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons(Connectivity2D.C8);
        ArrayList<Point2D> vertices = algo.trackBoundary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_trackBoundary_ExpandedCorners_C8_TouchBorders()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 6);
        fillRect(array, 1, 1, 4, 4, 255);
        fillRect(array, 0, 0, 2, 2, 255);
        fillRect(array, 4, 0, 2, 2, 255);
        fillRect(array, 0, 4, 2, 2, 255);
        fillRect(array, 4, 4, 2, 2, 255);
        
        int x0 = 0;
        int y0 = 0;
        LabelMapBoundaryPolygons.Direction initialDirection = Direction.DOWN;
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons(Connectivity2D.C8);
        ArrayList<Point2D> vertices = algo.trackBoundary(array, x0, y0, initialDirection);
        
        assertFalse(vertices.isEmpty());
        assertEquals(32, vertices.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_trackBoundary_NestedLabels()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 6);
        fillRect(array, 1, 1, 2, 2, 3);
        fillRect(array, 3, 1, 2, 2, 5);
        fillRect(array, 1, 3, 2, 2, 7);
        fillRect(array, 3, 3, 2, 2, 9);
        fillRect(array, 2, 2, 2, 2, 4);
        
        int x0 = 2;
        int y0 = 2;
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons(Connectivity2D.C4);
        ArrayList<Point2D> vertices = algo.trackBoundary(array, x0, y0, Direction.DOWN);
        
        assertFalse(vertices.isEmpty());
        assertEquals(8, vertices.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_process_square2x2()
    {
        UInt8Array2D array = UInt8Array2D.create(4, 4);
        fillRect(array, 1, 1, 2, 2, 3);
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons(Connectivity2D.C4);
        Map<Integer,ArrayList<Polygon2D>> boundaries = algo.process(array);
        
        assertFalse(boundaries.isEmpty());
        assertEquals(1, boundaries.size());
        
        Polygon2D poly3 = boundaries.get(3).get(0);
        assertEquals(8, poly3.vertexCount());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_process_FourLabels()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 6);
        fillRect(array, 1, 1, 2, 2, 3);
        fillRect(array, 3, 1, 2, 2, 5);
        fillRect(array, 1, 3, 2, 2, 7);
        fillRect(array, 3, 3, 2, 2, 9);
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons(Connectivity2D.C4);
        Map<Integer,ArrayList<Polygon2D>> boundaries = algo.process(array);
        
        assertFalse(boundaries.isEmpty());
        assertEquals(4, boundaries.size());
        
        Polygon2D poly3 = boundaries.get(3).get(0);
        assertEquals(8, poly3.vertexCount());
        Polygon2D poly5 = boundaries.get(5).get(0);
        assertEquals(8, poly5.vertexCount());
        Polygon2D poly7 = boundaries.get(7).get(0);
        assertEquals(8, poly7.vertexCount());
        Polygon2D poly9 = boundaries.get(9).get(0);
        assertEquals(8, poly9.vertexCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.LabelMapBoundaryPolygons#trackBoundary(net.sci.array.scalar.IntArray2D, int, int, net.sci.image.vectorize.LabelMapBoundaryPolygons.Direction)}.
     */
    @Test
    public final void test_process_squareWithHole()
    {
        UInt8Array2D array = UInt8Array2D.create(5, 5);
        fillRect(array, 1, 1, 3, 3, 255);
        array.setValue(2, 2, 0);
        
        LabelMapBoundaryPolygons algo = new LabelMapBoundaryPolygons(Connectivity2D.C4);
        Map<Integer,ArrayList<Polygon2D>> boundaries = algo.process(array);
        
        assertFalse(boundaries.isEmpty());
        assertEquals(1, boundaries.size());
        
        ArrayList<Polygon2D> polygons = boundaries.get(255);
        assertEquals(2, polygons.size());
        
        assertEquals(12, polygons.get(0).vertexCount());
        assertEquals(4, polygons.get(1).vertexCount());
    }
    
    private static final void fillRect(ScalarArray2D<?> array, int x0, int y0, int dx, int dy, double value)
    {
        for (int y = y0; y < y0 + dy; y++)
        {
            for (int x = x0; x < x0 + dx; x++)
            {
                array.setValue(x, y, value);
            }
        }
    }
}
