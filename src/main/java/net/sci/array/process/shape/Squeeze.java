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
            target.set(pos, array.get(srcPos));
        }
        
        return target;
    }

}