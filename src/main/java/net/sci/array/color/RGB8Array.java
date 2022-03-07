/**
 * 
 */
package net.sci.array.color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.vector.IntVectorArray;

/**
 * An array that contains colors represented as instances of RGB8 type.
 * 
 * @author dlegland
 *
 */
public interface RGB8Array extends IntVectorArray<RGB8>, ColorArray<RGB8>
{
    // =============================================================
    // Static variables

    public static final Array.Factory<RGB8> factory = new Array.Factory<RGB8>()
    {
        @Override
        public RGB8Array create(int[] dims, RGB8 value)
        {
            RGB8Array array = RGB8Array.create(dims);
            array.fill(value);
            return array;
        }
    };

	// =============================================================
	// Static methods

	public static RGB8Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return RGB8Array2D.create(dims[0], dims[1]);
		case 3:
			return RGB8Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return RGB8ArrayND.create(dims);
		}
	}

	/**
	 * Splits the three channels of a RGB8 array.
	 * 
	 * @param array
	 *            the RGB8 array
	 * @return a collection containing the three channels
	 */
	public static Collection<UInt8Array> splitChannels(RGB8Array array)
	{
		// create result arrays
		int[] dims = array.size();
		UInt8Array redChannel = UInt8Array.create(dims);
		UInt8Array greenChannel = UInt8Array.create(dims);
		UInt8Array blueChannel = UInt8Array.create(dims);
		
		// create iterators
		Iterator rgbIter = array.iterator();
		UInt8Array.Iterator rIter = redChannel.iterator();
		UInt8Array.Iterator gIter = greenChannel.iterator();
		UInt8Array.Iterator bIter = blueChannel.iterator();
		
		// iterate over elements of all arrays simultaneously
        while (rgbIter.hasNext())
        {
            RGB8 rgb = rgbIter.next();
            rIter.setNextInt(rgb.getSample(0));
            gIter.setNextInt(rgb.getSample(1));
            bIter.setNextInt(rgb.getSample(2));
        }
		
		// create the collection of channels
		Collection<UInt8Array> channels = new ArrayList<>(3);
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
	public static HashMap<String, UInt8Array> mapChannels(RGB8Array array)
	{
		// create result arrays
		int[] dims = array.size();
		UInt8Array redChannel = UInt8Array.create(dims);
		UInt8Array greenChannel = UInt8Array.create(dims);
		UInt8Array blueChannel = UInt8Array.create(dims);
		
		// create iterators
		Iterator rgbIter = array.iterator();
		UInt8Array.Iterator rIter = redChannel.iterator();
		UInt8Array.Iterator gIter = greenChannel.iterator();
		UInt8Array.Iterator bIter = blueChannel.iterator();
		
		// iterate over elements of all arrays simultaneously
		while (rgbIter.hasNext())
        {
            RGB8 rgb = rgbIter.next();
            rIter.setNextInt(rgb.getSample(0));
            gIter.setNextInt(rgb.getSample(1));
            bIter.setNextInt(rgb.getSample(2));
        }
		
		// concatenate channels into a new collection
		HashMap<String, UInt8Array> map = new HashMap<String, UInt8Array>(3);
		map.put("red", redChannel);
		map.put("green", greenChannel);
		map.put("blue", blueChannel);

		return map;
	}
	
	/**
	 * Creates a new RGB8 array by concatenating the specified channels.
	 * 
	 * @param redChannel
	 *            an instance of UInt8Array representing the red channel
	 * @param greenChannel
	 *            an instance of UInt8Array representing the green channel
	 * @param blueChannel
	 *            an instance of UInt8Array representing the blue channel
	 * @return a new instance of RGB8 array
	 */
	public static RGB8Array mergeChannels(UInt8Array redChannel, UInt8Array greenChannel, UInt8Array blueChannel)
	{
		// create result array
		int[] dims = redChannel.size();
		RGB8Array result = create(dims);
		
		// get iterators
		Iterator rgbIter = result.iterator();
		UInt8Array.Iterator rIter = redChannel.iterator();
		UInt8Array.Iterator gIter = greenChannel.iterator();
		UInt8Array.Iterator bIter = blueChannel.iterator();
		
		// iterate over elements of all arrays simultaneously
		while (rgbIter.hasNext())
		{
			int r = rIter.next().getInt();
			int g = gIter.next().getInt();
			int b = bIter.next().getInt();
			rgbIter.forward();
			rgbIter.set(new RGB8(r, g, b));
		}
		
		return result;
	}

	/**
     * Computes a binary overlay over a color or grayscale image.
     * 
     * @param baseArray
     *            the array to use as base
     * @param overlay
     *            the binary array that specifies the pixels to colorize
     * @param color
     *            the overlay color
     * @return the reference to the baseArray
     */
	public static RGB8Array binaryOverlay(RGB8Array baseArray, BinaryArray overlay, RGB8 color)
	{
	    RGB8Array.Iterator iter1 = baseArray.iterator();
	    BinaryArray.Iterator iter2 = overlay.iterator();
	    
	    while (iter1.hasNext() && iter2.hasNext())
	    {
	        iter1.forward();
	        iter2.forward();
            
	        if (iter2.getBoolean())
	        {
	            iter1.set(color);
	        }
	    }

	    return baseArray;
	}

	/**
     * Convert the given array to a color array. If the input array is already
     * an instance of RGB8Array, simply returns it.
     * 
     * Can process RGB8, UInt8 or Binary arrays.
     * 
     * @param array
     *            the input array to convert
     * @return a RG8 array with the same size
     */
	public static RGB8Array convert(Array<?> array)
	{
	    // Return input RGB8 array
	    if (array instanceof RGB8Array)
	    {
	        return (RGB8Array) array;
	    }
	    
        // convert UInt8 to RGB8
        if (array instanceof UInt8Array)
        {
            return convertUInt8Array((UInt8Array) array);
        }
        
        // case of array that contains RGB8 elements without being an instance of RGB8Array
        if (array.dataType().isAssignableFrom(RGB8.class))
        {
            return convertArrayOfRGB8(array);
        }

        // convert Binary to RGB8
        if (array instanceof BinaryArray)
        {
            return convertBinaryArray((BinaryArray) array);
        }

        throw new RuntimeException("Can not convert to RGB8Array array of class: " + array.getClass());
	}
	
	private static RGB8Array convertArrayOfRGB8(Array<?> array)
	{
        RGB8Array res = RGB8Array.create(array.size());
        for (int[] pos : res.positions())
        {
            res.set(pos, (RGB8) array.get(pos));
        }
        return res;
	}

    private static RGB8Array convertUInt8Array(UInt8Array array)
    {
        RGB8Array res = RGB8Array.create(array.size());
        for (int[] pos : res.positions())
        {
            int gray = array.getInt(pos);
            res.set(pos, new RGB8(gray, gray, gray));
        }
        return res;
    }
    
	private static RGB8Array convertBinaryArray(BinaryArray array)
	{
        RGB8Array res = RGB8Array.create(array.size());
        for (int[] pos : res.positions())
        {
            res.set(pos, array.getBoolean(pos) ? RGB8.WHITE : RGB8.BLACK);
        }
        return res;
	}
	
	
	// =============================================================
	// Methods specific to RGB8Array

	/**
	 * Converts this RGB8 array into a new UInt8Array, by computing the
	 * maximum channel value for each element.
	 * 
	 * @return an UInt8 version of this RGB8 array
	 */
    public default UInt8Array convertToUInt8()
    {
        int[] sizes = this.size();
        UInt8Array result = UInt8Array.create(sizes);
        
        for(int[] pos : this.positions())
        {
            result.setInt(pos, get(pos).getInt());
        }
        
        return result;
    }
	
	public default UInt8Array createUInt8View()
	{
	    return new UInt8View(this);
	}

    // =============================================================
    // Default Implementation of the IntVectorArray interface

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
        set(pos, new RGB8(intValues));
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
        samples[channel] = UInt8.clamp(intValue);
        set(pos, new RGB8(samples));
    }
    

	// =============================================================
	// Specialization of VectorArray interface

    /**
     * Always returns 3, as this is the number of components of the RGB8 type.
     * 
     * @see net.sci.array.vector.VectorArray#channelCount()
     */
    @Override
    public default int channelCount()
    {
        return 3;
    }

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public default UInt8Array channel(int channel)
    {
        return new RGB8Array.ChannelView(this, channel);
    }
    
    public default Iterable<? extends UInt8Array> channels()
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
    public default java.util.Iterator<? extends UInt8Array> channelIterator()
    {
        // Create an anonymous class for the channel iterator 
        return new java.util.Iterator<UInt8Array>()
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
                return new RGB8Array.ChannelView(RGB8Array.this, channel);
            }
        };
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
		int r = UInt8.clamp(values[0]);
		int g = UInt8.clamp(values[1]);
		int b = UInt8.clamp(values[2]);
		set(pos, new RGB8(r, g, b));
	}


	// =============================================================
	// Specialization of Array interface

	@Override
	public default RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	@Override
	public default Array.Factory<RGB8> getFactory()
	{
		return factory;
	}

	@Override
	public default RGB8Array duplicate()
	{
		// create output array
		RGB8Array result = RGB8Array.create(this.size());
		
		for (int[] pos : result.positions())
		{
		    result.set(pos, get(pos));
		}
		
		// return result
		return result;
	}

	/**
     * Default iterator over RGB8 values of the RGB8Array, based on the
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
            public RGB8 next()
            {
                iter.forward();
                return RGB8Array.this.get(iter.get());
            }

            @Override
            public RGB8 get()
            {
                return RGB8Array.this.get(iter.get());
            }

            @Override
            public void set(RGB8 value)
            {
                RGB8Array.this.set(iter.get(), value);
            }
        };
    }

	
	// =============================================================
	// Inner interface

	@Override
	public default Class<RGB8> dataType()
	{
		return RGB8.class;
	}

	public interface Iterator extends IntVectorArray.Iterator<RGB8>
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
			samples[c] = UInt8.clamp(value);
			set(new RGB8(samples[0], samples[1], samples[2]));
		}
	}

	
    // =============================================================
    // Inner classes

	/**
     * Utility class that implements a view on a channel of a RGB8 array as a
     * UInt8Array.
     * 
     * @see RGB8Array.#channelIterator()
     */
    static class ChannelView implements UInt8Array
    {
        RGB8Array array;
        int channel;
        
        public ChannelView(RGB8Array array, int channel)
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
        public byte getByte(int... pos)
        {
            return (byte) array.getSample(pos, channel);
        }


        @Override
        public void setByte(int[] pos, byte byteValue)
        {
            array.setSample(pos, channel, byteValue & 0x00FF);
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

    
    /**
	 * 
	 * @author dlegland
	 * @see UInt8Array.Wrapper
	 */
	class UInt8View implements UInt8Array
	{
	    RGB8Array parent;
	    
	    UInt8View(RGB8Array parent)
	    {
	        this.parent = parent;
	    }

        @Override
        public int dimensionality()
        {
            return parent.dimensionality();
        }

        @Override
        public int[] size()
        {
            return parent.size();
        }

        @Override
        public int size(int dim)
        {
            return parent.size(dim);
        }

        @Override
        public UInt8 get(int... pos)
        {
            RGB8 rgb = parent.get(pos);
            return new UInt8(UInt8.clamp(rgb.getValue()));
        }

        @Override
        public void set(int[] pos, UInt8 value)
        {
            RGB8 rgb = new RGB8(value.getInt());
            parent.set(pos, rgb);
        }

        @Override
        public byte getByte(int... pos)
        {
            return get(pos).getByte();
        }

        @Override
        public void setByte(int[] pos, byte b)
        {
            RGB8 rgb = new RGB8(b & 0x00FF);
            parent.set(pos, rgb);
        }

        @Override
        public PositionIterator positionIterator()
        {
            return parent.positionIterator();
        }

        @Override
        public Iterator iterator()
        {
            return new Iterator(parent.iterator());
        }
        
        private class Iterator implements UInt8Array.Iterator
        {
            RGB8Array.Iterator parentIter;
            
            public Iterator(RGB8Array.Iterator parentIter) 
            {
                this.parentIter = parentIter;
            }
            
            @Override
            public boolean hasNext()
            {
                return parentIter.hasNext();
            }

            @Override
            public UInt8 next()
            {
                return parentIter.next().toUInt8();
            }

            @Override
            public void forward()
            {
                parentIter.forward();
            }

            @Override
            public UInt8 get()
            {
                return parentIter.get().toUInt8();
            }

            @Override
            public void set(UInt8 value)
            {
                parentIter.set(RGB8.fromUInt8(value));
            }
            
            @Override
            public byte getByte()
            {
                return parentIter.get().toUInt8().getByte();
            }

            @Override
            public void setByte(byte b)
            {
                parentIter.set(new RGB8(b & 0x00FF, b & 0x00FF, b & 0x00FF));
            }
        }
	}
}
