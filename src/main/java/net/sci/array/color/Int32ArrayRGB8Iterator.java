/**
 * 
 */
package net.sci.array.color;

import net.sci.array.color.RGB8Array;
import net.sci.array.scalar.Int32Array;
import net.sci.array.scalar.UInt8;

/**
 * Wraps an Iterator over Int32 values into a <code>RGB8Array.Iterator</code>.
 * This class is used by implementations of RGB8Array that encapsulate an Int32
 * array for representing RGB int codes.
 * 
 * 
 * @see Int32EncodedRGB8Array2D
 * @see Int32EncodedRGB8Array3D
 * @see Int32EncodedRGB8ArrayND
 *
 * @author dlegland
 *
 */
public class Int32ArrayRGB8Iterator implements RGB8Array.Iterator
{
    Int32Array.Iterator intIterator;
    
    public Int32ArrayRGB8Iterator(Int32Array array)
    {
        this.intIterator = array.iterator();
    }
    
    @Override
    public boolean hasNext()
    {
        return intIterator.hasNext();
    }
    
    @Override
    public RGB8 next()
    {
        forward();
        return get();
    }
    
    @Override
    public void forward()
    {
        intIterator.forward();
    }
    
    @Override
    public double getValue(int c)
    {
        int intCode = intIterator.getInt();
        switch (c)
        {
        case 0:
            return intCode & 0x00FF;
        case 1:
            return (intCode >> 8) & 0x00FF;
        case 2:
            return (intCode >> 16) & 0x00FF;
        }
        throw new IllegalArgumentException(
                "Channel number must be comprised between 0 and 2, not " + c);
    }
    
    @Override
    public double[] getValues(double[] values)
    {
        int intCode = intIterator.getInt();
        values[0] = intCode & 0x00FF;
        values[1] = (intCode >> 8) & 0x00FF;
        values[2] = (intCode >> 16) & 0x00FF;
        return values;
    }
    
    @Override
    public void setValue(int c, double value)
    {
        // extract the three components of current RGB value
        int intCode = intIterator.getInt();
        int r = intCode & 0x000000FF;
        int g = intCode & 0x0000FF00;
        int b = intCode & 0x00FF0000;
        
        // convert the specified value to the UInt8 range
        int intValue = UInt8.clamp(value);
        
        // update the specified component with the given value
        switch (c)
        {
        case 0:
            r = intValue;
            break;
        case 1:
            g = intValue << 8;
            break;
        case 2:
            b = intValue << 16;
            break;
        default:
            throw new IllegalArgumentException(
                    "Channel number must be comprised between 0 and 2, not " + c);
        }
        
        // creates a new int code representing the new RGB value
        intCode = r | g | b;
        intIterator.setInt(intCode);
    }
    
    @Override
    public RGB8 get()
    {
        return new RGB8(intIterator.getInt());
    }
    
    @Override
    public void set(RGB8 rgb)
    {
        intIterator.setInt(rgb.getIntCode());
    }
}
