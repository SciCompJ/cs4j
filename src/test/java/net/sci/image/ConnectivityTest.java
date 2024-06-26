/**
 * 
 */
package net.sci.image;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class ConnectivityTest
{
    /**
     * Test method for {@link net.sci.image.Connectivity#convertDimensionality(net.sci.image.Connectivity, int)}.
     */
    @Test
    public final void testConvertDimensionality_3D_to_2D_C6()
    {
        Connectivity conn = Connectivity3D.C6;
        
        Connectivity res = Connectivity.convertDimensionality(conn, 2);
        
        assertEquals(2, res.dimensionality());
        assertEquals(4, res.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.Connectivity#convertDimensionality(net.sci.image.Connectivity, int)}.
     */
    @Test
    public final void testConvertDimensionality_3D_to_2D_C26()
    {
        Connectivity conn = Connectivity3D.C26;
        
        Connectivity res = Connectivity.convertDimensionality(conn, 2);
        
        assertEquals(2, res.dimensionality());
        assertEquals(8, res.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.Connectivity#convertDimensionality(net.sci.image.Connectivity, int)}.
     */
    @Test
    public final void testConvertDimensionality_2D_to_3D_C4()
    {
        Connectivity conn = Connectivity2D.C4;
        
        Connectivity res = Connectivity.convertDimensionality(conn, 3);
        
        assertEquals(3, res.dimensionality());
        assertEquals(4, res.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.Connectivity#convertDimensionality(net.sci.image.Connectivity, int)}.
     */
    @Test
    public final void testConvertDimensionality_2D_to_3D_C8()
    {
        Connectivity conn = Connectivity2D.C8;
        
        Connectivity res = Connectivity.convertDimensionality(conn, 3);
        
        assertEquals(3, res.dimensionality());
        assertEquals(8, res.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.Connectivity#convertDimensionality(net.sci.image.Connectivity, int)}.
     */
    @Test
    public final void testConvertDimensionality_2D_to_3D_C6()
    {
        Connectivity conn = Connectivity2D.C6_1;
        
        Connectivity res = Connectivity.convertDimensionality(conn, 3);
        
        assertEquals(3, res.dimensionality());
        assertEquals(6, res.offsets().size());
    }

}
