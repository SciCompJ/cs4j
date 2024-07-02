/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Pads an array with a number of elements in each direction.
 * 
 * @author dlegland
 *
 */
public class Padding
{
    public final static <T> Array<T> pad(Array<T> array, int pad, T padValue)
    {
        return new PaddedArray<T>(array, pad, padValue);
    }
    
    public final static <T> Array<T> pad(Array<T> array, int[] padBefore, int[] padAfter, T padValue)
    {
        return new PaddedArray<T>(array, padBefore, padAfter, padValue);
    }
    
    public final static <S extends Scalar<S>> ScalarArray<S> padScalar(ScalarArray<S> array, int pad, double padValue)
    {
        return new PaddedScalarArray<S>(array, pad, padValue);
    }
    
    public final static <S extends Scalar<S>> ScalarArray<S> padScalar(ScalarArray<S> array, int[] padBefore, int[] padAfter, double padValue)
    {
        return new PaddedScalarArray<S>(array, padBefore, padAfter, padValue);
    }
    
    public final static BinaryArray padBinary(BinaryArray array, int pad, boolean padValue)
    {
        return new PaddedBinaryArray(array, pad, padValue);
    }
    
    public final static BinaryArray padBinary(BinaryArray array, int[] padBefore, int[] padAfter, boolean padValue)
    {
        return new PaddedBinaryArray(array, padBefore, padAfter, padValue);
    }
    
    /**
     * Padding of a scalar array.
     * 
     * Array res = new Padding.PaddedArray(array, 1, paddingObj); 
     */
    public static class PaddedArray<T> implements Array<T>
    {
        Array<T> array;
        
        int[] padBefore;
        int[] padAfter;
        T padObj;
        int[] paddedSize;
        
        PaddedArray(Array<T> array, int pad, T padValue)
        {
            this(array, repInt(pad, array.dimensionality()), repInt(pad, array.dimensionality()), padValue);
        }
        
        PaddedArray(Array<T> array, int[] padBefore, int[] padAfter, T padValue)
        {
            // check dimensions
            int nd = array.dimensionality();
            if (padBefore.length != nd || padAfter.length != nd)
            {
                throw new IllegalArgumentException("Padding value arrays must have same length as array dimensionlity: " + nd);
            }
            
            // initialize class members
            this.array = array;
            this.padBefore = padBefore;
            this.padAfter = padAfter;
            this.padObj = padValue;
            
            // compute size of padded array
            this.paddedSize = new int[nd];
            for (int d = 0; d < nd; d++)
            {
                this.paddedSize[d] = array.size(d) + padBefore[d] + padAfter[d];
            }
        }
        
        @Override
        public T get(int[] pos)
        {
            int[] pos2 = shiftPos(pos, padBefore);
            return containsPos(pos2, array.size()) ? array.get(pos2) : padObj;
        }

        @Override
        public void set(int[] pos, T value)
        {
            throw new RuntimeException("Can not modify a padded array");
        }

        @Override
        public int dimensionality()
        {
            return padBefore.length;
        }

        @Override
        public int[] size()
        {
           return this.paddedSize;
        }

        @Override
        public int size(int dim)
        {
            return this.paddedSize[dim];
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
    }
    
    /**
     * Padding of a scalar array.
     * 
     * ScalarArray res = new Padding.PaddedScalarArray(array, 1, 42.0); 
     */
    public static class PaddedScalarArray<S extends Scalar<S>> extends PaddedArray<S> implements ScalarArray<S>
    {
        double padValue;
        
        PaddedScalarArray(ScalarArray<S> array, int pad, double padValue)
        {
            this(array, repInt(pad, array.dimensionality()), repInt(pad, array.dimensionality()), padValue);
        }
        
        PaddedScalarArray(ScalarArray<S> array, int[] padBefore, int[] padAfter, double padValue)
        {
            super(array, padBefore, padAfter, array.createElement(padValue));
            this.padValue = padValue;
        }
        
        @Override
        public double getValue(int[] pos)
        {
            int[] pos2 = shiftPos(pos, padBefore);
            return containsPos(pos2, array.size()) ? ((ScalarArray<S>) array).getValue(pos2) : padValue;
        }

        @Override
        public void setValue(int[] pos, double state)
        {
            throw new RuntimeException("Can not modify a padded array");
        }

        @Override
        public ScalarArray<S> newInstance(int... dims)
        {
            return ((ScalarArray<S>) array).newInstance(dims);
        }

        @Override
        public ScalarArray.Factory<S> factory()
        {
            return ((ScalarArray<S>) array).factory();
        }
    }
    
    
    /**
     * Padding of a binary array.
     * 
     * BinaryArray res = new Padding.PaddedBinaryArray(array, 1, false); 
     */
    public static class PaddedBinaryArray implements BinaryArray
    {
        BinaryArray array;
        int[] padBefore;
        int[] padAfter;
        boolean padValue;
        int[] paddedSize;
        
        PaddedBinaryArray(BinaryArray array, int pad, boolean padValue)
        {
            this(array, repInt(pad, array.dimensionality()), repInt(pad, array.dimensionality()), padValue);
        }
        
        PaddedBinaryArray(BinaryArray array, int[] padBefore, int[] padAfter, boolean padValue)
        {
            // check dimensions
            int nd = array.dimensionality();
            if (padBefore.length != nd || padAfter.length != nd)
            {
                throw new IllegalArgumentException("Padding value arrays must have same length as array dimensionlity: " + nd);
            }
            
            // initialize class variables
            this.array = array;
            this.padBefore = padBefore;
            this.padAfter = padAfter;
            this.padValue = padValue;
            
            // compute size of padded array
            this.paddedSize = new int[nd];
            for (int d = 0; d < nd; d++)
            {
                this.paddedSize[d] = array.size(d) + padBefore[d] + padAfter[d];
            }
        }
        
        @Override
        public boolean getBoolean(int[] pos)
        {
            int[] pos2 = shiftPos(pos, padBefore);
            return containsPos(pos2, array.size()) ? array.getBoolean(pos2) : padValue;
        }

        @Override
        public void setBoolean(int[] pos, boolean state)
        {
            throw new RuntimeException("Can not modify a padded array");
        }

        @Override
        public int dimensionality()
        {
            return padBefore.length;
        }

        @Override
        public int[] size()
        {
           return this.paddedSize;
        }

        @Override
        public int size(int dim)
        {
            return this.paddedSize[dim];
        }
    }
    
    
    private static final int[] repInt(int value, int n)
    {
        int[] res = new int[n];
        for (int i = 0; i < n; i++) res[i] = value;
        return res;
    }
    
    private static final int[] shiftPos(int[] pos, int[] shift)
    {
        int[] pos2 = new int[pos.length];
        for (int d = 0; d < pos.length; d++)
        {
            // compute position within inner array
            pos2[d] = pos[d] - shift[d];
        }
        return pos2;
    }
    
    private final static boolean containsPos(int[] pos, int[] size)
    {
        for (int d = 0; d < pos.length; d++)
        {
            if (pos[d] < 0) return false;
            if (pos[d] >= size[d]) return false;
        }
        return true;
    }
}
