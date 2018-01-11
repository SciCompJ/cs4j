/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.Float32Array;
import net.sci.array.data.ScalarArray;


/**
 * @author dlegland
 *
 */
public class ConvertToDouble implements ArrayOperator
{
	@Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
        Float32Array result = Float32Array.create(array.getSize());
        
        Array.Iterator<?> iter1 = array.iterator(); 
        ScalarArray.Iterator<?> iter2 = result.iterator();
        
        while(iter1.hasNext() && iter2.hasNext())
        {
            iter2.setNextValue(iter1.nextValue());
        }

        return result;
    }

    public <T> ScalarArray<?> convert(Array<T> array, ScalarArray<?> output)
    {
        // TODO: check dim
        // TODO: check size
        
        Array.Iterator<?> iter1 = array.iterator(); 
        ScalarArray.Iterator<?> iter2 = output.iterator();
        
        while(iter1.hasNext() && iter2.hasNext())
        {
            iter2.setNextValue(iter1.nextValue());
        }

        return output;
    }
}
