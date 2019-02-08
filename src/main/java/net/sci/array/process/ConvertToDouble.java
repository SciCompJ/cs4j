/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.Array.PositionIterator;
import net.sci.array.ArrayOperator;
import net.sci.array.Arrays;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.ScalarArray;


/**
 * @author dlegland
 *
 */
public class ConvertToDouble implements ArrayOperator
{
	@Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
	    if (!(array instanceof ScalarArray))
	    {
	        throw new RuntimeException("Requires a scalar array");
	    }
        Float32Array result = Float32Array.create(array.size());
        
        PositionIterator iter = array.positionIterator();
        while(iter.hasNext())
        {
            int[] pos = iter.next();
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
        PositionIterator iter = source.positionIterator();
        while(iter.hasNext())
        {
            int[] pos = iter.next();
            target.setValue(pos, ((ScalarArray<?>) source).getValue(pos));
        }

        // return reference to target
        return target;
    }
}
