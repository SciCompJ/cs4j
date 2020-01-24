/**
 * 
 */
package net.sci.array.process.shape;

import java.util.ArrayList;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;


/**
 * Remove array dimensions whose size is 1.
 * 
 * <pre>{@code
 *    // create an empty array with only one non-unit dimension
 *    UInt8Array array = UInt8Array.create(new int[]{1, 10, 1});
 *    
 *    // apply squeeze operation
 *    Squeeze op = new Squeeze();
 *    UInt8Array res = (UInt8Array) op.process(array);
 *    
 *    // the following should equal 1
 *    int newDim = res.dimensionality();
 * }</pre>
 * @author dlegland
 *
 */
public class Squeeze implements ArrayOperator
{
    /**
     * Constructor.
     */
    public Squeeze()
    {
    }

    /**
     * Remove array dimensions whose size is 1. If there is no dimension to
     * remove, returns the input array.
     * 
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
    @Override
    public <T> Array<T> process(Array<T> array)
    {
        int nd = array.dimensionality();
        int[] dims = array.size();
        
        // identify the dimensions that need to be retained
        ArrayList<Integer> dimsToKeep = new ArrayList<Integer>();
        for (int d = 0; d < dims.length; d++)
        {
            if (dims[d] > 1)
            {
                dimsToKeep.add(d);
            }
        }

        if  (dimsToKeep.isEmpty())
        {
            return array;
        }
        
        // compute dimensions of output array
        int nd2 = dimsToKeep.size();
        int[] dims2 = new int[nd2];
        for (int d = 0; d < nd2; d++)
        {
            dims2[d] = dims[dimsToKeep.get(d)];
        }
        
        // create output array
        Array<T> target = array.newInstance(dims2);
        
        // initialize source cursor
        int[] srcPos = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            srcPos[d] = 0;
        }

        // iterate over position of output array
        for (int[] pos : target.positions())
        {
            for (int d = 0; d < nd2; d++)
            {
                srcPos[dimsToKeep.get(d)] = pos[d];
            }
            target.set(array.get(srcPos), pos);
        }
        
        return target;
    }

}
