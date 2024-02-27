/**
 * 
 */
package net.sci.array.color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.vector.IntVectorArray;

/**
 * An array that contains colors represented as instances of RGB16 type.
 * 
 * @author dlegland
 *
 */
public interface RGB16Array extends IntVectorArray<RGB16,UInt16>, ColorArray<RGB16>
{
    // =============================================================
    // Static variables

    public static final Factory factory = new DenseRGB16ArrayFactory();
    
    
    // =============================================================
    // Static methods

    public static RGB16Array create(int... dims)
    {
        return factory.create(dims);
    }

    /**
     * Splits the three channels of a RGB16 array.
     * 
     * @param array
     *            the RGB16 array
     * @return a collection containing the three channels
     */
    public static Collection<UInt16Array> splitChannels(RGB16Array array)
    {
        // create result arrays
        int[] dims = array.size();
        UInt16Array redChannel = UInt16Array.create(dims);
        UInt16Array greenChannel = UInt16Array.create(dims);
        UInt16Array blueChannel = UInt16Array.create(dims);
        
        // create iterators
        Iterator rgbIter = array.iterator();
        UInt16Array.Iterator rIter = redChannel.iterator();
        UInt16Array.Iterator gIter = greenChannel.iterator();
        UInt16Array.Iterator bIter = blueChannel.iterator();
        
        // iterate over elements of all arrays simultaneously
        while (rgbIter.hasNext())
        {
            RGB16 rgb = rgbIter.next();
            rIter.setNextInt(rgb.getSample(0));
            gIter.setNextInt(rgb.getSample(1));
            bIter.setNextInt(rgb.getSample(2));
        }
        
        // create the collection of channels
        Collection<UInt16Array> channels = new ArrayList<>(3);
        channels.add(redChannel);
        channels.add(greenChannel);
        channels.add(blueChannel);
        
        return channels;
    }
    
    /**
     * Splits the channels of the color image and returns the new ByteImages
     * into a Map, using channel names as key.
     * 
     * Example:
     * 
     * <pre>
     * <code>
     * ColorProcessor colorImage = ...
     * HashMap&lt;String, ByteProcessor&gt; channels = mapChannels(colorImage);
     * ByteProcessor blue = channels.get("blue");
     * </code>
     * </pre>
     * 
     * @param array
     *            the original color array
     * @return a hashmap indexing the three channels by their names
     */
    public static HashMap<String, UInt16Array> mapChannels(RGB16Array array)
    {
        // create result arrays
        int[] dims = array.size();
        UInt16Array redChannel = UInt16Array.create(dims);
        UInt16Array greenChannel = UInt16Array.create(dims);
        UInt16Array blueChannel = UInt16Array.create(dims);
        
        // create iterators
        Iterator rgbIter = array.iterator();
        UInt16Array.Iterator rIter = redChannel.iterator();
        UInt16Array.Iterator gIter = greenChannel.iterator();
        UInt16Array.Iterator bIter = blueChannel.iterator();
        
        // iterate over elements of all arrays simultaneously
        while (rgbIter.hasNext())
        {
            RGB16 rgb = rgbIter.next();
            rIter.setNextInt(rgb.getSample(0));
            gIter.setNextInt(rgb.getSample(1));
            bIter.setNextInt(rgb.getSample(2));
        }
        
        // concatenate channels into a new collection
        HashMap<String, UInt16Array> map = new HashMap<String, UInt16Array>(3);
        map.put("red", redChannel);
        map.put("green", greenChannel);
        map.put("blue", blueChannel);

        return map;
    }
    
    /**
     * Creates a new RGB16 array by concatenating the specified channels.
     * 
     * @param redChannel
     *            an instance of UInt16Array representing the red channel
     * @param greenChannel
     *            an instance of UInt16Array representing the green channel
     * @param blueChannel
     *            an instance of UInt16Array representing the blue channel
     * @return a new instance of RGB16 array
     */
    public static RGB16Array mergeChannels(UInt16Array redChannel, UInt16Array greenChannel, UInt16Array blueChannel)
    {
        // create result array
        int[] dims = redChannel.size();
        RGB16Array result = create(dims);
        
        // get iterators
        Iterator rgbIter = result.iterator();
        UInt16Array.Iterator rIter = redChannel.iterator();
        UInt16Array.Iterator gIter = greenChannel.iterator();
        UInt16Array.Iterator bIter = blueChannel.iterator();
        
        // iterate over elements of all arrays simultaneously
        while (rgbIter.hasNext())
        {
            int r = rIter.next().getInt();
            int g = gIter.next().getInt();
            int b = bIter.next().getInt();
            rgbIter.setNext(new RGB16(r, g, b));
        }
        
        return result;
    }

