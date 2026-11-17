/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;

/**
 * 
 */
public class ScalarArrayOperatorTest
{
    /**
     * Test method for {@link net.sci.array.numeric.process.ScalarArrayOperator#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process_UInt8Array()
    {
        UInt8Array2D array = UInt8Array2D.create(20, 10);
        array.fillValues((x, y) -> 20.0 * y + x);
        
        ScalarArrayOperator op = new Sqrt();
        Array<?> res = op.process(array);
        
        assertTrue(res.elementInstanceOf(Scalar.class));
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        // some value check
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ScalarArray2D scalarRes = ScalarArray2D.wrap(ScalarArray.wrap((Array<? extends Scalar>) res));
        assertEquals(0.0, scalarRes.getValue(0, 0), 0.01);
        assertEquals(3.0, scalarRes.getValue(9, 0), 0.01);
        assertEquals(9.0, scalarRes.getValue(1, 4), 0.01);
        assertEquals(12.0, scalarRes.getValue(4, 7), 0.01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.ScalarArrayOperator#process(net.sci.array.Array)}.
     */
    @Test
    public final void test_process_ArrayOfUInt8()
    {
        Array2D<UInt8> array = Array2D.create(20, 10, UInt8.ZERO);
        array.fill((x, y) -> new UInt8(20 * y + x));
        
        ScalarArrayOperator op = new Sqrt();
        Array<?> res = op.process(array);
        
        assertTrue(res.elementInstanceOf(Scalar.class));
        assertEquals(2, res.dimensionality());
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        // some value check
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ScalarArray2D scalarRes = ScalarArray2D.wrap(ScalarArray.wrap((Array<? extends Scalar>) res));
        assertEquals(0.0, scalarRes.getValue(0, 0), 0.01);
        assertEquals(3.0, scalarRes.getValue(9, 0), 0.01);
        assertEquals(9.0, scalarRes.getValue(1, 4), 0.01);
        assertEquals(12.0, scalarRes.getValue(4, 7), 0.01);
    }

    /**
     * Create a dummy class that operate on ScalarArray instances.
     */
    private class Sqrt extends AlgoStub implements ScalarArrayOperator
    {
        @Override
        public ScalarArray<?> processScalar(ScalarArray<?> array)
        {
            ScalarArray<?> res = array.duplicate();
            res.fillValues(pos -> Math.sqrt(array.getValue(pos)));
            return res;
        }
    }
}
