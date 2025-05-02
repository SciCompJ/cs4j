/**
 * 
 */
package net.sci.image.io;

import net.sci.array.binary.BinaryArray;
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64VectorArray;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.image.Image;

/**
 * A list of common pixel types, providing methods for facilitating input and
 * output of image data.
 */
public class PixelType
{
    public static final PixelType BINARY = new PixelType(1, 1, true, false);
    public static final PixelType UINT8 = new PixelType(1, 8, true, false);
    public static final PixelType UINT12 = new PixelType(1, 12, true, false);
    public static final PixelType UINT16 = new PixelType(1, 16, true, false);
    public static final PixelType INT16 = new PixelType(1, 16, true, true);
    public static final PixelType INT32 = new PixelType(1, 32, true, true);
    public static final PixelType FLOAT32 = new PixelType(1, 32, false, true);
    public static final PixelType FLOAT64 = new PixelType(1, 64, false, true);
    public static final PixelType RGB8 = new PixelType(3, 8, true, false);
    public static final PixelType RGB16 = new PixelType(3, 16, true, false);
            
    public static final PixelType fromImage(Image image)
    {
        return switch (image.getData())
        {
            case BinaryArray x -> BINARY;
            case UInt8Array x -> UINT8;
            case UInt16Array x -> UINT16;
            case Int16Array x -> INT16;
            case Int32Array x -> INT32;
            case Float32Array x -> FLOAT32;
            case Float64Array x -> FLOAT64;
            case RGB8Array x -> RGB8;
            case RGB16Array x -> RGB16;
            case Float32VectorArray vectorArray -> new Float32Vector(vectorArray.channelCount());
            case Float64VectorArray vectorArray -> new Float64Vector(vectorArray.channelCount());
            default -> throw new RuntimeException("Unable to retrieve pixel type of image");
        };
    }
    
    /**
     * The number of samples per pixel, corresponding to number of channels or components.
     */
    private final int sampleCount;
    
    /**
     * The number of bits necessary to represent the value of a sample/channel value.
     */
    private final int bitsPerSample;
    
    /**
     * Boolean flag for integer data.
     */
    private final boolean isInteger;
    
    /**
     * In case of integer data, a boolean flag indicating whether the data type
     * supports signed (i.e. both positive and negative) values.
     */
    private final boolean isSigned;
    
    /**
     * Returns the number of samples / components used to represent this pixel.
     * Typical values are 1 for grayscale images, 3 or 4 for color images...
     * 
     * @return the number of samples / components used to represent this pixel.
     */
    public int sampleCount()
    {
        return sampleCount;
    }
    
    /**
     * Returns the number of bits used to represent as sample element of a
     * pixel. Typical values are multiples of 8.
     * 
     * @return the number of bits used to represent as sample element of a
     *         pixel.
     */
    public int bitsPerSample()
    {
        return bitsPerSample;
    }
    
    /**
     * Returns the minimal number of bytes necessary for representing a pixel value.
     * @return
     */
    public int byteCount()
    {
        return (int) (sampleCount * bitsPerSample / 8);
    }
    
    /**
     * Returns true if this pixel is represented with integer value(s).
     * 
     * @return true if this pixel is represented with integer value(s).
     */
    public boolean isInteger()
    {
        return isInteger;
    }

    /**
     * Returns true if the possible values of the pixel may be negative.
     * 
     * @return true if this type accepts negative values.
     */
    public boolean isSigned()
    {
        return isSigned;
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private PixelType(int sampleCount, int bitsPerSample, boolean isInteger, boolean isSigned)
    {
        this.sampleCount = sampleCount;
        this.bitsPerSample = bitsPerSample;
        this.isInteger = isInteger;
        this.isSigned = isSigned;
    }
    
    public static class Float32Vector extends PixelType
    {
        int nChannels;
        public Float32Vector(int nChannels)
        {
            super(nChannels, 32, false, true);
        }
    }
    
    public static class Float64Vector extends PixelType
    {
        int nChannels;
        public Float64Vector(int nChannels)
        {
            super(nChannels, 64, false, true);
        }
    }
}
