/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Arrays;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.VectorArray;

/**
 * @author dlegland
 *
 */
public class Gradient extends AlgoStub implements ArrayOperator
{

    /* (non-Javadoc)
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
    @Override
    public <T> VectorArray<?,?> process(Array<T> array)
    {
        // check validity of input
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires an instance of ScalarArray as input");
        }
        
        // allocate memory for result
        ScalarArray<?> source = (ScalarArray<?>) array;
        VectorArray<?,?> target = Float32VectorArray.create(source.size(), source.dimensionality());
        
        processScalar(source, target);
        
        return target;
    }

    public void processScalar(ScalarArray<?> source, VectorArray<?,?> target)
    {
        // check dimensionality of inputs
        checkArrayDimensions(source, target);

        int nd = source.dimensionality();
        for (int d = 0; d < nd; d++)
        {
            FiniteDifferences diffOp = new FiniteDifferences(d);
            diffOp.processScalar(source, target.channel(d));
        }
    }
    
    private void checkArrayDimensions(ScalarArray<?> source, VectorArray<?,?> target)
    {
        if (target.channelCount() != source.dimensionality())
        {
            throw new IllegalArgumentException("Target array must have at least " + source.dimensionality() + " channels");
        }
        if (!Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Source and Target arrays must have same dimensions");
        }
    }

}