    /**
     * Convert the given array to a color array. If the input array is already
     * an instance of RGB16Array, simply returns it.
     * 
     * Can process RGB16, UInt16 or Binary arrays.
     * 
     * @param array
     *            the input array to convert
     * @return a RG16 array with the same size
     */
    public static RGB16Array convert(Array<?> array)
    {
        // Return input RGB16 array
        if (array instanceof RGB16Array)
        {
            return (RGB16Array) array;
        }
        
        // convert UInt16 to RGB16
        if (array instanceof UInt16Array)
        {
            return convertUInt16Array((UInt16Array) array);
        }
        
        // case of array that contains RGB16 elements without being an instance of RGB16Array
        if (array.dataType().isAssignableFrom(RGB16.class))
        {
            return convertArrayOfRGB16(array);
        }

        // convert Binary to RGB16
        if (array instanceof BinaryArray)
        {
            return convertBinaryArray((BinaryArray) array);
        }

        throw new RuntimeException("Can not convert to RGB16Array array of class: " + array.getClass());
    }
    
    private static RGB16Array convertArrayOfRGB16(Array<?> array)
    {
        RGB16Array res = RGB16Array.create(array.size());
        for (int[] pos : res.positions())
        {
            res.set(pos, (RGB16) array.get(pos));
        }
        return res;
    }

    private static RGB16Array convertUInt16Array(UInt16Array array)
    {
        RGB16Array res = RGB16Array.create(array.size());
        for (int[] pos : res.positions())
        {
            int gray = array.getInt(pos);
            res.set(pos, new RGB16(gray, gray, gray));
        }
        return res;
    }
    
    private static RGB16Array convertBinaryArray(BinaryArray array)
    {
        RGB16 white = new RGB16(0x0FFFF, 0x0FFFF, 0x0FFFF);
        RGB16 black = new RGB16(0, 0, 0);
        RGB16Array res = RGB16Array.create(array.size());
        for (int[] pos : res.positions())
        {
            res.set(pos, array.getBoolean(pos) ? white : black);
        }
        return res;
    }

