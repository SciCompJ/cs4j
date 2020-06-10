/**
 * 
 */
package net.sci.array.vector;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class VectorTest
{

    /**
     * Test method for {@link net.sci.array.vector.Vector#norm(double[])}.
     */
    @Test
    public void testNormDoubleArray()
    {
        // use a pair of Pythagorean triples
        Float64Vector vector = new Float64Vector(new double[] {3.0, 12.0, 4.0} );
        double norm = Vector.norm(vector.getValues());
        assertEquals(13.0, norm, 0.01);
    }

    /**
     * Test method for {@link net.sci.array.vector.Vector#maxNorm(double[])}.
     */
    @Test
    public void testMaxNormDoubleArray()
    {
        // use a pair of Pythagorean triples
        Float64Vector vector = new Float64Vector(new double[] {3.0, 12.0, 4.0} );
        double norm = Vector.maxNorm(vector.getValues());
        assertEquals(12.0, norm, 0.01);
    }

}
