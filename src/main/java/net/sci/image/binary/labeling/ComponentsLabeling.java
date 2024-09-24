package net.sci.image.binary.labeling;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;
import net.sci.image.ImageType;

/**
 * General interface for computing Connected Components Labeling of binary
 * arrays.
 */
public interface ComponentsLabeling extends ArrayOperator, ImageArrayOperator
{
    /**
     * Choose an IntArray factory based on a bit depth. This method is used to
     * facilitate conversion from ImageJ code.
     * 
     * @param bitDepth
     *            the number of bits for representing integers, that must be 8,
     *            16 or 32.
     * @return an instance of UInt8Array, UInt16Array, or UInt32Array.
     */
    public static IntArray.Factory<?> chooseIntArrayFactory(int bitDepth)
    {
        return switch (bitDepth)
        {
            case 8 -> UInt8Array.defaultFactory;
            case 16 -> UInt16Array.defaultFactory;
            case 32 -> Int32Array.defaultFactory;
            default -> throw new IllegalArgumentException("Bit Depth should be 8, 16 or 32.");
        };
    }
    

    public int processBinary(BinaryArray array, IntArray<?> labelMap);
    
    /**
     * Creates a new array that can be used as output for processing the given
     * input array.
     * 
     * @param baseArray
     *            the reference array
     * @return a new instance of IntArray that can be used for storing the
     *         result of labeling
     */
    public IntArray<?> createEmptyLabelMap(Array<?> baseArray);

    @Override
    public default Image process(Image image)
    {
        BinaryArray array = BinaryArray.wrap(image.getData());
        IntArray<?> result = createEmptyLabelMap(array);
        int nLabels = processBinary(array, result);
        System.out.println("number of labels: " + nLabels); 
        Image resultImage = new Image(result, ImageType.LABEL, image);
        resultImage.getDisplaySettings().setDisplayRange(new double[] {0, nLabels});
        return resultImage;
    }
}
