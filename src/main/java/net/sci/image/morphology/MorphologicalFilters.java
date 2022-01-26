/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.Array;
import net.sci.image.morphology.filter.BlackTopHat;
import net.sci.image.morphology.filter.Closing;
import net.sci.image.morphology.filter.Dilation;
import net.sci.image.morphology.filter.Erosion;
import net.sci.image.morphology.filter.Gradient;
import net.sci.image.morphology.filter.Laplacian;
import net.sci.image.morphology.filter.MorphologicalFilterAlgo;
import net.sci.image.morphology.filter.Opening;
import net.sci.image.morphology.filter.WhiteTopHat;

/**
 * <p>
 * Collection of static methods for morphological filters,
 * as well as an enumeration of available methods.
 * </p>
 * 
 * <p>
 * Example of use:
 * <pre>
 * {@code
 * Array<?> array = ...
 * Strel se = SquareStrel.fromDiameter(5);
 * Array<?> grad = MorphologicalFilters.gradient(array, se);
 * Image res = new Image(grad, "Gradient");
 * res.show(); 
 *  }</pre>
 *
 * 
 * @see Strel
 * @author dlegland
 */
public class MorphologicalFilters
{
    /**
     * A pre-defined set of basis morphological operations, that can be easily 
     * used with a GenericDialog.</p>
     *  
     * Example:
     * <pre>
     * {@code
     * // Use a generic dialog to define an operator 
     * GenericDialog gd = new GenericDialog();
     * gd.addChoice("Operation", Operation.getAllLabels();
     * gd.showDialog();
     * Operation op = Operation.fromLabel(gd.getNextChoice());
     * // Apply the operation on the current array
     * Array<?> array = image.getData();
     * op.apply(array, SquareStrel.fromRadius(2));
     * }</pre>
     */
    public enum Operation 
    {
        /** Morphological erosion (local minima) */
        EROSION("Erosion"),
        /** Morphological dilation (local maxima) */
        DILATION("Dilation"),
        /** Morphological opening (erosion followed by dilation) */
        OPENING("Opening"),
        /** Morphological closing (dilation followed by erosion) */
        CLOSING("Closing"),
        /** White Top-Hat */
        TOPHAT("White Top Hat"),
        /** Black Top-Hat */
        BOTTOMHAT("Black Top Hat"),
        /** Morphological gradient (difference of dilation with erosion) */
        GRADIENT("Gradient"),
        /**
         * Morphological laplacian (difference of external gradient with
         * internal gradient)
         */
        LAPLACIAN("Laplacian");
        
        private final String label;
        
        private Operation(String label) 
        {
            this.label = label;
        }
        
        /**
         * Applies the current operator to the input array.
         * 
         * @param array
         *            the array to process
         * @param strel
         *            the structuring element to use
         * @return the result of morphological operation applied to array
         */
        public Array<?> process(Array<?> array, Strel strel) 
        {
            if (this == DILATION)
                return new Dilation(strel).process(array);
            if (this == EROSION)
                return new Erosion(strel).process(array);
            if (this == CLOSING)
                return new Closing(strel).process(array);
            if (this == OPENING)
                return new Opening(strel).process(array);
            if (this == TOPHAT)
                return new WhiteTopHat(strel).process(array);
            if (this == BOTTOMHAT)
                return new BlackTopHat(strel).process(array);
            if (this == GRADIENT)
                return new Gradient(strel).process(array);
            if (this == LAPLACIAN)
                return new Laplacian(strel, 128.0).process(array); // TODO: adapt middle value to array type
//            if (this == INTERNAL_GRADIENT)
//                return internalGradient(array, strel);
//            if (this == EXTERNAL_GRADIENT)
//                return externalGradient(array, strel);
            
            throw new RuntimeException(
                    "Unable to process the " + this + " morphological operation");
        }
        
        public MorphologicalFilterAlgo createOperator(Strel strel)
        {
            if (this == DILATION) return new Dilation(strel);
            if (this == EROSION) return new Erosion(strel);
            if (this == CLOSING) return new Closing(strel);
            if (this == OPENING) return new Opening(strel);
            if (this == TOPHAT) return new WhiteTopHat(strel);
            if (this == BOTTOMHAT) return new BlackTopHat(strel);
            if (this == GRADIENT) return new Gradient(strel);
            if (this == LAPLACIAN) return new Laplacian(strel, 0.0); // TODO: adapt middle value to array type
//            if (this == INTERNAL_GRADIENT) return new internalGradient(strel);
//            if (this == EXTERNAL_GRADIENT) return new externalGradient(strel);
            throw new RuntimeException(
                    "Unable to process the " + this + " morphological operation");
        }
        
        public String toString() 
        {
            return this.label;
        }
        
        public static String[] getAllLabels()
        {
            int n = Operation.values().length;
            String[] result = new String[n];
            
            int i = 0;
            for (Operation op : Operation.values())
                result[i++] = op.label;
            
            return result;
        }
        
