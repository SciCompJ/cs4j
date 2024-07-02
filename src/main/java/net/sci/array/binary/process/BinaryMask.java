/**
 * 
 */
package net.sci.array.binary.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.impl.ArrayWrapperStub;

/**
 * Creates new array by keeping only values for which binary mask is set to
 * <code>true</code>.
 * 
 * @author dlegland
 *
 */
public class BinaryMask extends AlgoStub
{
    public static final <T> Array<T> createView(Array<T> array, BinaryArray mask)
    {
        return new View<T>(array, mask);
    }
    
    public <T> Array<T> process(Array<T> array, BinaryArray mask)
    {
        // check input validity
        if (!Arrays.isSameSize(array, mask))
        {
            throw new IllegalArgumentException("Both arrays must have same size");
        }
        
        // dispatch according to array dimensionality
        if (array.dimensionality() == 2)
        {
            return process2d(array, mask);
        }
        else if (array.dimensionality() == 3)
        {
            return process3d(array, mask);
        }
        
        // process ND case 
        
        // allocate result array
        Array<T> res = array.newInstance(array.size());
        
        // iterate over positions
        for (int[] pos : res.positions())
        {
            if (mask.getBoolean(pos))
            {
                res.set(pos, array.get(pos));
            }
        }
        
        // return result
        return res;
    }
    
    private <T> Array<T> process2d(Array<T> array, BinaryArray mask)
    {
        // retrieve array dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // allocate result array
        Array<T> res = array.newInstance(new int[] {sizeX, sizeY});
        
        // iterate over 2D positions
        int[] pos = new int[2];
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            pos[1] = y;
            for (int x = 0; x < sizeX; x++)
            {
                pos[0] = x;
                
                if (mask.getBoolean(pos))
                {
                    res.set(pos, array.get(pos));
                }
            }
        }
        
        // return result array
        return res;
    }

    private <T> Array<T> process3d(Array<T> array, BinaryArray mask)
    {
        // retrieve array dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // allocate result array
        Array<T> res = array.newInstance(new int[] {sizeX, sizeY, sizeZ});
        
        // iterate over 3D positions
        int[] pos = new int[3];
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            pos[2] = z;
            for (int y = 0; y < sizeY; y++)
            {
                pos[1] = y;
                for (int x = 0; x < sizeX; x++)
                {
                    pos[0] = x;

                    if (mask.getBoolean(pos))
                    {
                        res.set(pos, array.get(pos));
                    }
                }
            }
        }
        
        // return result array
        return res;
    }
    
    static class View<T> extends ArrayWrapperStub<T>
    {
        /**
         * The array containing the original values.
         */
        Array<T> array;
        
        /**
         * The binary mask, the same size as the array, used to select which
         * elements of the array must be returned.
         */
        BinaryArray mask;
        
        /**
         * The value returned when the corresponding mask element is set to
         * <code>false</code>.
         */
        T zero;
        
        public View(Array<T> array, BinaryArray mask)
        {
            super(array);
            
            // check same size
            if (!Arrays.isSameSize(array, mask))
            {
                throw new IllegalArgumentException("Both arrays must have same size");
            }
            
            // keep references 
            this.array = array;
            this.mask = mask;
            
            // initialize "zero" element
            zero = array.newInstance(new int[] {1,1}).get(new int[] {0, 0});
        }

        @Override
        public Class<T> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public Array<T> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public Factory<T> factory()
        {
            return array.factory();
        }

        @Override
        public T get(int[] pos)
        {
            return mask.getBoolean(pos) ? array.get(pos) : zero;
        }

        @Override
        public void set(int[] pos, T value)
        {
            throw new RuntimeException("Can not modify values of a View Array");
        }
    }
}
