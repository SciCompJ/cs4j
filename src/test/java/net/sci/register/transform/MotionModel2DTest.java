/**
 * 
 */
package net.sci.register.transform;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class MotionModel2DTest
{
    
    /**
     * Test method for {@link net.sci.register.transform.MotionModel2D#affineMatrix()}.
     */
    @Test
    public final void testAffineMatrix()
    {
        double tx = 30;
        double ty = 20;
        double angle = 10;
        MotionModel2D transfo = new MotionModel2D(new double[] {tx, ty, angle});
        
        double[][] matrix = transfo.affineMatrix();
        assertEquals(matrix.length, 3);
        assertEquals(matrix[0].length, 3);
        double cot = Math.cos(Math.toRadians(angle));
        double sit = Math.sin(Math.toRadians(angle));
        assertEquals(matrix[0][0], cot, 0.001);
        assertEquals(matrix[0][1], -sit, 0.001);
        assertEquals(matrix[0][2],  tx, 0.001);
        assertEquals(matrix[1][0], sit, 0.001);
        assertEquals(matrix[1][1], cot, 0.001);
        assertEquals(matrix[1][2],  ty, 0.001);
    }    
}
