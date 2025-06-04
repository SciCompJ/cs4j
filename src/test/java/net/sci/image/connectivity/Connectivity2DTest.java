/**
 * 
 */
package net.sci.image.connectivity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class Connectivity2DTest
{
    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity2D#convert(net.sci.image.connectivity.Connectivity)}.
     */
    @Test
    public final void testWrap_conn3dC6()
    {
        Connectivity3D conn3d = Connectivity3D.C6;
        Connectivity2D conn = Connectivity2D.convert(conn3d);
        
        assertEquals(4, conn.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity2D#convert(net.sci.image.connectivity.Connectivity)}.
     */
    @Test
    public final void testWrap_conn3dC26()
    {
        Connectivity3D conn3d = Connectivity3D.C26;
        Connectivity2D conn = Connectivity2D.convert(conn3d);
        
        assertEquals(8, conn.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity#offsets()}.
     */
    @Test
    public final void testOffsets_length_C4()
    {
        Connectivity2D conn = Connectivity2D.C4;
        assertEquals(4, conn.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity#offsets()}.
     */
    @Test
    public final void testOffsets_length_C8()
    {
        Connectivity2D conn = Connectivity2D.C8;
        assertEquals(8, conn.offsets().size());
    }

}
