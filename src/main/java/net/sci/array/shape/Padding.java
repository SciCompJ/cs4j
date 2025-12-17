/**
 * 
 */
package net.sci.array.shape;

import java.util.Collection;
import java.util.List;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.NumericArray;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Pads an array with a number of elements in each direction.
 * 
 * @author dlegland
 *
 */
public class Padding extends AlgoStub implements ArrayOperator
{
    // =============================================================
    // Static methods

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
    
    public final static <S extends Scalar<S>> ScalarArray<S> padScalar(ScalarArray<S> array, int[] padBefore,
            int[] padAfter, double padValue)
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
    
    
    // =============================================================
    // Static methods

    /**
     * An interface for specifying how to determine padding value from an array
     * and a position that can be outside the array. Provides also a number of
     * common modes as final specialized instances of the Mode interface.
     */
    public static interface Mode
    {
        /**
         * determine padding value from an array and a position that can be
         * outside the array.
         * 
         * @param <T>
         *            the type of data within the array
         * @param array
         *            the array to pad
         * @param pos
         *            an array of integers corresponding to a position (that can
         *            be outside the array)
         * @return a value associated to the position
         */
        public <T> T get(Array<T> array, int[] pos);
        
        /**
         * Associates the value zero to each position outside the original array.
         * Requires a numeric array to work properly. 
         */
        public static Mode ZERO = new Mode()
        {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T get(Array<T> array, int[] pos)
            {
                if (array.containsPosition(pos)) return array.get(pos);
                
                if (array instanceof NumericArray)
                {
                    return (T) ((NumericArray<?>) array).sampleElement().zero();
                }
                else
                {
                    throw new RuntimeException("Extension with a constant requires a numeric array");
                }
            }
        };
        
        /**
         * Associates the largest possible value for the array type to each
         * position outside the original array. Requires a scalar array to work
         * properly.
         */
        public static Mode TYPE_MAX = new Mode()
        {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T get(Array<T> array, int[] pos)
            {
                if (array.containsPosition(pos)) return array.get(pos);
                
                if (array instanceof ScalarArray)
                {
                    return (T) ((ScalarArray<?>) array).sampleElement().typeMax();
                }
                else
                {
                    throw new RuntimeException("Extension with a type max requires a scalar array");
                }
            }
        };
        
        /**
         * Associates the closest element of the original array to each position outside
         * the bounds of the array.
         */
        public static Mode REPLICATE = new Mode()
        {
            @Override
            public <T> T get(Array<T> array, int[] pos)
            {
                if (array.containsPosition(pos)) return array.get(pos);
                
                int[] pos2 = new int[pos.length];
                for (int d = 0; d < pos.length; d++)
                {
                    pos2[d] = Math.max(0, Math.min(pos[d], array.size(d)-1));
                }
                return array.get(pos2);
            }
        };
        
        /**
         * Creates a mirror along each dimension of the elements array.
         * 
         * For example, an array with values {@code [1 2 3 4 5]} and padding
         * sizes equal to 3 in main direction will result to an array with
         * values {@code [3 2 1 1 2 3 4 5 5 4 3]}.
         */
        public static Mode MIRROR = new Mode()
        {
            @Override
            public <T> T get(Array<T> array, int[] pos)
            {
                if (array.containsPosition(pos)) return array.get(pos);
                
                int[] pos2 = new int[pos.length];
                for (int d = 0; d < pos.length; d++)
                {
                    pos2[d] = newPos(pos[d], array.size(d));
                }
                return array.get(pos2);
            }
            
            private static final int newPos(int x, int size)
            {
                x = x % (2 * size);
                if (x < 0) x = -x - 1;
                if (x >= size) x = 2 * size - 1 - x;
                return x;
            }
        };
        
        /**
         * Creates a periodic repetition of the original array along each dimension.
         * 
         * For example, an array with values {@code [1 2 3 4 5]} and padding
         * sizes equal to 3 in main direction will result to an array with
         * values {@code [3 4 5 1 2 3 4 5 1 2 3]}.
         */
        public static Mode PERIODIC = new Mode()
        {
            @Override
            public <T> T get(Array<T> array, int[] pos)
                        {
                if (array.containsPosition(pos)) return array.get(pos);
                
                int[] pos2 = new int[pos.length];
                for (int d = 0; d < pos.length; d++)
                {
                    pos2[d] = newPos(pos[d], array.size(d));
                }
                return array.get(pos2);
            }
            
            private static final int newPos(int x, int size)
            {
                x = x % size;
                if (x < 0) x += size;
                return x;
            }
        };
    }
    
    

    // =============================================================
    // Class members

    /**
     * The padding size before and after the array, along each dimension, as a
     * Nd-by-2 array of non-negative integers.
     */
    int[][] padSizes;
    
    /**
     * The padding strategy.
     */
    Mode mode;
    
    
    // =============================================================
    // Constructors

    public Padding(int[] padSizes, Mode mode)
    {
        int nd = padSizes.length;
        this.padSizes = new int[nd][2];
        for (int d = 0; d < nd; d++)
        {
            this.padSizes[d][0] = this.padSizes[d][1] = padSizes[d];
        }
        
        this.mode = mode;
    }
    
    
    // =============================================================
    // Processing methods

    public<T> Array<?> createView(Array<T> array)
    {
        return new View<T>(array, padSizes, mode);
    }
    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        int nd = array.dimensionality();
        if (this.padSizes.length != nd) throw new RuntimeException("Dimension of extents and array must be consistent");
        
        int[] newDims = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            newDims[d] = array.size(d) + padSizes[d][0] + padSizes[d][1];
        }
        
        // allocate memory for result
        Array<T> res = array.newInstance(newDims);
        
        // fill result
        for(int[] pos : res.positions())
        {
            int[] pos2 = new int[pos.length];
            for (int d = 0; d < pos.length; d++)
            {
                pos2[d] = pos[d] - this.padSizes[d][0];
            }
            res.set(pos, this.mode.get(array, pos2));
        }
        
        return res;
    }


    // =============================================================
    // Inner classes

    private static class View<T> implements Array.View<T>
    {
        Array<T> array;
        int[] dims;
        int[] shifts;
        Mode mode;
        
        protected View(Array<T> array, int[][] extents, Mode mode)
        {
            this.array = array;
            this.mode = mode;
            
            int nd = extents.length;
            this.dims = new int[nd];
            this.shifts = new int[nd];
            for (int d = 0; d < nd; d++)
            {
                this.dims[d] = array.size(d) + extents[d][0] + extents[d][1];
                this.shifts[d] = extents[d][0];
            }
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
            return mode.get(array, shiftPos(pos, shifts));
        }

        @Override
        public void set(int[] pos, T value)
        {
            throw new RuntimeException("Can not modify such an array with extended borders");
        }

        @Override
        public Class<T> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public int[] size()
        {
            return dims;
        }

        @Override
        public int size(int dim)
        {
            return dims[dim];
        }

        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }

        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(array);
        }
    }
    
    
    /**
     * Padding of a scalar array.
     * 
     * Array res = new Padding.PaddedArray(array, 1, paddingObj); 
     */
    public static class PaddedArray<T> implements Array<T>, Array.View<T>
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
        public Collection<Array<?>> parentArrays()
        {
            return List.of(array);
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
    public static class PaddedBinaryArray implements BinaryArray, Array.View<Binary>
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
        public Collection<Array<?>> parentArrays()
        {
            return List.of(array);
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
