/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom3d.Point3D;

/**
 * 
 */
public class BinaryImageBoundaryFacetMidPointsTest
{
    
    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImageBoundaryFacetMidPoints#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processBinary2d_rect()
    {
        BinaryArray2D array = BinaryArray2D.of(new boolean[][] {
            {false, false, false, false, false},
            {false,  true,  true,  true, false},
            {false,  true,  true,  true, false},
            {false, false, false, false, false},
        });
        
        BinaryImageBoundaryFacetMidPoints algo = new BinaryImageBoundaryFacetMidPoints();
        List<Point2D> pts = algo.processBinary2d(array);
        
        assertEquals(10, pts.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImageBoundaryFacetMidPoints#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processBinary2d_touchBorder()
    {
        BinaryArray2D array = BinaryArray2D.of(new boolean[][] {
                { false, true, false }, 
                {  true, true,  true }, 
                { false, true, false }, 
        });
        
        BinaryImageBoundaryFacetMidPoints algo = new BinaryImageBoundaryFacetMidPoints();
        List<Point2D> pts = algo.processBinary2d(array);
        
        assertEquals(12, pts.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImageBoundaryFacetMidPoints#processBinary3d(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void test_processBinary3d_cube()
    {
        BinaryArray3D array = BinaryArray3D.create(5, 4, 3);
        array.setBoolean(1, 1, 1, true);
        array.setBoolean(2, 1, 1, true);
        array.setBoolean(3, 1, 1, true);
        array.setBoolean(1, 2, 1, true);
        array.setBoolean(2, 2, 1, true);
        array.setBoolean(3, 2, 1, true);
        
        BinaryImageBoundaryFacetMidPoints algo = new BinaryImageBoundaryFacetMidPoints();
        List<Point3D> pts = algo.processBinary3d(array);
        
        // cuboid 3x2x1
        // 2x6 = 12 boundary points in z-direction
        // 2x3 =  6 boundary points in y-direction
        // 2x2 =  4 boundary points in x-direction
        // total: 22
        assertEquals(22, pts.size());
    }
    
    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImageBoundaryFacetMidPoints#processBinary2d(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processBinary3d_touchBorder()
    {
        BinaryArray3D array = BinaryArray3D.create(3, 2, 1);
        array.setBoolean(0, 0, 0, true);
        array.setBoolean(1, 0, 0, true);
        array.setBoolean(2, 0, 0, true);
        array.setBoolean(0, 1, 0, true);
        array.setBoolean(1, 1, 0, true);
        array.setBoolean(2, 1, 0, true);
        
        BinaryImageBoundaryFacetMidPoints algo = new BinaryImageBoundaryFacetMidPoints();
        List<Point3D> pts = algo.processBinary3d(array);
        
        assertEquals(22, pts.size());
    }
    
}