        /**
         * Determines the operation type from its label.
         * 
         * @param opLabel
         *            the label of the operation
         * @return the parsed Operation
         * @throws IllegalArgumentException
         *             if label is not recognized.
         */
        public static Operation fromLabel(String opLabel)
        {
            if (opLabel != null)
                opLabel = opLabel.toLowerCase();
            for (Operation op : Operation.values()) 
            {
                String cmp = op.label.toLowerCase();
                if (cmp.equals(opLabel))
                    return op;
            }
            throw new IllegalArgumentException("Unable to parse Operation with label: " + opLabel);
        }
    }

    // =======================================================================
    // Main morphological operations
    
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
        return new Laplacian(strel, 128.0).process(array);
    }
    
 

//    /** 
//     * Computes the morphological internal gradient of the input array.
//     * The morphological internal gradient is obtained by from the difference 
//     * of original array with the result of an erosion.
//     * 
//     * @see #erosion(Array, Strel)
//     * @see #gradient(Array, Strel)
//     * @see #externalGradient(Array, Strel)
//     * 
//     * @param array
//     *            the input array to process (grayscale or RGB)
//     * @param strel
//     *            the structuring element used for morphological internal gradient
//     * @return the result of the morphological internal gradient
//     */
//    public static Array<?> internalGradient(Array<?> array, Strel strel)
//    {
//        if (array instanceof ScalarArray2D)
//        {
//            return internalGradient_scalar2d((ScalarArray2D<?>) array, strel);
//        }
//        else if (array instanceof VectorArray2D<?>)
//        {
//            return internalGradient_vector2d((VectorArray2D<?>) array, strel);
//        }
//        else
//        {
//            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
//        }
//    }
//    
//    private static ScalarArray2D<?> internalGradient_scalar2d(ScalarArray2D<?> array, Strel strel) 
//    {
//        // First performs closing
//        ScalarArray2D<?> result = strel.erosion(array);
//        
//        // Compute subtraction of result from original array
//        for (int y = 0; y < array.size(1); y++)
//        {
//            for (int x = 0; x < array.size(0); x++)
//            {
//                double val = array.getValue(x, y) - result.getValue(x, y);
//                result.setValue(x, y, val);
//            }
//        }
//        
//        return result;
//    }
//
//    /**
//     * Computes internal morphological gradient on each channel of a vector
//     * array, and reconstitutes the resulting vector array.
//     * 
//     * @param array
//     *            the input vector array
//     * @param strel
//     *            the structuring element used for gradient
//     * @return the result of the morphological gradient
//     */
//    private static VectorArray2D<?> internalGradient_vector2d(VectorArray2D<?> array, Strel strel)
//    {
//        // allocate memory for result
//        VectorArray2D<?> res = array.duplicate();
//        
//        // iterate over channels
//        for (int c = 0; c < array.channelNumber(); c++)
//        {
//            // process current channel and copy into result array
//            copyChannel(internalGradient_scalar2d(array.channel(c), strel), res, c);
//        }
//        return res;
//    }
//
//
//    /** 
//     * Computes the morphological external gradient of the input array.
//     * The morphological external gradient is obtained by from the difference 
//     * of the result of a dilation and of the original array .
//     * 
//     * @see #dilation(Array, Strel)
//     * 
//     * @param array
//     *            the input array to process (grayscale or RGB)
//     * @param strel
//     *            the structuring element used for morphological external gradient
//     * @return the result of the morphological external gradient
//     */
//    public static Array<?> externalGradient(Array<?> array, Strel strel) 
//    {
//        if (array instanceof ScalarArray2D)
//        {
//            return externalGradient_scalar2d((ScalarArray2D<?>) array, strel);
//        }
//        else if (array instanceof VectorArray2D<?>)
//        {
//            return externalGradient_vector2d((VectorArray2D<?>) array, strel);
//        }
//        else
//        {
//            throw new RuntimeException("Can not process array of class: " + array.getClass().getName());
//        }
//    }
//    
//    private static ScalarArray2D<?> externalGradient_scalar2d(ScalarArray2D<?> array, Strel strel) 
//    {
//        // First performs closing
//        ScalarArray2D<?> result = strel.dilation(array);
//        
//        // Compute subtraction of result from original array
//        for (int y = 0; y < array.size(1); y++)
//        {
//            for (int x = 0; x < array.size(0); x++)
//            {
//                double val = result.getValue(x, y) - array.getValue(x, y);
//                result.setValue(x, y, val);
//            }
//        }
//        
//        return result;
//    }
//
//    /**
//     * Computes external morphological gradient on each channel of a vector
//     * array, and reconstitutes the resulting vector array.
//     * 
//     * @param array
//     *            the input vector array
//     * @param strel
//     *            the structuring element used for gradient
//     * @return the result of the morphological gradient
//     */
//    private static VectorArray2D<?> externalGradient_vector2d(VectorArray2D<?> array, Strel strel)
//    {
//        // allocate memory for result
//        VectorArray2D<?> res = array.duplicate();
//        
//        // iterate over channels
//        for (int c = 0; c < array.channelNumber(); c++)
//        {
//            // process current channel and copy into result array
//            copyChannel(externalGradient_scalar2d(array.channel(c), strel), res, c);
//        }
//        return res;
//    }

    /**
     * Makes the default constructor private to avoid creation of instances.
     */
    private MorphologicalFilters() 
    {
    }
}
