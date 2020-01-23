/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArrayUInt8View;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.vector.VectorArray;

/**
 * Encapsulates a vector array and computes a RGB representation on-the-fly.
 * 
 * @author dlegland
 *
 */
public class VectorArrayRGB8View implements RGB8Array
{
    // =============================================================
    // Class variables

    VectorArray<?> array;
    
    int[] channels;
    double[][] extents;
    
    
    // =============================================================
    // Constructors

    public VectorArrayRGB8View(VectorArray<?> array, int c1, double[] extent1,
            int c2, double[] extent2, int c3, double[] extent3)
    {
        this.array = array;
        this.channels = new int[] { c1, c2, c3 };
        this.extents = new double[][]{extent1, extent2, extent3};
    }
    
    public VectorArrayRGB8View(VectorArray<?> array, 
            int c1, double min1, double max1, 
            int c2, double min2, double max2, 
            int c3, double min3, double max3)
    {
        this.array = array;
        this.channels = new int[] { c1, c2, c3 };
        this.extents = new double[][]{{min1, max1}, {min2, max2}, {min3, max3}};
    }
    
    public VectorArrayRGB8View(VectorArray<?> array, int c1, int c2, int c3)
    {
        this.array = array;
        this.channels = new int[] { c1, c2, c3 };
        this.extents = new double[][]{array.channel(c1).finiteValueRange(), array.channel(c2).finiteValueRange(), array.channel(c3).finiteValueRange()};
    }
    
    
    // =============================================================
    // Implementation of RGB8Array interface

    private static final double scaleValue(double value, double[] extent)
    {
        return (value - extent[0]) / (extent[1] - extent[0]);
    }

    
    // =============================================================
    // Implementation of VectorArray interface
    
    /* (non-Javadoc)
     * @see net.sci.array.vector.VectorArray#getValue(int[], int)
     */
    @Override
    public double getValue(int[] pos, int channel)
    {
        double value = this.array.getValue(pos, channels[channel]); 
        return scaleValue(value, extents[channel]);
    }

    /* (non-Javadoc)
     * @see net.sci.array.vector.VectorArray#setValue(int[], int, double)
     */
    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        throw new RuntimeException("Can not modify values");
    }


    /* (non-Javadoc)
     * @see net.sci.array.color.RGB8Array#channel(int)
     */
    @Override
    public UInt8Array channel(int channelIndex)
    {
        ScalarArray<?> channel = this.array.channel(this.channels[channelIndex]);
        double[] ext = this.extents[channelIndex];
        return new ScalarArrayUInt8View(channel, ext[0], ext[1]);
    }

    /* (non-Javadoc)
     * @see net.sci.array.color.RGB8Array#channels()
     */
    @Override
    public Iterable< UInt8Array> channels()
    {
        return new Iterable<UInt8Array>()
        { 
            @Override
            public java.util.Iterator<UInt8Array> iterator()
            {
                return channelIterator();
            }
        };
    }

    /* (non-Javadoc)
     * @see net.sci.array.color.RGB8Array#channelIterator()
     */
    @Override
    public java.util.Iterator<UInt8Array> channelIterator()
    {
        return new ChannelIterator();
    }
    

    // =============================================================
    // Implementation of Array interface
    
    /* (non-Javadoc)
     * @see net.sci.array.Array#get(int[])
     */
    @Override
    public RGB8 get(int... pos)
    {
        double red   = array.getValue(pos, channels[0]);
        double green = array.getValue(pos, channels[1]);
        double blue  = array.getValue(pos, channels[2]);
        int r8 = (int) (255 * scaleValue(red,   extents[0]));
        int g8 = (int) (255 * scaleValue(green, extents[1]));
        int b8 = (int) (255 * scaleValue(blue,  extents[2]));
        return new RGB8(r8, g8, b8);
    }

    /* (non-Javadoc)
     * @see net.sci.array.Array#set(int[], java.lang.Object)
     */
    @Override
    public void set(RGB8 value, int... pos)
    {
        throw new RuntimeException("Can not modify values");
    }

    /* (non-Javadoc)
     * @see net.sci.array.Array#positionIterator()
     */
    @Override
    public net.sci.array.Array.PositionIterator positionIterator()
    {
        return this.array.positionIterator();
    }

    /* (non-Javadoc)
     * @see net.sci.array.Array#dimensionality()
     */
    @Override
    public int dimensionality()
    {
        return this.array.dimensionality();
    }

    /* (non-Javadoc)
     * @see net.sci.array.Array#size()
     */
    @Override
    public int[] size()
    {
        return this.array.size();
    }


    /* (non-Javadoc)
     * @see net.sci.array.Array#size(int)
     */
    @Override
    public int size(int dim)
    {
        return this.array.size(dim);
    }


    /* (non-Javadoc)
     * @see net.sci.array.color.RGB8Array#iterator()
     */
    @Override
    public Iterator iterator()
    {
        return new Iterator()
        {
            PositionIterator iter = positionIterator();

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public void forward()
            {
                iter.forward();
            }

            @Override
            public RGB8 get()
            {
                return VectorArrayRGB8View.this.get(iter.get());
            }

            @Override
            public void set(RGB8 value)
            {
                throw new RuntimeException("Can not modify values of a VectorArrayRGB8View");
            }

            @Override
            public double getValue(int c)
            {
                int channel = channels[c];
                double val = array.getValue(iter.get(), channel);
                return 255 * scaleValue(val, extents[c]);
            }

            @Override
            public void setValue(int c, double value)
            {
                throw new RuntimeException("Can not modify values of a VectorArrayRGB8View");
            }
        };
    }

    

    private class ChannelIterator implements java.util.Iterator<UInt8Array> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < 2;
        }

        @Override
        public UInt8Array next()
        {
            channel++;
            return VectorArrayRGB8View.this.channel(channel);
        }
    }
}
