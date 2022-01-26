/**
 * 
 */
package net.sci.image.morphology;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.image.morphology.filter.BinaryClosing;
import net.sci.image.morphology.filter.BinaryDilation;
import net.sci.image.morphology.filter.BinaryErosion;
import net.sci.image.morphology.filter.BinaryOpening;

/**
 * Base class for morphological filter applied on a binary array, based on a
 * structuring element.
 * 
 * @see net.sci.image.morphology.Strel
 * @see BinaryDilation
 * @see BinaryErosion
 * @see BinaryOpening
 * @see BinaryClosing
 * @see MorphologicalFilter
 * 
 * @author dlegland
 */
public abstract class BinaryMorphologicalFilter extends AlgoStub implements ArrayOperator
{
    /**
     * The structuring element used by concrete implementations.
     */
    protected Strel strel;

    /**
     * Initializes the inner structuring element.
     * 
     * @param strel
     *            the structuring element to use for morphological operation.
     */
    public BinaryMorphologicalFilter(Strel strel)
    {
        this.strel = strel;
    }

    /**
     * Apply this morphological operation on the (binary) input array.
     * 
     * @param array
     *            the binary array to process
     * @return the result of the morphological filter.
     */
    public abstract BinaryArray processBinary(BinaryArray array);

    /**
     * @return the structuring element used by this filter.
     */
    public Strel getStrel()
    {
        return this.strel;
    }
    
    /**
     * Default implementation that case the input array to a BinaryArray, and
     * calls the processBinary method.
     * 
     * @param array
     *            the array to process
     * @throws RuntimeException
     *             if the input array is not an instance of BinaryArray
     */
    @Override
    public <T> BinaryArray process(Array<T> array)
    {
        if (array instanceof BinaryArray)
        {
            return processBinary((BinaryArray) array);
        }
        else
        {
            throw new RuntimeException(
                    "Requires an instance of BinaryArray");
        }
    }

}
