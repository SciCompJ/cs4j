/**
 * 
 */
package net.sci.array.shape;

import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.Array3D;
import net.sci.array.ArrayOperator;

/**
 * Flips the content of an array along the specified dimension.
 * 
 * Example 1: flip an array horizontally
 * {@snippet lang="java":
 * Array<?> array = ...
 * Array<?> res = new Flip(0).process(array);
 * }
 * 
 * Example 2: creates a view on the flipped array
 * {@snippet lang="java":
 * Array<?> array = ...
 * Array<?> res = new Flip(0).createView(array);
 * }
 */
public class Flip extends AlgoStub implements ArrayOperator
{
    int dim;

    /**
     * Creates a new instance of Flip operator, that specifies the dimension of
     * flip.
     * 
     * @param dim
     *            the dimension to flip, between 0 and the array dimensionality
     *            minus one
     */
    public Flip(int dim)
    {
        this.dim = dim;
    }

    @Override
    public <T> Array<T> process(Array<T> array)
    {
        Array<T> output = array.newInstance(array.size());
        process(array, output);
        return output;
    }

    /**
     * Flips the content of the input 2D array, and stores the result in the
     * output array.
     * 
     * @param input
     *            the input array
     * @param output
     *            the output array
     * @param <T1>
     *            the type of the input array
     * @param <T2>
     *            the type of the output array
     */
    public <T1 extends T2, T2> void process2d(Array2D<T1> input, Array2D<T2> output)
    {
        // get image size
        int sizeX = input.size(0);
        int sizeY = input.size(1);

        // iterate over pixels
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                switch (dim)
                {
                    case 0 -> output.set(x, y, input.get(sizeX - 1 - x, y));
                    case 1 -> output.set(x, y, input.get(x, sizeY - 1 - y));
                }
            }
        }
    }

    /**
     * Flips the content of the input 3D array, and stores the result in the
     * output array.
     * 
     * @param input
     *            the input array
     * @param output
     *            the output array
     * @param <T1>
     *            the type of the input array
     * @param <T2>
     *            the type of the output array
     */
    public <T1 extends T2, T2> void process3d(Array3D<T1> input, Array3D<T2> output)
    {
        // get image size
        int sizeX = input.size(0);
        int sizeY = input.size(1);
        int sizeZ = input.size(2);

        // iterate over pixels
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    switch (dim)
                    {
                        case 0-> output.set(x, y, z, input.get(sizeX - 1 - x, y, z));
                        case 1-> output.set(x, y, z, input.get(x, sizeY - 1 - y, z));
                        case 2-> output.set(x, y, z, input.get(x, y, sizeZ - 1 - z));
                    }
                }
            }
        }
    }

    /**
     * Flips the content of the input ND array, and stores the resut in the
     * output array. This methods works for arrays of any dimensionality, but
     * may be slower than methods for specific dimensionality.
     * 
     * @param input
     *            the input array
     * @param output
     *            the output array
     * @param <T1>
     *            the type of the input array
     * @param <T2>
     *            the type of the output array
     */
    public <T1 extends T2, T2> void processNd(Array<T1> input, Array<T2> output)
    {
        int nd = input.dimensionality();
        int sizeDim = input.size(this.dim);
        int[] pos2 = new int[nd];

        // iterate over positions of input array
        for (int[] pos : input.positions())
        {
            System.arraycopy(pos, 0, pos2, 0, nd);
            pos2[dim] = sizeDim - 1 - pos[dim];
            output.set(pos2, input.get(pos));
        }
    }

    /**
     * Processing method of the Flip operator, that will delegate the processing
     * to a more specialized methods depending on dimensionality of input array.
     * Both arrays must have same dimensionality.
     * 
     * @param <T1>
     *            the type of the input array
     * @param <T2>
     *            the type of the output array
     * @param input
     *            the input array
     * @param output
     *            the array that will contain the result of the Flip operation
     */
    public <T1 extends T2, T2> void process(Array<T1> input, Array<T2> output)
    {
        switch(input.dimensionality())
        {
            case 2 -> process2d(Array2D.wrap(input), Array2D.wrap(output));
            case 3 -> process3d(Array3D.wrap(input), Array3D.wrap(output));
            default -> processNd(input, output);
        };
    }

    /**
     * Creates a virtual array that will perform flip operation on the fly when
     * elements will be requested.
     * 
     * @param <T>
     *            the type of the input array. Output array have same type.
     * @param array
     *            the array to flip
     * @return a flipped view on the input array.
     */
    public <T> Array<T> createView(Array<T> array)
    {
        int[] dims = array.size();
        Function<int[], int[]> mapping = (int[] pos) -> {
            int[] pos2 = new int[pos.length];
            System.arraycopy(pos, 0, pos2, 0, pos.length);
            pos2[dim] = array.size(dim) - 1 - pos[dim];
            return pos2;
        };

        return array.reshapeView(dims, mapping);
    }
}
