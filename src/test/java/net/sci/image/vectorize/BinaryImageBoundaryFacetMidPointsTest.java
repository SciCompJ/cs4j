/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.geom.geom2d.Point2D;

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
        List<Point2D> pts = BinaryImageBoundaryFacetMidPoints.reduce(algo.processBinary2d(array));
        
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
        List<Point2D> pts = BinaryImageBoundaryFacetMidPoints.reduce(algo.processBinary2d(array));
        
        assertEquals(12, pts.size());
    }
    
}