    /**
     * Encapsulates the specified array into a new RGB16Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * RGB16Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a RGB16 view of the original array
     */
    @SuppressWarnings("unchecked")
    public static RGB16Array wrap(Array<?> array)
    {
        if (array instanceof RGB16Array)
        {
            return (RGB16Array) array;
        }
        
        if (RGB16.class.isAssignableFrom(array.dataType()))
        {
            return new Wrapper((Array<RGB16>) array);
        }
        
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.dataType());
    }
    
    
    // =============================================================
    // New methods specific to RGB16Array

    /**
     * Converts this RGB16 array into a new UInt16Array, by computing the
     * maximum channel value for each element.
     * 
     * @return an UInt16 version of this RGB16 array
     */
    public default UInt16Array convertToUInt16()
    {
        int[] sizes = this.size();
        UInt16Array result = UInt16Array.create(sizes);
        
        for (int[] pos : result.positions())
        {
            result.setInt(pos, this.get(pos).getInt());
        }
        
        return result;
    }

    /**
     * Converts this RGB16 array into a new RGB8 Array, by dividing the values
     * within each channel by 256.
     * 
     * @return an RGB8 version of this RGB16 array
     */
    public default RGB8Array convertToRGB8()
    {
        RGB8Array res = RGB8Array.create(this.size());
        int[] rgb16 = new int[3];
        int[] rgb8 = new int[3];
        for (int[] pos : res.positions())
        {
            getSamples(pos, rgb16);
            rgb8[0] = rgb16[0] / 256; 
            rgb8[1] = rgb16[1] / 256; 
            rgb8[2] = rgb16[2] / 256;
            res.setSamples(pos, rgb8);
        }
        return res;
    }

    
    // =============================================================
    // Default implementations of IntVectorArray interface
    
    @Override
    public default int[] getSamples(int[] pos)
    {
        return get(pos).getSamples();
    }

    @Override
    public default int[] getSamples(int[] pos, int[] intValues)
    {
        return get(pos).getSamples(intValues);
    }

    @Override
    public default void setSamples(int[] pos, int[] intValues)
    {
        set(pos, new RGB16(intValues));
    }

    @Override
    public default int getSample(int[] pos, int channel)
    {
        return get(pos).getSample(channel);
    }

    @Override
    public default void setSample(int[] pos, int channel, int intValue)
    {
        int[] samples = get(pos).getSamples();
        samples[channel] = UInt16.clamp(intValue);
        set(pos, new RGB16(samples));
    }
    

    // =============================================================
    // Default implementation of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public default UInt16Array channel(int channel)
    {
        return new RGB16Array.ChannelView(this, channel);
    }
  
    public default Iterable<? extends UInt16Array> channels()
    {
        return new Iterable<UInt16Array>()
        {
            @SuppressWarnings("unchecked")
            @Override
            public java.util.Iterator<UInt16Array> iterator()
            {
                return (java.util.Iterator<UInt16Array>) channelIterator();
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
    public default java.util.Iterator<? extends UInt16Array> channelIterator()
    {
        // Create an anonymous class for the channel iterator 
        return new java.util.Iterator<UInt16Array>()
        {
            int channel = -1;

            @Override
            public boolean hasNext()
            {
                return channel < 2;
            }

            @Override
            public UInt16Array next()
            {
                channel++;
                return new RGB16Array.ChannelView(RGB16Array.this, channel);
            }
        };
    }

    /**
     * Always returns 3, as this is the number of components of the RGB16 type.
     * 
     * @see net.sci.array.vector.VectorArray#channelCount()
     */
    @Override
    public default int channelCount()
    {
        return 3;
    }

    @Override
    public default double getValue(int[] pos, int channel)
    {
        return get(pos).getValues()[channel];
    }

    @Override
    public default void setValue(int[] pos, int channel, double value)
    {        
        int[] samples = get(pos).getSamples();
        samples[channel] = UInt16.convert(value);
        set(pos, new RGB16(samples));
    }

    @Override
    public default double[] getValues(int[] pos)
    {
        return get(pos).getValues();
    }

    @Override
    public default double[] getValues(int[] pos, double[] values)
    {
        return get(pos).getValues(values);
    }

    @Override
    public default void setValues(int[] pos, double[] values)
    {
        int r = UInt16.convert(values[0]);
        int g = UInt16.convert(values[1]);
        int b = UInt16.convert(values[2]);
        set(pos, new RGB16(r, g, b));
    }
    
    
    // =============================================================
    // Default implementations for Array interface

    /**
     * Override default behavior of Array interface to return a RGB16 element.
     * 
     * @return a RGB16 instance corresponding to a black color.
     */
    @Override
    public default RGB16 sampleElement()
    {
        return new RGB16(0, 0, 0);
    }
    
    @Override
    public default RGB16Array newInstance(int... dims)
    {
        return RGB16Array.create(dims);
    }

    @Override
    public default Array.Factory<RGB16> factory()
    {
        return factory;
    }

    @Override
    public default RGB16Array duplicate()
    {
        // create output array
        RGB16Array result = RGB16Array.create(this.size());

        // initialize iterators
        RGB16Array.Iterator iter1 = this.iterator();
        RGB16Array.Iterator iter2 = result.iterator();

        // copy values into output array
        while (iter1.hasNext())
        {
            iter2.setNext(iter1.next());
        }

        // return result
        return result;
    }

    @Override
    public default Class<RGB16> dataType()
    {
        return RGB16.class;
    }

    /**
     * Default iterator over RGB16 values of the RGB16Array, based on the
     * positionIterator.
     * 
     * @see #positionIterator()
     */
    public default Iterator iterator()
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
            public RGB16 next()
            {
                iter.forward();
                return RGB16Array.this.get(iter.get());
            }

            @Override
            public RGB16 get()
            {
                return RGB16Array.this.get(iter.get());
            }

            @Override
            public void set(RGB16 value)
            {
                RGB16Array.this.set(iter.get(), value);
            }
        };
    }

    // =============================================================
    // Inner classes

    /**
     * Utility class that implements a view on a channel of a RGB16 array as a
     * UInt16Array.
     * 
     * @see RGB16Array.#channel(int)
     * @see RGB16Array.#channelIterator()
     */
    static class ChannelView implements UInt16Array
    {
        RGB16Array array;
        int channel;
        
        public ChannelView(RGB16Array array, int channel)
        {
            int nChannels = 3;
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            
            this.array = array;
            this.channel = channel;
        }


        @Override
        public short getShort(int[] pos)
        {
            return (short) array.getSample(pos, channel);
        }


        @Override
        public void setShort(int[] pos, short shortValue)
        {
            array.setSample(pos, channel, shortValue & 0x00FFFF);
        }


        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }

        @Override
        public int[] size()
        {
            return array.size();
        }

        @Override
        public int size(int dim)
        {
            return array.size(dim);
        }

        @Override
        public PositionIterator positionIterator()
        {
            return array.positionIterator();
        }
    }
	
    static class Wrapper extends ArrayWrapperStub<RGB16> implements RGB16Array
    {
        Array<RGB16> array;
        
        public Wrapper(Array<RGB16> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public RGB16 get(int[] pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int[] pos, RGB16 rgb)
        {
            array.set(pos, rgb);
        }        
    }
    

    // =============================================================
    // Inner interface

    public interface Iterator extends IntVectorArray.Iterator<RGB16, UInt16>
    {
        @Override
        public default int getSample(int c)
        {
            return get().getSamples()[c];
        }

        @Override
        public default double getValue(int c)
        {
            return get().getValues()[c];
        }

        @Override
        public default void setValue(int c, double value)
        {
            int[] samples = get().getSamples();
            samples[c] = UInt16.convert(value);
            set(new RGB16(samples[0], samples[1], samples[2]));
        }
    }
    

    // =============================================================
    // Specialization of the Factory interface

    /**
     * Specialization of the ArrayFactory for generating instances of RGB16Array.
     */
    public interface Factory extends IntVectorArray.Factory<RGB16>
    {
        /**
         * Creates a new RGB16Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new RGB16Array initialized with zeros
         */
        public RGB16Array create(int... dims);

        /**
         * Creates a new RGB16Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public RGB16Array create(int[] dims, RGB16 value);
    }
}
