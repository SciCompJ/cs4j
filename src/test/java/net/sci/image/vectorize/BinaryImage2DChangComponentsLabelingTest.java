/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.geom.polygon2d.Polyline2D;

/**
 * @author dlegland
 *
 */
public class BinaryImage2DChangComponentsLabelingTest
{
    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImage2DChangComponentsLabeling#getResult(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_singleSquare()
    {
        BinaryArray2D array = BinaryArray2D.create(6, 6);
        array.setBoolean(2, 2, true);
        array.setBoolean(3, 2, true);
        array.setBoolean(2, 3, true);
        array.setBoolean(3, 3, true);
        
        BinaryImage2DChangComponentsLabeling algo = new BinaryImage2DChangComponentsLabeling();
        
        BinaryImage2DChangComponentsLabeling.Result res = algo.getResult(array);
        
        assertEquals(1, res.outerContours.size());
        assertEquals(0, res.innerContours.size());
        
        Polyline2D poly = res.outerContours.iterator().next();
        assertEquals(4, poly.vertexCount());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImage2DChangComponentsLabeling#getResult(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_threeSquares_touchCorners()
    {
        BinaryArray2D array = BinaryArray2D.create(13, 10);
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                array.setBoolean(j + 2, i + 2, true);
                array.setBoolean(j + 8, i + 2, true);
                array.setBoolean(j + 5, i + 5, true);
            }
        }
        
        BinaryImage2DChangComponentsLabeling algo = new BinaryImage2DChangComponentsLabeling();
        
        BinaryImage2DChangComponentsLabeling.Result res = algo.getResult(array);
        
        assertEquals(1, res.outerContours.size());
        assertEquals(0, res.innerContours.size());
    }

    /**
     * Test method for {@link net.sci.image.vectorize.BinaryImage2DChangComponentsLabeling#getResult(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testProcess_ThickRing()
    {
        BinaryArray2D array = BinaryArray2D.create(10, 10);
        for (int i = 2; i < 8; i++)
        {
            for (int j = 2; j < 8; j++)
            {
                array.setBoolean(j, i, true);
            }
        }
        for (int i = 4; i < 6; i++)
        {
            for (int j = 4; j < 6; j++)
            {
                array.setBoolean(j, i, false);
            }
        }

        BinaryImage2DChangComponentsLabeling algo = new BinaryImage2DChangComponentsLabeling();
        
        BinaryImage2DChangComponentsLabeling.Result res = algo.getResult(array);
        
        assertEquals(1, res.outerContours.size());
        assertEquals(1, res.innerContours.size());
    }

}
