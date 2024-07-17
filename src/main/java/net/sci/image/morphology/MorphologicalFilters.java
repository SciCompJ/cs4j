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
    // =======================================================================
    // Inner enumeration
    
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
        EROSION("Erosion", "Ero"),
        /** Morphological dilation (local maxima) */
        DILATION("Dilation", "Dil"),
        /** Morphological opening (erosion followed by dilation) */
        OPENING("Opening", "Op"),
        /** Morphological closing (dilation followed by erosion) */
        CLOSING("Closing", "Cl"),
        /** White Top-Hat */
        TOPHAT("White Top Hat", "WTH"),
        /** Black Top-Hat */
        BOTTOMHAT("Black Top Hat", "BTH"),
        /** Morphological gradient (difference of dilation with erosion) */
        GRADIENT("Gradient", "MGrad"),
        /**
         * Morphological laplacian (difference of the outer gradient with the
         * inner gradient, equal to DIL+ERO-2*Img).
         */
        LAPLACIAN("Laplacian", "MLap"),
        /** Morphological internal gradient (difference of original image with erosion) */
        INNER_GRADIENT("Inner Gradient", "InnGrad"), 
        /** Morphological internal gradient (difference of dilation with original image) */
        OUTER_GRADIENT("Outer Gradient", "OutGrad");

        
        /**
         * A label that can be used for display in graphical widgets.
         */
        private final String label;
        
        /**
         * A suffix intended to be used for creating result image names. 
         */
        private String suffix;
        
        
        private Operation(String label, String suffix) 
        {
            this.label = label;
            this.suffix = suffix;
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
            return createOperator(strel).process(array);
        }
        
        public MorphologicalFilter createOperator(Strel strel)
        {
            if (this == DILATION) return new Dilation(strel);
            if (this == EROSION) return new Erosion(strel);
            if (this == CLOSING) return new Closing(strel);
            if (this == OPENING) return new Opening(strel);
            if (this == TOPHAT) return new WhiteTopHat(strel);
            if (this == BOTTOMHAT) return new BlackTopHat(strel);
            if (this == GRADIENT) return new Gradient(strel);
            if (this == LAPLACIAN) return new Laplacian(strel, 0.0);
            if (this == INNER_GRADIENT) return new InnerGradient(strel);
            if (this == OUTER_GRADIENT) return new OuterGradient(strel);
            throw new RuntimeException(
                    "Unable to process the " + this + " morphological operation");
        }
        
        public String suffix()
        {
            return suffix;
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
