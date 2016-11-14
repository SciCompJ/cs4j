/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.Array.Iterator;
import net.sci.array.ArrayToArrayOperator;


/**
 * @author dlegland
 *
 */
public class ConvertToDouble implements ArrayToArrayOperator
{

	@Override
	public void process(Array<?> input, Array<?> output)
	{
		// TODO: check dims
		// TODO: check size
		// TODO: check types
		
		Iterator<?> iter1 = input.iterator(); 
		Iterator<?> iter2 = output.iterator();
		
		while(iter1.hasNext() && iter2.hasNext())
		{
			iter1.forward();
			iter2.forward();
			iter2.setValue(iter1.getValue());
		}
	}

}
