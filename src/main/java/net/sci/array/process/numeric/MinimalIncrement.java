/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Float32;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64Array;
import net.sci.array.scalar.Int;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.ScalarArray;

/**
 * Increments the values within an array by the smallest possible amount. Can be
 * used to count elements and store counting results within an array.
 * 
 * Can be used either via the <code>process(...)</code> method, in that case a
 * new array is created, or via the <code>createView(...)</code> method, in that
 * case a view instance is created, that increments values on the fly.
 * 
 * Implemented for different types:
 * <ul>
 * <li>Int types (UInt8, UInt16, Int16, Int32): adds value 1 to the current
 * value</li>
 * <li>Float32 and Float64: uses Math.nextUp() to increment current value</li>
 * </ul>
 */
public class MinimalIncrement extends AlgoStub implements ScalarArrayOperator
{
    /**
     * Creates an incremented view on the input array. The incremented values
     * are computed only when required, without having to allocate memory for
     * result.
     * 
     * Implemented for <code>Int</code>, <code>Float32</code> and
     * <code>Float64</code> types.
     * 
     * @param array
     *            the array to increment.
     * @return a view on the original array that returns the smallest possible
     *         incremented values
     * @throws RuntimeException
     *             if the array does not contain element of managed types.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final Array<?> createView(Array<?> array)
    {
        // dispatch processing according to type of element contained by array
        if (Int.class.isAssignableFrom(array.elementClass()))
        {
            return new IntView(IntArray.wrap((Array<? extends Int>) array));
        }
        else if (Float32.class.isAssignableFrom(array.elementClass()))
        {
            return new Float32View(Float32Array.wrap((Array<Float32>) array));
        }
        else if (Float64.class.isAssignableFrom(array.elementClass()))
        {
            return new Float64View(Float64Array.wrap((Array<Float64>) array));
        }
        else
        {
            throw new RuntimeException("Can not process array containing elements with class: " + array.elementClass());
        }
    }
    
    /**
     * Increments the values within the input array by the smallest possible
     * amount, and returns the newly created array.
     * 
     * Implemented for <code>Int</code>, <code>Float32</code> and
     * <code>Float64</code> types.
     * 
     * @param array
     *            the array to increment.
     * @return a new instance the same type as the input array containing the
     *         incremented values. incremented values
     * @throws RuntimeException
     *             if the array does not contain element of managed types.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        // dispatch processing according to type of element contained by array
        if (Int.class.isAssignableFrom(array.elementClass()))
        {
            return processInt(IntArray.wrap((Array<? extends Int>) array));
        }
        else if (Float32.class.isAssignableFrom(array.elementClass()))
        {
            return processFloat32(Float32Array.wrap(array));
        }
        else if (Float64.class.isAssignableFrom(array.elementClass()))
        {
            return processFloat64(Float64Array.wrap(array));
        }
        else
        {
            throw new RuntimeException("Can not process array containing elements with class: " + array.elementClass());
        }
    }
    
    public IntArray<?> processInt(IntArray<?> array)
    {
        IntArray<?> res = array.newInstance(array.size());
        for (int[] pos : array.positions())
        {
            res.setInt(pos, array.getInt(pos) + 1);
        }
        return res;
    }

    public Float32Array processFloat32(Float32Array array)
    {
        Float32Array res = array.newInstance(array.size());
        for (int[] pos : array.positions())
        {
            res.setFloat(pos, Math.nextUp(array.getFloat(pos)));
        }
        return res;
    }
    
    public Float64Array processFloat64(Float64Array array)
    {
        Float64Array res = array.newInstance(array.size());
        for (int[] pos : array.positions())
        {
            res.setValue(pos, Math.nextUp(array.getValue(pos)));
        }
        return res;
    }
    
    public static class IntView<I extends Int<I>> extends ArrayWrapperStub<I> implements IntArray<I>
    {
        public IntView(IntArray<I> array)
        {
            super(array);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<I> elementClass()
        {
            return ((IntArray<I>) array).elementClass();
        }

        @SuppressWarnings("unchecked")
        @Override
        public I get(int[] pos)
        {
            return ((IntArray<I>) array).createElement(((IntArray<I>) array).getInt(pos) + 1);
        }

        @Override
        public void set(int[] pos, I value)
        {
            throw new RuntimeException("Can not modify an Increment view");
        }

        @SuppressWarnings("unchecked")
        @Override
        public int getInt(int[] pos)
        {
            return ((IntArray<I>) array).getInt(pos) + 1;
        }

        @Override
        public void setInt(int[] pos, int value)
        {
            throw new RuntimeException("Can not modify an Increment view");
        }

        @SuppressWarnings("unchecked")
        @Override
        public IntArray<I> newInstance(int... dims)
        {
            return ((IntArray<I>) array).newInstance(dims);
        }

        @SuppressWarnings("unchecked")
        @Override
        public net.sci.array.scalar.IntArray.Factory<I> factory()
        {
            return ((IntArray<I>) array).factory();
        }
    }
    
    public static class Float32View extends ArrayWrapperStub<Float32> implements Float32Array
    {
        protected Float32View(Array<?> array)
        {
            super(array);
        }

        @Override
        public float getFloat(int[] pos)
        {
            return Math.nextUp(((Float32Array) array).getFloat(pos));
        }

        @Override
        public void setFloat(int[] pos, float value)
        {
            throw new RuntimeException("Can not modify an Increment view");
        }
    }
    
    public static class Float64View extends ArrayWrapperStub<Float64> implements Float64Array
    {
        protected Float64View(Array<?> array)
        {
            super(array);
        }

        @Override
        public double getValue(int[] pos)
        {
            return Math.nextUp(((Float64Array) array).getValue(pos));
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            throw new RuntimeException("Can not modify an Increment view");
        }
    }
}
