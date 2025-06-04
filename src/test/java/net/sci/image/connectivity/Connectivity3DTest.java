/**
 * 
 */
package net.sci.image.connectivity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class Connectivity3DTest
{
    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity3D#convert(net.sci.image.connectivity.Connectivity)}.
     */
    @Test
    public final void testWrap_conn2dC4()
    {
        Connectivity2D conn2d = Connectivity2D.C4;
        Connectivity3D conn = Connectivity3D.convert(conn2d);
        
        assertEquals(4, conn.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity3D#convert(net.sci.image.connectivity.Connectivity)}.
     */
    @Test
    public final void testWrap_conn2dC8()
    {
        Connectivity2D conn2d = Connectivity2D.C8;
        Connectivity3D conn = Connectivity3D.convert(conn2d);
        
        assertEquals(8, conn.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity#offsets()}.
     */
    @Test
    public final void testOffsets_C6()
    {
        Connectivity3D conn = Connectivity3D.C6;
        assertEquals(6, conn.offsets().size());
    }

    /**
     * Test method for {@link net.sci.image.connectivity.Connectivity#offsets()}.
     */
    @Test
    public final void testOffsets_C26()
    {
        Connectivity3D conn = Connectivity3D.C26;
        assertEquals(26, conn.offsets().size());
    }

}
