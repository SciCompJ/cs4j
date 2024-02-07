/**
 * 
 */
package net.sci.util;

/**
 * Utility methods for mathematics.
 */
public class MathUtils
{
    /**
     * Computes the product of several integer values. The typical usage is to
     * count elements in arrays based on the array of sizes along each
     * dimensions.
     * 
     * @param dims
     *            the values to multiply
     * @return the product of the input values
     */
    public static final long prod(int... dims)
    {
        long n = 1;
        for (int dim : dims) 
            n *= dim;
        return n;
    }
    
    /**
     * Computes the cumulative products of the values within specified array,
     * and returns the result as an array of long values the same size as the
     * input array. The first values equals the first value of input array. The
     * last value is to product of all values within input array.
     * 
     * @param dims
     *            the array of values to multiply
     * @return the array of cumulative products.
     */
    public static final long[] cumProdLong(int[] dims)
    {
        long[] res = new long[dims.length];
        res[0] = dims[0];
        for (int d = 1; d < dims.length; d++)
        {
            res[d] = res[d - 1] * dims[d];
        }
        return res;
    }
    
    /**
     * Distributes scalar values evenly between two bounds. If the second bound
     * is smaller than the first bound, then the result array contains
     * descending values.
     * 
     * @param first
     *            the first bound of the interval
     * @param last
     *            the second bound of the interval
     * @param count
     *            the number of values
     * @return an array of double values with <code>count</code> elements
     *         starting with value <code>first</code> and finishing with value
     *         <code>last</code>.
     */
    public static final double[] linspace(double first, double last, int count)
    {
        if (count < 1) throw new IllegalArgumentException("number of steps must be geater than 1");
        
        double[] res = new double[count];
        double step = (last - first) / (count - 1);
        for (int i = 0; i < count; i++)
        {
            res[i] = first + i * step;
        }
        return res;
    }
    
    /** 
     * Private constructor to prevent instantiation.
     */
    private MathUtils()
    {
    }
}
