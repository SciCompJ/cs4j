/**
 * 
 */
package net.sci.array.color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sci.array.Array;
import net.sci.array.ArrayWrapperStub;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.process.type.ConvertToUInt8;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.vector.IntVectorArray;

/**
 * An array that contains colors represented as instances of RGB8 type.
 * 
 * @author dlegland
 *
 */
public interface RGB8Array extends IntVectorArray<RGB8,UInt8>, ColorArray<RGB8>
{
    // =============================================================
    // Static variables

    public static final Factory factory = new DenseRGB8ArrayFactory();
    
    
    // =============================================================
    // Static methods

    public static RGB8Array create(int... dims)
    {
        return factory.create(dims);
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
        return new MergeChannelsRGB8Array(redChannel, greenChannel, blueChannel);
    }

    /**
     * Computes the color images that corresponds to overlay of a binary mask
     * onto a scalar array.
     * 
     * @param baseArray
     *            the array to use as base
     * @param binaryMask
     *            the binary array that specifies the array elements to
     *            colorize.
     * @param overlayColor
     *            the overlay color
     * @return a new color array corresponding to the overlay.
     */
    public static RGB8Array binaryOverlay(UInt8Array baseArray, BinaryArray binaryMask, RGB8 overlayColor)
    {
        RGB8Array res = convert(baseArray);
        
        for (int[] pos : res.positions())
        {
            if (binaryMask.getBoolean(pos))
            {
                res.set(pos, overlayColor);
            }
        }
    
        return res;
    }

    /**
     * Computes the color images that corresponds to overlay of a binary mask
     * onto a scalar array.
     * 
     * @param baseArray
     *            the array to use as base
     * @param binaryMask
     *            the binary array that specifies the array elements to colorize.
     * @param overlayColor
     *            the overlay color
     * @return a new color array corresponding to the overlay.
     */
    public static RGB8Array binaryOverlay(Array<?> baseArray, BinaryArray binaryMask, RGB8 overlayColor, double overlayOpacity)
    {
        if (baseArray.dataType() == UInt8.class)
        {
            return binaryOverlay_uint8(UInt8Array.wrap(baseArray), binaryMask, overlayColor, overlayOpacity);
        }
        
        // create result array
        RGB8Array res = (baseArray instanceof RGB8Array) 
                ? ((RGB8Array) baseArray).duplicate() 
                : convert(baseArray);
        
        // pre-compute opacity weights for gray and overlay
        final double op0 = 1.0 - overlayOpacity;
        final double op1 = overlayOpacity;
        
        final double rOvr = op0 * overlayColor.red();
        final double gOvr = op0 * overlayColor.green();
        final double bOvr = op0 * overlayColor.blue();
        
        for (int[] pos : res.positions())
        {
            if (binaryMask.getBoolean(pos))
            {
                res.setSample(pos, 0, (int) (res.getSample(pos, 0) * op1 + rOvr));
                res.setSample(pos, 1, (int) (res.getSample(pos, 1) * op1 + gOvr));
                res.setSample(pos, 2, (int) (res.getSample(pos, 2) * op1 + bOvr));
            }
        }

        return res;
    }
    
    /**
     * Computes the color images that corresponds to overlay of a binary mask
     * onto a scalar array.
     * 
     * @param baseArray
     *            the array to use as base
     * @param binaryMask
     *            the binary array that specifies the array elements to colorize.
     * @param overlayColor
     *            the overlay color
     * @return a new color array corresponding to the overlay.
     */
    private static RGB8Array binaryOverlay_uint8(UInt8Array baseArray, BinaryArray binaryMask, RGB8 overlayColor, double overlayOpacity)
    {
        // pre-compute opacity weights for gray and overlay
        final double op0 = 1.0 - overlayOpacity;
        final double op1 = overlayOpacity;
        
        final double rOvr = op1 * overlayColor.intRed();
        final double gOvr = op1 * overlayColor.intGreen();
        final double bOvr = op1 * overlayColor.intBlue();
        
        RGB8Array res = RGB8Array.create(baseArray.size());
        for (int[] pos : res.positions())
        {
            int gray = baseArray.getInt(pos);
            if (binaryMask.getBoolean(pos))
            {
                res.setSample(pos, 0, (int) (gray * op0 + rOvr));
                res.setSample(pos, 1, (int) (gray * op0 + gOvr));
                res.setSample(pos, 2, (int) (gray * op0 + bOvr));
            }
            else
            {
                res.setSamples(pos, new int[] {gray, gray, gray});
            }
        }

        return res;
    }
    
