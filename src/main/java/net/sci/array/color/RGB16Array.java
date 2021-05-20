/**
 * 
 */
package net.sci.array.color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sci.array.Array;
import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.vector.IntVectorArray;
import net.sci.array.vector.VectorArray;

/**
 * An array that contains colors represented as instances of RGB16 type.
 * 
 * @author dlegland
 *
 */
public interface RGB16Array extends IntVectorArray<RGB16>, ColorArray<RGB16>
{
    // =============================================================
    // Static variables

    public static final Array.Factory<RGB16> factory = new Array.Factory<RGB16>()
    {
        @Override
        public RGB16Array create(int[] dims, RGB16 value)
        {
            RGB16Array array = RGB16Array.create(dims);
            array.fill(value);
            return array;
        }
    };

	// =============================================================
	// Static methods

	public static RGB16Array create(int... dims)
	{
		switch (dims.length)
		{
		case 2:
			return RGB16Array2D.create(dims[0], dims[1]);
		case 3:
			return RGB16Array3D.create(dims[0], dims[1], dims[2]);
		default:
			return RGB16ArrayND.create(dims);
		}
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
    // Implementation of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public UInt16Array channel(int channel);
    
    public Iterable<? extends UInt16Array> channels();

    public java.util.Iterator<? extends UInt16Array> channelIterator();

    
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
		int r = UInt16.clamp(values[0]);
		int g = UInt16.clamp(values[1]);
		int b = UInt16.clamp(values[2]);
		set(pos, new RGB16(r, g, b));
	}


	// =============================================================
	// Specialization of Array interface

	@Override
	public default RGB16Array newInstance(int... dims)
	{
		return RGB16Array.create(dims);
	}

	@Override
	public default Array.Factory<RGB16> getFactory()
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
		while(iter1.hasNext())
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

	public abstract Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<RGB16>
	{
        @Override
        public default double getValue(int c)
        {
            return get().getValues()[c];
        }

		@Override
		public default void setValue(int c, double value)
		{
			int[] samples = get().getSamples();
			samples[c] = UInt16.clamp(value);
			set(new RGB16(samples[0], samples[1], samples[2]));
		}
	}

}
