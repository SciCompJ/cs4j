/**
 * 
 */
package net.sci.array.process.type;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Arrays;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.ScalarArray;


/**
 * @author dlegland
 *
 */
public class ConvertToDouble extends AlgoStub implements ArrayOperator
{
	@Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
	    if (!(array instanceof ScalarArray))
	    {
	        throw new RuntimeException("Requires a scalar array");
	    }
        Float32Array result = Float32Array.create(array.size());
        
        for(int[] pos : result.positions())
        {
            result.setValue(pos, ((ScalarArray<?>) array).getValue(pos));
        }

        return result;
    }

    public ScalarArray<?> convert(ScalarArray<?> source, ScalarArray<?> target)
    {
        // check inputs
        if (!Arrays.isSameDimensionality(source, target))
        {
            throw new IllegalArgumentException("Both arrays must have same dimensionality");
        }
        if (!Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Both arrays must have same dimensions");
        }
        
        // iterate over positions
        for(int[] pos : target.positions())
        {
            target.setValue(pos, ((ScalarArray<?>) source).getValue(pos));
        }

        // return reference to target
        return target;
    }
}
