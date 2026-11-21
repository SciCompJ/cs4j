/**
 * 
 */
package net.sci.image.filtering;

import java.util.Arrays;
import java.util.stream.IntStream;

import net.sci.algo.AlgoStub;
import net.sci.array.PositionIterator;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.process.ScalarArrayOperator;
import net.sci.image.ImageArrayOperator;
import net.sci.util.MathUtils;

/**
 * Utility class that performs 1D convolution along a specific axis of a
 * multi-dimensional array.
 */
public class Convolution1D extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator
{
    /**
     * The array axis to process.
     */
    int axis;
    
    /**
     * The convolution kernel, stored in reverse order.
     */
    double[] kernel;
    
    /**
     * The factory used to create output array. If set to null (the default), use the factory
     * of the input array.
     */
    ScalarArray.Factory<?> factory = null;
    

    public Convolution1D(int axis, double[] kernel)
    {
        this.axis = axis;
        this.kernel = reverse(kernel);
    }
    
    private double[] reverse(double[] array)
    {
        int n = array.length;
        return IntStream.rangeClosed(1, n).mapToDouble(i -> array[n-i]).toArray();
    }

    /**
     * Sets up the factory used to create output arrays.
     * 
     * @param factory the factory to set
     */
    public void setFactory(ScalarArray.Factory<?> factory)
    {
        this.factory = factory;
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        // allocate result array
        ScalarArray.Factory<?> factory = this.factory;
        if (factory == null)
        {
            factory = array.factory();
        }
        ScalarArray<?> res = factory.create(array.size());
        
        
        int[] dims = array.size();
        dims[axis] = 1;
        
        int size = array.size(axis);
        int pad = (this.kernel.length - 1) / 2;
        
        double[] resRow = new double[size];
        
        // prepare iteration monitoring
        int i = 0;
        int nPos = (int) MathUtils.prod(dims);
        int step = Math.max(nPos / 500, 1);
        
        // iterate over fibers parallel to operator axis
        PositionIterator iter = PositionIterator.of(dims);
        while(iter.hasNext())
        {
            if (i++ % step == 0)
            {
                this.fireProgressChanged(this, i, nPos);
            }
            int[] refPos = iter.next();
            
            double[] fiber = getPaddedRow(array, axis, refPos, pad);
            filterRow(fiber, this.kernel, resRow);
            setRow(res, axis, refPos, resRow);
        }
        
        return res;
    }
    
    private static double[] getPaddedRow(ScalarArray<?> array, int dim, int[] pos0, int pad)
    {
        int n = array.size(dim);
        int n2 = n + 2 * pad;
        
        double[] row = new double[n2];
        int[] pos = Arrays.copyOf(pos0, pos0.length);
        pos[dim] = 0;
        Arrays.fill(row, 0, pad, array.getValue(pos));
        for (int i = 0; i < n; i++)
        {
            pos[dim] = i;
            row[i + pad] = array.getValue(pos);
        }
        pos[dim] = n - 1;
        Arrays.fill(row, n+pad, n2, array.getValue(pos));
        
        return row;
    }
    
    /**
     * Computes cross-correlation of the input array with the specified kernel,
     * and stores the result in the specified array.
     * 
     * @param input
     *            the input array, with size n + 2 * r
     * @param kernel
     *            the array containing kernel values, with size 2*r+1
     * @param output
     *            the array to store the result, with size n
     */
    private static final void filterRow(double[] input, double[] kernel, double[] output)
    {
        int n = output.length;
        for (int i = 0; i < n; i++)
        {
            double sum = 0;
            for (int j = 0; j < kernel.length; j++)
            {
                sum += input[i + j] * kernel[j];
            }
            output[i] = sum;
        }
    }
    
    private static final void setRow(ScalarArray<?> array, int dim, int[] refPos, double [] row)
    {
        int[] pos = Arrays.copyOf(refPos, refPos.length);
        for (int i = 0; i < array.size(dim); i++)
        {
            pos[dim] = i;
            array.setValue(pos, row[i]);
        }
    }
}