	/**
     * Applies a binary overlay over a color image (updates the reference
     * image).
     * 
     * @param baseArray
     *            the array to use as base
     * @param binaryMask
     *            the binary array that specifies the pixels to colorize
     * @param overlayColor
     *            the overlay color
     * @return the reference to the baseArray
     */
    public static RGB8Array overlayBinary(RGB8Array baseArray, BinaryArray binaryMask, RGB8 overlayColor)
    {
        for (int[] pos : baseArray.positions())
        {
            if (binaryMask.getBoolean(pos))
            {
                baseArray.set(pos, overlayColor);
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

        // case of array that contains RGB8 elements without being an instance
        // of RGB8Array
        if (RGB8.class.isAssignableFrom(array.dataType()))
        {
            return convertArrayOfRGB8(array);
        }

        // convert UInt8 to RGB8
        if (array instanceof UInt8Array)
        {
            return convertUInt8Array((UInt8Array) array);
        }
        
        // convert Binary to RGB8
        if (array instanceof BinaryArray)
        {
            return convertBinaryArray((BinaryArray) array);
        }

        throw new RuntimeException("Can not convert to RGB8Array array of class: " + array.getClass());
    }

    /**
     * Converts the specified scalar array into a color RGB8 array, by mapping
     * the range of values within <code>bounds</code> into the colors specified
     * by <code>colorMap</code>.
     * 
     * @param array
     *            the array to convert
     * @param bounds
     *            the range of values to map to extreme colors of the color map
     * @param colorMap
     *            the colormap
     * @return the result of conversion to RGB8Array
     */
    public static RGB8Array convert(ScalarArray<?> array, double[] bounds, ColorMap colorMap)
    {
        // compute slope for intensity conversions
        double extent = bounds[1] - bounds[0];
        int nColors = colorMap.size();

        // allocate result array
        RGB8Array res = RGB8Array.create(array.size());

        // iterate over elements of result array
        for (int[] pos : res.positions())
        {
            double value = array.getValue(pos);
            int index = (int) Math.min(Math.max((nColors - 1) * (value - bounds[0]) / extent, 0), (nColors - 1));
            RGB8 rgb8 = RGB8.fromColor(colorMap.getColor(index));
            res.set(pos, rgb8);
        }

        return res;
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
        res.fill(pos -> array.getBoolean(pos) ? RGB8.WHITE : RGB8.BLACK);
        return res;
    }

    /**
     * Encapsulates the specified array into a new RGB8Array, by creating a
     * Wrapper if necessary. If the original array is already an instance of
     * RGB8Array, it is returned.
     * 
     * @param array
     *            the original array
     * @return a RGB8 view of the original array
     */
    @SuppressWarnings("unchecked")
    public static RGB8Array wrap(Array<?> array)
    {
        if (array instanceof RGB8Array)
        {
            return (RGB8Array) array;
        }
        
        if (RGB8.class.isAssignableFrom(array.dataType()))
        {
            return new Wrapper((Array<RGB8>) array);
        }
        
        if (UInt8.class.isAssignableFrom(array.dataType()))
        {
            return new UInt8ArrayRGB8View(UInt8Array.wrap(array));
        }
        
        if (Binary.class.isAssignableFrom(array.dataType()))
        {
            return new BinaryArrayRGB8View(BinaryArray.wrap(array));
        }
        
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.dataType());
    }
    
    
    // =============================================================
    // Methods specific to RGB8Array

    /**
     * Converts this RGB8 array into a new UInt8Array, by computing the maximum
     * channel value for each element.
     * 
     * @return an UInt8 version of this RGB8 array
     */
    public default UInt8Array convertToUInt8()
    {
        return new ConvertToUInt8().processRGB8(this);
    }

    public default UInt8Array createUInt8View()
    {
        return new UInt8View(this);
    }

    /**
     * Returns the largest value within the samples of the RGB8 element at the
     * specified position.
     * 
     * The aim of this method is to facilitate the conversion of RGB8 arrays
     * into grayscale (UInt8) arrays.
     * 
     * @see RGB8.maxSample()
     * 
     * @param pos
     *            the position within array
     * @return largest value within the samples, as an integer.
     */
    public default int getMaxSample(int[] pos)
    {
        return get(pos).maxSample();
    }
    
	/**
     * Returns the intcode of the RGB8 value at specified position.
     * 
     * @see #setIntCode(int[], int)
     * 
     * @param pos
     *            the position within array
     * @return the intcode representing the RGB value
     */
    public default int getIntCode(int[] pos)
    {
        return get(pos).intCode();
    }
    
    /**
     * Default implementation for setting the intcode of an element of the
     * array.
     * 
     * @see #getIntCode(int[])
     * 
     * @param pos
     *            the position of the element to set
     * @param intCode
     *            the integer code of the RGB8 value
     */
    public default void setIntCode(int[] pos, int intCode)
    {
        setSample(pos, 0, intCode & 0x00FF);
        setSample(pos, 1, (intCode >> 8) & 0x00FF);
        setSample(pos, 2, (intCode >> 16) & 0x00FF);
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
                return RGB8Array.this.channel(index);
            }
        };
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
        samples[channel] = UInt8.convert(value);
        set(pos, new RGB8(samples));
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
        int r = UInt8.convert(values[0]);
        int g = UInt8.convert(values[1]);
        int b = UInt8.convert(values[2]);
        set(pos, new RGB8(r, g, b));
    }
    

    // =============================================================
    // Specialization of Array interface

    /**
     * Override default behavior of Array interface to return a RGB8 element.
     * 
     * @return the RGB8 element corresponding to RGB8.BLACK.
     */
    @Override
    public default RGB8 sampleElement()
    {
        return RGB8.BLACK;
    }
    
    @Override
    public default RGB8Array newInstance(int... dims)
    {
        return RGB8Array.create(dims);
    }

    @Override
    public default Array.Factory<RGB8> factory()
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

	public interface Iterator extends IntVectorArray.Iterator<RGB8,UInt8>
	{
        @Override
        public default int getSample(int c)
        {
            return get().getSample(c);
        }

        @Override
        public default double getValue(int c)
        {
            return get().getValue(c);
        }

		@Override
		public default void setValue(int c, double value)
		{
			int[] samples = get().getSamples();
			samples[c] = UInt8.convert(value);
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
        public byte getByte(int[] pos)
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

    static class Wrapper  extends ArrayWrapperStub<RGB8> implements RGB8Array
    {
        Array<RGB8> array;
        
        public Wrapper(Array<RGB8> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public RGB8 get(int[] pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int[] pos, RGB8 rgb)
        {
            array.set(pos, rgb);
        }        
    }
    
    /**
	 * 
	 * @author dlegland
	 * @see UInt8Array.ScalarArrayWrapper
	 */
	class UInt8View extends ArrayWrapperStub<UInt8> implements UInt8Array
	{
	    RGB8Array parent;
	    
	    UInt8View(RGB8Array parent)
	    {
	        super(parent);
	        this.parent = parent;
	    }

        @Override
        public UInt8 get(int[] pos)
        {
            return new UInt8(parent.get(pos).maxSample());
        }

        @Override
        public void set(int[] pos, UInt8 value)
        {
            RGB8 rgb = new RGB8(value.getInt());
            parent.set(pos, rgb);
        }

        @Override
        public byte getByte(int[] pos)
        {
            return get(pos).getByte();
        }

        @Override
        public void setByte(int[] pos, byte b)
        {
            RGB8 rgb = new RGB8(b & 0x00FF);
            parent.set(pos, rgb);
        }

	}
	
    
    // =============================================================
    // Specialization of the Factory interface

    /**
     * Specialization of the ArrayFactory for generating instances of RGB8Array.
     */
    public interface Factory extends IntVectorArray.Factory<RGB8>
    {
        /**
         * Creates a new RGB8Array of the specified dimensions, initialized
         * with zeros.
         * 
         * @param dims
         *            the dimensions of the new array
         * @return a new RGB8Array initialized with zeros
         */
        public RGB8Array create(int... dims);

        /**
         * Creates a new RGB8Array with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of IntArray
         */
        public RGB8Array create(int[] dims, RGB8 value);
    }
}
