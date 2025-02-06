/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.Array;
import net.sci.image.morphology.filtering.BlackTopHat;
import net.sci.image.morphology.filtering.Closing;
import net.sci.image.morphology.filtering.Dilation;
import net.sci.image.morphology.filtering.Erosion;
import net.sci.image.morphology.filtering.Gradient;
import net.sci.image.morphology.filtering.InnerGradient;
import net.sci.image.morphology.filtering.Laplacian;
import net.sci.image.morphology.filtering.Opening;
import net.sci.image.morphology.filtering.OuterGradient;
import net.sci.image.morphology.filtering.WhiteTopHat;

/**
 * <p>
 * Collection of static methods for morphological filters,
 * as well as an enumeration of available methods.
 * </p>
 * 
 * <p>
 * Example of use:
 * <pre>
 * {@snippet lang="java" :
 * Array<?> array = ...
 * Strel se = SquareStrel.fromDiameter(5);
 * Array<?> grad = MorphologicalFilters.gradient(array, se);
 * Image res = new Image(grad, "Gradient");
 * res.show(); 
 * }
 * 
 * @see Strel
 * @author dlegland
 */
public class MorphologicalFilters
{
    // =======================================================================
    // Static methods to perform common morphological operations
    
    /**
     * Performs morphological dilation on the input array.
     * 
     * Dilation is obtained by extracting the maximum value among pixels in the
     * neighborhood given by the structuring element.
     * 
     * This methods is mainly a wrapper to the dilation method of the strel
     * object.
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for dilation
     * @return the result of the dilation
     * 
     * @see #erosion(Array, Strel)
     * @see Strel#dilation(Array)
     */
    public static Array<?> dilation(Array<?> array, Strel strel)
    {
        return new Dilation(strel).process(array);
    }

    /**
     * Performs morphological erosion on the input array. Erosion is obtained by
     * extracting the minimum value among pixels in the neighborhood given by
     * the structuring element.
     * 
     * This methods is mainly a wrapper to the erosion method of the strel
     * object.
     * 
     * @see #dilation(Array, Strel)
     * @see Strel#erosion(Array)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for erosion
     * @return the result of the erosion
     */
    public static Array<?> erosion(Array<?> array, Strel strel)
    {
        return new Erosion(strel).process(array);
    }

    /**
     * Performs morphological opening on the input array.
     * 
     * The opening is obtained by performing an erosion followed by an dilation
     * with the reversed structuring element.
     * 
     * This methods is mainly a wrapper to the opening method of the strel object.
     * 
     * @see #closing(Array, Strel)
     * @see Strel#opening(Array)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for opening
     * @return the result of the morphological opening
     */
    public static Array<?> opening(Array<?> array, Strel strel)
    {
        return new Opening(strel).process(array);
    }


    /**
     * Performs closing on the input array.
     * The closing is obtained by performing a dilation followed by an erosion
     * with the reversed structuring element.
     *  
     * This methods is mainly a wrapper to the opening method of the strel object.
     * @see #opening(Array, Strel)
     * @see Strel#closing(Array)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for closing
     * @return the result of the morphological closing
     */
    public static Array<?> closing(Array<?> array, Strel strel) 
    {
        return new Closing(strel).process(array);
    }

    /**
     * Computes white top hat of the original array.
     * The white top hat is obtained by subtracting the result of an opening 
     * from the original array.
     *  
     * The white top hat enhances light structures smaller than the structuring element.
     * 
     * @see #blackTopHat(Array, Strel)
     * @see #opening(Array, Strel)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for computing white top-hat
     * @return the result of the white top-hat
     */
    public static Array<?> whiteTopHat(Array<?> array, Strel strel)
    {
        return new WhiteTopHat(strel).process(array);
    }
    
    /**
     * Computes black top hat (or "bottom hat") of the original array.
     * The black top hat is obtained by subtracting the original array from
     * the result of a closing.
     *  
     * The black top hat enhances dark structures smaller than the structuring element.
     * 
     * @see #whiteTopHat(Array, Strel)
     * @see #closing(Array, Strel)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for black top-hat
     * @return the result of the black top-hat
     */
    public static Array<?> blackTopHat(Array<?> array, Strel strel)
    {
        return new BlackTopHat(strel).process(array);
    }
    
    /**
     * Computes the morphological gradient of the input array.
     * The morphological gradient is obtained by from the difference of array 
     * dilation and array erosion computed with the same structuring element. 
     * 
     * @see #erosion(Array, Strel)
     * @see #dilation(Array, Strel)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for morphological gradient
     * @return the result of the morphological gradient
     */
    public static Array<?> gradient(Array<?> array, Strel strel)
    {
        return new Gradient(strel).process(array);
    }
    
    
    /**
     * Computes the morphological Laplacian of the input array. The
     * morphological laplacian is obtained by computing the sum of a dilation
     * and an erosion using the same structuring element, and removing twice the
     * value of the original array.
     * 
     * Homogeneous regions appear as gray.
     * 
     * @see #erosion(Array, Strel)
     * @see #dilation(Array, Strel)
     * @see #gradient(Array, Strel)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for morphological laplacian
     * @return the result of the morphological laplacian
     */
    public static Array<?> laplacian(Array<?> array, Strel strel) 
    {
        return new Laplacian(strel, 0.0).process(array);
    }
    
    /** 
     * Computes the inner morphological gradient of the input array.
     * The morphological internal gradient is obtained by from the difference 
     * of original array with the result of an erosion.
     * 
     * @see #erosion(Array, Strel)
     * @see #gradient(Array, Strel)
     * @see #outerGradient(Array, Strel)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for morphological internal gradient
     * @return the result of the morphological internal gradient
     */
    public static Array<?> innerGradient(Array<?> array, Strel strel)
    {
        return new InnerGradient(strel).process(array);
    }
    
    /** 
     * Computes the outer morphological gradient of the input array.
     * The morphological external gradient is obtained by from the difference 
     * of the result of a dilation and of the original array .
     * 
     * @see #dilation(Array, Strel)
     * @see #innerGradient(Array, Strel)
     * 
     * @param array
     *            the input array to process (grayscale or RGB)
     * @param strel
     *            the structuring element used for morphological external gradient
     * @return the result of the morphological external gradient
     */
    public static Array<?> outerGradient(Array<?> array, Strel strel) 
    {
        return new OuterGradient(strel).process(array);
    }
    

    // =======================================================================
    // Constructor
    
    /**
     * Makes the default constructor private to avoid creation of instances.
     */
    private MorphologicalFilters() 
    {
    }
}
