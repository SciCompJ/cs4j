/**
 * 
 */
package net.sci.register.transform;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class SimilarityModel2DTest
{
    
    /**
     * Test method for {@link net.sci.register.transform.SimilarityModel2D#affineMatrix()}.
     */
    @Test
    public final void testAffineMatrix()
    {
        double tx = 30;
        double ty = 20;
        double angle = 10;
        double logk = 0.5;
        SimilarityModel2D transfo = new SimilarityModel2D(new double[] {tx, ty, angle, logk});
        
        double[][] matrix = transfo.affineMatrix();
        assertEquals(matrix.length, 3);
        assertEquals(matrix[0].length, 3);
        
        double cot = Math.cos(Math.toRadians(angle));
        double sit = Math.sin(Math.toRadians(angle));
        double k = Math.pow(2.0, logk);
        
        assertEquals(matrix[0][0], k * cot, 0.001);
        assertEquals(matrix[0][1], -k * sit, 0.001);
        assertEquals(matrix[0][2], tx, 0.001);
        assertEquals(matrix[1][0], k * sit, 0.001);
        assertEquals(matrix[1][1], k * cot, 0.001);
        assertEquals(matrix[1][2],  ty, 0.001);
    }
    
}
