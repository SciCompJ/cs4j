/**
 * 
 */
package net.sci.array.color;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.numeric.UInt8Array;

/**
 * A RGB8Array view that combines three UInt8Array instances.
 * 
 * @author dlegland
 *
 */
public class MergeChannelsRGB8Array implements RGB8Array, Array.View<RGB8>
{
    // =============================================================
    // Inner members

    /**
     * The reference array for red channel.
     */
    UInt8Array redChannel;
    
    /**
     * The reference array for green channel.
     */
    UInt8Array greenChannel;

    /**
     * The reference array for blue channel.
     */
    UInt8Array blueChannel;
    

    // =============================================================
    // Constructor

    public MergeChannelsRGB8Array(UInt8Array redChannel, UInt8Array greenChannel, UInt8Array blueChannel)
    {
        if (!Arrays.isSameDimensionality(redChannel, greenChannel) || !Arrays.isSameDimensionality(redChannel, blueChannel))
        {
            throw new RuntimeException("Arrays must have same dimensionality");
        }
        if (!Arrays.isSameSize(redChannel, greenChannel) || !Arrays.isSameSize(redChannel, blueChannel))
        {
            throw new RuntimeException("Arrays must have same size");
        }
        
        this.redChannel = redChannel;
        this.greenChannel = greenChannel;
        this.blueChannel = blueChannel;
    }

    
    // =============================================================
    // Implementation of the RGB8Array interface

    @Override
    public int getGrayValue(int[] pos)
    {
        int r = this.redChannel.getInt(pos);
        int g = this.greenChannel.getInt(pos);
        int b = this.blueChannel.getInt(pos);
        return RGB8.grayValue(r, g, b);
    }
    
    @Override
    public int getMaxSample(int[] pos)
    {
        int r = this.redChannel.getInt(pos);
        int g = this.greenChannel.getInt(pos);
        int b = this.blueChannel.getInt(pos);
        return Math.max(Math.max(r, g), b);
    }
    
    @Override
    public int getIntCode(int[] pos)
    {
        int r = this.redChannel.getInt(pos);
        int g = this.greenChannel.getInt(pos);
        int b = this.blueChannel.getInt(pos);
        return RGB8.intCode(r, g, b);
    }
    
    @Override
    public void setIntCode(int[] pos, int intCode)
    {
        this.redChannel.setInt(pos, intCode & 0x00FF);
        this.greenChannel.setInt(pos, (intCode >> 8) & 0x00FF);
        this.blueChannel.setInt(pos, (intCode >> 16) & 0x00FF);
    }
    

    // =============================================================
    // Implementation of the VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public UInt8Array channel(int channel)
    {
        switch(channel)
        {
            case 0: return redChannel;
            case 1: return greenChannel;
            case 2: return blueChannel;
            default: throw new RuntimeException("channel index must be comprised between 0 and 2");
        }
    }
    
    public Iterable<? extends UInt8Array> channels()
    {
        return new Iterable<UInt8Array>()
        {
            @SuppressWarnings("unchecked")
            @Override
            public java.util.Iterator<UInt8Array> iterator()
            {
                return (java.util.Iterator<UInt8Array>) channelIterator();
            }
        };
    }
    
    /**
     * Returns an iterator over the channels within this RGB8 Array, each
     * channel implementing the UInt8Array interface.
     * 
     * A default implementation is provided, but specialized implementations may
     * provide more efficient or more specific implementations.
     */
    public java.util.Iterator<? extends UInt8Array> channelIterator()
    {
        // Create an anonymous class for the channel iterator 
        return new java.util.Iterator<UInt8Array>()
        {
            int index = -1;

            @Override
            public boolean hasNext()
            {
                return index < 2;
            }

            @Override
            public UInt8Array next()
            {
                index++;
                return channel(index);
            }
        };
    }
    
    @Override
    public double getValue(int[] pos, int channel)
    {
        switch(channel)
        {
            case 0: return redChannel.getValue(pos);
            case 1: return greenChannel.getValue(pos);
            case 2: return blueChannel.getValue(pos);
            default: throw new RuntimeException("channel index must be comprised between 0 and 2");
        }
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }
    
    
    // =============================================================
    // Implementation of the Array.View interface

    @Override
    public Collection<Array<?>> parentArrays()
    {
        return List.of(redChannel, greenChannel, blueChannel);
    }


    // =============================================================
    // Implementation of the Array interface

    @Override
    public RGB8 get(int[] pos)
    {
        int r = this.redChannel.getInt(pos);
        int g = this.greenChannel.getInt(pos);
        int b = this.blueChannel.getInt(pos);
        
        return new RGB8(r, g, b);
    }

    @Override
    public void set(int[] pos, RGB8 value)
    {
        throw new RuntimeException("Can not modify values of a view class");
    }
    
    @Override
    public int dimensionality()
    {
        return redChannel.dimensionality();
    }

    @Override
    public int[] size()
    {
        return redChannel.size();
    }

    @Override
    public int size(int dim)
    {
        return redChannel.size(dim);
    }
}
