/**
 * 
 */
package net.sci.array.color;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.scalar.UInt8Array;

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
    /**
     * The reference array to overlay.
     */
    Array<?> baseArray;
    
    /**
     * The binary mask defining the overlay.
     */
    BinaryArray binaryArray;
    
    /**
     * The Overlay color. Default is red.
     */
    RGB8 overlayColor = RGB8.RED;

    /**
     * Creates an RGB8 view that combines original values of the reference array
     * with a color overlay defined by the binary mask and the specified color.
     * 
     * @param baseArray
     *            the reference array to overlay. Can be an instance of
     *            UInt8Array, BinaryArray, or RGB8Array.
     * @param binaryArray
     *            the binary mask defining the overlay. Should be the same size
     *            as the reference array.
     * @param overlayColor
     *            the overlay color.
     */
    public BinaryOverlayRGB8Array(Array<?> baseArray, BinaryArray binaryArray, RGB8 overlayColor)
    {
        // check input validity
        if (!Arrays.isSameSize(baseArray, binaryArray))
        {
            throw new IllegalArgumentException("Input arrays must have same size"); 
        }
        if (!isAllowedType(baseArray))
        {
            throw new IllegalArgumentException("Reference array must be an instance of UInt8Array, RGB8Array, or BinaryArray"); 
        }
        
        // copy values
        this.baseArray = baseArray;
        this.binaryArray = binaryArray;
        this.overlayColor = overlayColor;
    }
    
    private boolean isAllowedType(Array<?> array)
    {
        if (array instanceof UInt8Array) return true;
        if (array instanceof RGB8Array) return true;
        if (array instanceof BinaryArray) return true;
        return false;
    }
    
    @Override
    public int getIntCode(int[] pos)
    {
        if (binaryArray.getBoolean(pos))
        {
            return overlayColor.intCode;
        }
        return getBaseArrayIntCode(pos);
    }
    
    private int getBaseArrayIntCode(int... pos)
    {
        if (this.baseArray instanceof UInt8Array)
        {
            int val = ((UInt8Array) this.baseArray).getInt(pos);
            return RGB8.intCode(val, val, val);
        }
        else if (this.baseArray instanceof RGB8Array)
        {
            return ((RGB8Array) this.baseArray).getIntCode(pos);
        }
        else if (this.baseArray instanceof BinaryArray)
        {
            return ((BinaryArray) this.baseArray).getBoolean(pos) ? RGB8.WHITE.intCode : RGB8.BLACK.intCode;
        }
        throw new RuntimeException("Can not process array with class: " + this.baseArray.getClass());
    }
    
    
    @Override
    public double getValue(int[] pos, int channel)
    {
        if (binaryArray.getBoolean(pos))
        {
            return overlayColor.getSample(channel);
        }
        
        return getBaseArrayColor(pos).getSample(channel);
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }

    @Override
    public RGB8 get(int... pos)
    {
        if (binaryArray.getBoolean(pos))
        {
            return overlayColor;
        }
        
        return getBaseArrayColor(pos);
    }

    private RGB8 getBaseArrayColor(int... pos)
    {
        if (this.baseArray instanceof UInt8Array)
        {
            int val = ((UInt8Array) this.baseArray).getInt(pos);
            return new RGB8(val, val, val);
        }
        else if (this.baseArray instanceof RGB8Array)
        {
            return ((RGB8Array) this.baseArray).get(pos);
        }
        else if (this.baseArray instanceof BinaryArray)
        {
            return ((BinaryArray) this.baseArray).getBoolean(pos) ? RGB8.WHITE : RGB8.BLACK;
        }
        throw new RuntimeException("Can not process array with class: " + this.baseArray.getClass());
    }

    @Override
    public void set(int[] pos, RGB8 value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }
    
    @Override
    public int dimensionality()
    {
        return baseArray.dimensionality();
    }

    @Override
    public int[] size()
    {
        return baseArray.size();
    }

    @Override
    public int size(int dim)
    {
        return baseArray.size(dim);
    }

    @Override
    public PositionIterator positionIterator()
    {
        return baseArray.positionIterator();
    }
}
