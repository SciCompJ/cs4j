/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.process.shape.*;

/**
 * Utility class that group several static methods operating on the shape of
 * arrays.
 * 
 * Implementation of the operators are in the @link{net.sci.array.process.shape} package.
 * 
 * @see{net.sci.array.process.shape}
 * 
 * @author dlegland
 */
public class Shape
{
    /**
     * Decimates the number of elements of the input array, by retaining only
     * one element over k in each dimension.
     * 
     * @param <T>
     *            The type of the input array
     * @param array
     *            the array to down-sample
     * @param ratio
     *            the decimation ratio
     * @return the result of down sampling applied to input array.
     * 
     * @see net.sci.array.process.shape.DownSampler
     */
    public static final <T> Array<T> downSample(Array<T> array, int ratio)
    {
        return new DownSampler(ratio).process(array);
    }
    
    /**
     * Flips the content of an array along the specified dimension.
     * 
     * <pre>{@code
     *    // create an empty array with only one non-unit dimension
     *    UInt8Array2D array = new BufferedUInt8Array2D(6, 4);
     *    array.populateValues((x,y) -> (double) x + y * 10.0);
     *        
     *    // apply flip operation
     *    Array<?> resFlip= Shape.flip(array, 0);
     *    
     *    // apply squeeze operation
     *    UInt8Array res = (UInt8Array) Shape.squeeze(array);
     *    
     *    // the following should equal 1
     *    int newDim = res.dimensionality();
     * }</pre>
     * 
     * @param <T>
     *            The type of the input array
     * @param array
     *            the array to flip
     * @param dim
     *            the flip dimension, between 0 and the nd-1 (where nd is the
     *            number of dimension of the array).
     * @return the result of flip operation applied to the input array
     * 
     * @see net.sci.array.process.shape.Flip
     */
    public static final <T> Array<T> flip(Array<T> array, int dim)
    {
        return new Flip(dim).process(array);
    }
    
    /**
     * Removess array dimensions whose size is 1.
     * 
     * <pre>{@code
     *    // create an empty array with only one non-unit dimension
     *    UInt8Array array = UInt8Array.create(new int[]{1, 10, 1});
     *    
     *    // apply squeeze operation
     *    UInt8Array res = (UInt8Array) Shape.squeeze(array);
     *    
     *    // the following should equal 1
     *    int newDim = res.dimensionality();
     * }</pre>
     * 
     * @param <T>
     *            The type of the input array
     * @param array
     *            the array to squeeze
     * @return a new array without any dimension with size equal to 1.
     * 
     * @see net.sci.array.process.shape.Squeeze
     */
    public static final <T> Array<T> squeeze(Array<T> array)
    {
        return new Squeeze().process(array);
    }
    
    /**
     * Reshapes the dimensions of an array.
     * 
     * The product of dimensions of both arrays should be the same.
     * 
     * @param <T>
     *            The type of the input array
     * @param array
     *            the array to reshape
     * @param dims
     *            the new dimensions of the array
     * @return an array containing the same elements as <code>array</code>, with
     *         size <code>dims</code>.
     * 
     * @see net.sci.array.process.shape.Reshape
     */
    public static final <T> Array<T> reshape(Array<T> array, int[] dims)
    {
        return new Reshape(dims).process(array);
    }
    
    /**
     * Permutes the dimensions of an array.
     * 
     * <p>Example:
     * <pre>{@code
        // create input 5x4x3 array
        int[] dims = new int[] {5, 4, 3};
        Array<?> array = UInt8Array.create(dims);
        // create permute dimensions operator 
        int[] order = new int[] {2, 0, 1};
        // apply operator to array
        Array<?> result = Shape.permuteDimensions(array, order);
        // resulting dimensions should be: int[] {3, 5, 4};
        int[] newDims = result.size(); 
     * }</pre>
     * 
     * @param <T>
     *            The type of the input array
     * @param array
     *            the array whose dimensions will be permuted
     * @param order
     *            the dimension order of the new array. Should be a permutation
     *            of integers between 0 and nd-1.
     * @return an array containing the same elements as <code>array</code>, with
     *         permuted dimensions.
     * 
     * @see net.sci.array.process.shape.PermuteDimensions
     */
    public static final <T> Array<T> permuteDimensions(Array<T> array,
            int[] order)
    {
        return new PermuteDimensions(order).process(array);
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Shape() 
    {
    }
}
