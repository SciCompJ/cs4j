/**
 * 
 */
package net.sci.image.vectorize;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.geom.geom2d.Geometry2D;
import net.sci.geom.graph.Graph2D;

/**
 * @author dlegland
 *
 */
public class IsocontourTest
{

    /**
     * Test method for
     * {@link net.sci.image.vectorize.Isocontour#processScalar2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testProcessScalar2d()
    {
        UInt8Array2D array = UInt8Array2D.create(4, 4);
        array.setInt(1, 1, 10);
        array.setInt(2, 1, 10);
        array.setInt(1, 2, 10);
        array.setInt(2, 2, 10);

        Isocontour algo = new Isocontour(5.0);

        Geometry2D geom = algo.computeGraph(array);

        assertFalse(geom == null);
    }

    /**
     * Test method for
     * {@link net.sci.image.vectorize.Isocontour#computeGraph(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testComputeGraph_simpleSquare()
    {
        UInt8Array2D array = UInt8Array2D.create(4, 4);
        array.setInt(1, 1, 10);
        array.setInt(2, 1, 10);
        array.setInt(1, 2, 10);
        array.setInt(2, 2, 10);

        Isocontour algo = new Isocontour(5.0);

        Graph2D graph = algo.computeGraph(array);

        assertEquals(8, graph.vertexCount());
        assertEquals(8, graph.edgeCount());
    }

}
