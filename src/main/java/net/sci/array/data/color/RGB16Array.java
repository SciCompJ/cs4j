/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.ArrayFactory;
import net.sci.array.data.UInt16Array;
import net.sci.array.data.VectorArray;
import net.sci.array.type.RGB16;
import net.sci.array.type.UInt16;

/**
 * An array that contains colors that can be represented as instances of RGB16 type.
 * 
 * @author dlegland
 *
 */
public interface RGB16Array extends VectorArray<RGB16>
{
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
//		default:
//			return RGB8ArrayND.create(dims);
		}
		throw new RuntimeException("RGB16Arrays not yet implemented for dimension " + dims.length);
	}

//	/**
//	 * Splits the three channels of a RGB8 array.
//	 * 
//	 * @param array
//	 *            the RGB8 array
//	 * @return a collection containing the three channels
//	 */
//	public static Collection<UInt8Array> splitChannels(RGB8Array array)
//	{
//		// create result arrays
//		int[] dims = array.getSize();
//		UInt8Array redChannel = UInt8Array.create(dims);
//		UInt8Array greenChannel = UInt8Array.create(dims);
//		UInt8Array blueChannel = UInt8Array.create(dims);
//		
//		// create iterators
//		Iterator rgbIter = array.iterator();
//		UInt8Array.Iterator rIter = redChannel.iterator();
//		UInt8Array.Iterator gIter = greenChannel.iterator();
//		UInt8Array.Iterator bIter = blueChannel.iterator();
//		
//		// iterate over elements of all arrays simultaneously
//		while (rgbIter.hasNext())
//		{
//			rgbIter.forward();
//			RGB8 rgb = rgbIter.get();
//			rIter.forward();
//			rIter.setInt(rgb.getSample(0));
//			gIter.forward();
//			gIter.setInt(rgb.getSample(1));
//			bIter.forward();
//			bIter.setInt(rgb.getSample(2));
//		}
//		
//		// create the collection of channels
//		Collection<UInt8Array> channels = new ArrayList<>(3);
//		channels.add(redChannel);
//		channels.add(greenChannel);
//		channels.add(blueChannel);
//		
//		return channels;
//	}
	
//	/**
//	 * Splits the channels of the color image and returns the new ByteImages
//	 * into a Map, using channel names as key.
//	 * 
//	 * Example:
//	 * 
//	 * <pre>
//	 * <code>
//	 * ColorProcessor colorImage = ...
//	 * HashMap&lt;String, ByteProcessor&gt; channels = mapChannels(colorImage);
//	 * ByteProcessor blue = channels.get("blue");
//	 * </code>
//	 * </pre>
//	 * 
//	 * @param array
//	 *            the original color array
//	 * @return a hashmap indexing the three channels by their names
//	 */
//	public static HashMap<String, UInt8Array> mapChannels(RGB8Array array)
//	{
//		// create result arrays
//		int[] dims = array.getSize();
//		UInt8Array redChannel = UInt8Array.create(dims);
//		UInt8Array greenChannel = UInt8Array.create(dims);
//		UInt8Array blueChannel = UInt8Array.create(dims);
//		
//		// create iterators
//		Iterator rgbIter = array.iterator();
//		UInt8Array.Iterator rIter = redChannel.iterator();
//		UInt8Array.Iterator gIter = greenChannel.iterator();
//		UInt8Array.Iterator bIter = blueChannel.iterator();
//		
//		// iterate over elements of all arrays simultaneously
//		while (rgbIter.hasNext())
//		{
//			rgbIter.forward();
//			RGB8 rgb = rgbIter.get();
//			rIter.forward();
//			rIter.setInt(rgb.getSample(0));
//			gIter.forward();
//			gIter.setInt(rgb.getSample(1));
//			bIter.forward();
//			bIter.setInt(rgb.getSample(2));
//		}
//		
//		// concatenate channels into a new collection
//		HashMap<String, UInt8Array> map = new HashMap<String, UInt8Array>(3);
//		map.put("red", redChannel);
//		map.put("green", greenChannel);
//		map.put("blue", blueChannel);
//
//		return map;
//	}
	
//	/**
//	 * Creates a new RGB8 array by concatenating the specified channels.
//	 * 
//	 * @param redChannel
//	 *            an instance of UInt8Array representing the red channel
//	 * @param greenChannel
//	 *            an instance of UInt8Array representing the green channel
//	 * @param blueChannel
//	 *            an instance of UInt8Array representing the blue channel
//	 * @return a new instance of RGB8 array
//	 */
//	public static RGB8Array mergeChannels(UInt8Array redChannel, UInt8Array greenChannel, UInt8Array blueChannel)
//	{
//		// create result array
//		int[] dims = redChannel.getSize();
//		RGB8Array result = create(dims);
//		
//		// get iterators
//		Iterator rgbIter = result.iterator();
//		UInt8Array.Iterator rIter = redChannel.iterator();
//		UInt8Array.Iterator gIter = greenChannel.iterator();
//		UInt8Array.Iterator bIter = blueChannel.iterator();
//		
//		// iterate over elements of all arrays simultaneously
//		while (rgbIter.hasNext())
//		{
//			int r = rIter.next().getInt();
//			int g = gIter.next().getInt();
//			int b = bIter.next().getInt();
//			rgbIter.forward();
//			rgbIter.set(new RGB8(r, g, b));
//		}
//		
//		return result;
//	}
	

	// =============================================================
	// New methods specific to RGB16Array

	/**
	 * Converts this RGB16 array into a new UInt16Array, by computing the
	 * luminance of each element.
	 * 
	 * @return an UInt16 version of this RGB16 array
	 */
	public UInt16Array convertToUInt16();
	

	// =============================================================
	// Specialization of VectorArray interface

	/**
	 * Always returns 3, as this is the number of components of the RGB16 type.
	 * 
	 * @see net.sci.array.data.VectorArray#getVectorLength()
	 */
	@Override
	public default int getVectorLength()
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
	public default double getValue(int[] position)
	{
		return get(position).getValue();
	}

	@Override
	public default void setValue(int[] position, double value)
	{
		set(position, RGB16.fromValue(value));
	}

	@Override
	public default RGB16Array newInstance(int... dims)
	{
		return RGB16Array.create(dims);
	}

	@Override
	public default ArrayFactory<RGB16> getFactory()
	{
		return new ArrayFactory<RGB16>()
		{
			@Override
			public RGB16Array create(int[] dims, RGB16 value)
			{
				RGB16Array array = RGB16Array.create(dims);
				array.fill(value);
				return array;
			}
		};
	}

	@Override
	public default RGB16Array duplicate()
	{
		// create output array
		RGB16Array result = RGB16Array.create(this.getSize());

		// initialize iterators
		RGB16Array.Iterator iter1 = this.iterator();
		RGB16Array.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.forward();
			iter2.set(iter1.next());
		}
		
		// return result
		return result;
	}

	@Override
	public default Class<RGB16> getDataType()
	{
		return RGB16.class;
	}

	public Iterator iterator();

	
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
