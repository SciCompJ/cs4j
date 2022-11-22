/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.image.morphology.filter.BinaryBlackTopHat;
import net.sci.image.morphology.filter.BinaryClosing;
import net.sci.image.morphology.filter.BinaryDilation;
import net.sci.image.morphology.filter.BinaryErosion;
import net.sci.image.morphology.filter.BinaryGradient;
import net.sci.image.morphology.filter.BinaryInnerGradient;
import net.sci.image.morphology.filter.BinaryOpening;
import net.sci.image.morphology.filter.BinaryOuterGradient;
import net.sci.image.morphology.filter.BinaryWhiteTopHat;
import net.sci.image.morphology.reconstruct.BinaryFillHoles3D;
import net.sci.image.morphology.reconstruct.BinaryKillBorders;
import net.sci.image.morphology.reconstruct.RunLengthBinaryReconstruction2D;
import net.sci.image.morphology.reconstruct.RunLengthBinaryReconstruction3D;


/**
 * <p>
 * Collection of static methods for morphological filters applied on binary
 * images.
 * </p>
 * 
 * @author dlegland
 */
public class BinaryMorphology
{
    // =======================================================================
    // Static methods to perform common morphological operations
    
    /**
     * Performs morphological dilation on the input binary array.
     * 
     * @see net.sci.image.morphology.filter.BinaryDilation
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for dilation
     * @return the result of the dilation
     */
    public static final BinaryArray dilation(BinaryArray array, Strel strel)
    {
        return new BinaryDilation(strel).process(array);
    }

    /**
     * Performs morphological erosion on the input binary array. 
     * 
     * 
     * @see net.sci.image.morphology.filter.BinaryErosion
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for erosion
     * @return the result of the erosion
     */
    public static final BinaryArray erosion(BinaryArray array, Strel strel)
    {
        return new BinaryErosion(strel).process(array);
    }

    /**
     * Performs morphological opening on the input binary array.
     * 
     * @see net.sci.image.morphology.filter.BinaryOpening
     * 
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for opening
     * @return the result of the morphological opening
     */
    public static final BinaryArray opening(BinaryArray array, Strel strel)
    {
        return new BinaryOpening(strel).process(array);
    }


    /**
     * Performs closing on the input binary array.
     * 
     * @see net.sci.image.morphology.filter.BinaryClosing
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for closing
     * @return the result of the morphological closing
     */
    public static final BinaryArray closing(BinaryArray array, Strel strel) 
    {
        return new BinaryClosing(strel).process(array);
    }
    
    /**
     * Computes morphological gradient on the input binary array. The gradient
     * is obtained by computing the set-difference between the results of the
     * dilation and of the erosion.
     * 
     * @see net.sci.image.morphology.filter.BinaryGradient
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for gradient
     * @return the result of the gradient.
     */
    public static final BinaryArray gradient(BinaryArray array, Strel strel)
    {
        return new BinaryGradient(strel).process(array);
    }
    
    /**
     * Computes morphological inner gradient on the input binary array. The
     * inner gradient is obtained by computing the set-difference between the
     * results of the array and of the erosion.
     * 
     * @see net.sci.image.morphology.filter.BinaryInnerGradient
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for gradient
     * @return the result of the inner gradient.
     */
    public static final BinaryArray innerGradient(BinaryArray array, Strel strel)
    {
        return new BinaryInnerGradient(strel).process(array);
    }
    
    /**
     * Computes morphological outer gradient on the input binary array. The
     * outer gradient is obtained by computing the set-difference between the
     * result of the dilation and the original array.
     * 
     * @see net.sci.image.morphology.filter.BinaryOuterGradient
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for gradient
     * @return the result of the outer gradient.
     */
    public static final BinaryArray outerGradient(BinaryArray array, Strel strel)
    {
        return new BinaryOuterGradient(strel).process(array);
    }
    
    /**
     * Computes White Top-Hat of the binary array, by performing morphological
     * opening, and retaining elements from original array that do not belong to
     * opening. The effect is to retain foreground elements smaller than the
     * structuring element.
     * 
     * @see net.sci.image.morphology.filter.BinaryWhiteTopHat
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for top hat
     * @return the result of the white top hat.
     */
    public static final BinaryArray whiteTophHat(BinaryArray array, Strel strel)
    {
        return new BinaryWhiteTopHat(strel).process(array);
    }
    
    /**
     * Computes Black Top-Hat of the binary array, by performing morphological
     * closing, and retaining elements that do not belong to original array. The
     * effect is to retain background elements smaller than the structuring
     * element.
     * 
     * @see net.sci.image.morphology.filter.BinaryBlackTopHat
     * 
     * @param array
     *            the binary input array to process
     * @param strel
     *            the structuring element used for top hat
     * @return the result of the black top hat.
     */
    public static final BinaryArray blakTophHat(BinaryArray array, Strel strel)
    {
        return new BinaryBlackTopHat(strel).process(array);
    }
    
    /**
     * Removes the border of the input binary array. The principle is to perform a
     * morphological reconstruction by dilation initialized with image boundary.
     * 
     * @see #fillHoles(BinaryArray)
     * 
     * @param array
     *            the image to process
     * @return a new image with borders removed
     */
    public static final BinaryArray killBorders(BinaryArray array)
    {
        return new BinaryKillBorders().process(array);
    }

    /**
     * Fills the holes within the input binary array.
     * 
     * @see #killBorders(BinaryArray)
     * 
     * @param array
     *            the binary array to process
     * @return the array with holes filled
     */
    public static final BinaryArray fillHoles(BinaryArray array)
    {
        return new BinaryFillHoles3D().process(array);
    }
    
    /**
     * Static method to computes the morphological reconstruction by dilation of
     * the binary marker image under the binary mask image.
     *
     * @param marker
     *            input marker array
     * @param mask
     *            input mask array
     * @return the result of morphological reconstruction
     */
    public final static BinaryArray reconstruction(BinaryArray marker, BinaryArray mask) 
    {
        if (marker.dimensionality() != mask.dimensionality())
        {
            throw new RuntimeException("Input arrays must have same dimensionality");
        }
        if (!Arrays.isSameSize(marker, mask))
        {
            throw new RuntimeException("Input arrays must have same size");
        }
            
        if (marker.dimensionality() == 2)
        {
            BinaryArray2D marker2d = BinaryArray2D.wrap(marker);
            BinaryArray2D mask2d = BinaryArray2D.wrap(mask);
            return new RunLengthBinaryReconstruction2D().processBinary2d(marker2d, mask2d);
        }
        else if (marker.dimensionality() == 3)
        {
            BinaryArray3D marker3d = BinaryArray3D.wrap(marker);
            BinaryArray3D mask3d = BinaryArray3D.wrap(mask);
            return new RunLengthBinaryReconstruction3D().processBinary3d(marker3d, mask3d);
        }
        else
        {
            throw new RuntimeException("Requires array with dimension 2 or 3.");
        }
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private BinaryMorphology()
    {
    }
}
