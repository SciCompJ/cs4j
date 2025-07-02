/**
 * 
 */
package net.sci.array.color;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;

/**
 * Creates an RGB8 view that combines original values of a reference array with
 * a color overlay defined by a binary mask and a specified color.
 * 
 * This class keeps references to arrays, and computes the requested RGB colors
 * on the fly. It requires less memory than creating a new RGB8Array. When
 * necessary, a deep copy can be obtained via the duplicate() method.
 * 
 * @see RGB8Array#duplicate()
 * 
 * @author dlegland
 *
 */
public class BinaryOverlayRGB8Array implements RGB8Array
{
    // =============================================================
    // class members

    /**
     * The reference array of the overlay wrapped to a RGB8 array in order to
     * facilitate on the fly conversions.
     */
    RGB8Array wrappedArray;
    
    /**
     * The binary mask defining the overlay.
     */
    BinaryArray binaryMask;
    
    /**
     * The overlay color. Default is red.
     */
    RGB8 overlayColor = RGB8.RED;
    
    /**
     * The overlay opacity, as a double value between 0 and 1.
     */
    double opacity;
    
    
    // =============================================================
    // Constructor
    
    /**
     * Creates an RGB8 view that combines original values of the reference array
     * with a color overlay defined by the binary mask and the specified color.
     * A default opacity of 1.0 (fully opaque) is used as default for the binary
     * overlay.
     * 
     * @param baseArray
     *            the reference array to overlay. Can be an instance of
     *            UInt8Array, BinaryArray, or RGB8Array.
     * @param binaryMask
     *            the binary mask defining the overlay. Should be the same size
     *            as the reference array.
     * @param overlayColor
     *            the overlay color.
     */
    public BinaryOverlayRGB8Array(Array<?> baseArray, BinaryArray binaryMask, RGB8 overlayColor)
    {
        this(baseArray, binaryMask, overlayColor, 1.0);
    }

    /**
     * Creates an RGB8 view that combines original values of the reference array
     * with a color overlay defined by the binary mask and the specified color.
     * 
     * @param baseArray
     *            the reference array to overlay. Can be an instance of
     *            UInt8Array, BinaryArray, or RGB8Array.
     * @param binaryMask
     *            the binary mask defining the overlay. Should be the same size
     *            as the reference array.
     * @param overlayColor
     *            the overlay color.
     * @param opacity
     *            the overlay opacity, as a double value between 0 (fully
     *            transparent) and 1 (fully opaque).
     */
    public BinaryOverlayRGB8Array(Array<?> baseArray, BinaryArray binaryMask, RGB8 overlayColor, double opacity)
    {
        // check input validity
        if (!Arrays.isSameSize(baseArray, binaryMask))
        {
            throw new IllegalArgumentException("Input arrays must have same size"); 
        }
        
        // wrap the base array into a RGB8 array
        if (RGB8.class.isAssignableFrom(baseArray.elementClass())) 
        {
            this.wrappedArray = RGB8Array.wrap(baseArray);
        }
        else if (UInt8.class.isAssignableFrom(baseArray.elementClass()))
        {
            this.wrappedArray = new UInt8ArrayRGB8View(UInt8Array.wrap(baseArray));
        }
        else if (Binary.class.isAssignableFrom(baseArray.elementClass()))
        {
            this.wrappedArray = new BinaryArrayRGB8View(BinaryArray.wrap(baseArray));
        }
        else
        {
            throw new IllegalArgumentException("Reference array must be an instance of UInt8Array, RGB8Array, or BinaryArray");
        }
        
        // keep also other variables
        this.binaryMask = binaryMask;
        this.overlayColor = overlayColor;
        this.opacity = opacity;
    }
    
    
    // =============================================================
    // Implementation of RGB8Array interface

    @Override
    public int getIntCode(int[] pos)
    {
        if (binaryMask.getBoolean(pos))
        {
            if (opacity == 1.0)
            {
                return this.overlayColor.intCode();
            }
            return RGB8.interp(wrappedArray.get(pos), overlayColor, opacity).intCode();
        }
        
        // return background array value
        return wrappedArray.getIntCode(pos);
    }
    
    @Override
    public void setIntCode(int[] pos, int intCode)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }
    
    
    // =============================================================
    // Implementation of IntVectorArray interface

    @Override
    public int getSample(int[] pos, int channel)
    {
        if (binaryMask.getBoolean(pos))
        {
            if (binaryMask.getBoolean(pos))
            {
                return overlayColor.getSample(channel);
            }
            return (int) interp(wrappedArray.getSample(pos, channel), overlayColor.getSample(channel), opacity);
        }
        
        // return background array value
        return wrappedArray.getSample(pos, channel);
    }

    @Override
    public void setSample(int[] pos, int channel, int value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }
    
    
    // =============================================================
    // Implementation of VectorArray interface

    @Override
    public double getValue(int[] pos, int channel)
    {
        if (binaryMask.getBoolean(pos))
        {
            if (binaryMask.getBoolean(pos))
            {
                return overlayColor.getValue(channel);
            }
            return interp(wrappedArray.getValue(pos, channel), overlayColor.getValue(channel), opacity);
        }
        
        // return background array value
        return wrappedArray.getValue(pos, channel);
    }
    
    private double interp(double v0, double v1, double t)
    {
        return v0 * (1.0 - t) + v1 * t;
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }
    
    
    // =============================================================
    // Implementation of Array interface

    @Override
    public RGB8 get(int[] pos)
    {
        if (binaryMask.getBoolean(pos))
        {
            if (opacity == 1.0)
            {
                return this.overlayColor;
            }
            return RGB8.interp(wrappedArray.get(pos), overlayColor, opacity);
        }
        
        // return background array value
        return wrappedArray.get(pos);
    }

    @Override
    public void set(int[] pos, RGB8 value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }
    
    @Override
    public int dimensionality()
    {
        return wrappedArray.dimensionality();
    }

    @Override
    public int[] size()
    {
        return wrappedArray.size();
    }

    @Override
    public int size(int dim)
    {
        return wrappedArray.size(dim);
    }
}
