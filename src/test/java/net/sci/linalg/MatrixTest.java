/**
 * 
 */
package net.sci.linalg;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class MatrixTest
{
    
    /**
     * Test method for {@link net.sci.linalg.Matrix#times(net.sci.linalg.Matrix)}.
     */
    @Test
    public final void testTimes()
    {
        int nr = 3;
        int nc = 2;
        Matrix mat = Matrix.create(nr, nc);
        mat.set(0, 0, 1);
        mat.set(1, 0, 2);
        mat.set(2, 0, 3);
        mat.set(0, 1, 4);
        mat.set(1, 1, 5);
        mat.set(2, 1, 6);

        Matrix res = mat.transpose().times(mat);
        
        assertEquals(nc, res.getSize(0));
        assertEquals(nc, res.getSize(1));
        
        assertEquals(14, res.get(0, 0), .01);
        assertEquals(32, res.get(1, 0), .01);
        assertEquals(32, res.get(0, 1), .01);
        assertEquals(77, res.get(1, 1), .01);
    }
    
    /**
     * Test method for {@link net.sci.linalg.Matrix#transpose()}.
     */
    @Test
    public final void testTranspose()
    {
        int nr = 3;
        int nc = 2;
        Matrix mat = Matrix.create(nr, nc);

        Matrix res = mat.transpose();
        assertEquals(2, res.getSize(0));
        assertEquals(3, res.getSize(1));
    }
    
}
