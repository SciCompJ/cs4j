/**
 * 
 */
package net.sci.array.data.color;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.data.UInt8Array;
import net.sci.array.data.VectorArray;
import net.sci.array.type.RGB8;

/**
 * An array that contains colors that can be represented as instances of RGB8 type.
 * 
 * @author dlegland
 *
 */
public interface RGB8Array extends VectorArray<RGB8>
{
	// =============================================================
	// Static methods

	public static RGB8Array create(int[] dims)
	{
		switch (dims.length)
		{
		case 2:
			return RGB8Array2D.create(dims[0], dims[1]);
		case 3:
			return RGB8Array3D.create(dims[0], dims[1], dims[2]);
		default:
			//TODO: implement the rest
			throw new RuntimeException("Can not create such image");
//			return UInt8ArrayND.create(dims);
		}
	}

	public static Collection<UInt8Array> splitChannels(RGB8Array array)
	{
		// create result arrays
		int[] dims = array.getSize();
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
			rgbIter.forward();
			RGB8 rgb = rgbIter.get();
			rIter.forward();
			rIter.setInt(rgb.getSample(0));
			gIter.forward();
			gIter.setInt(rgb.getSample(1));
			bIter.forward();
			bIter.setInt(rgb.getSample(2));
		}
		
		// create the collection of channels
		Collection<UInt8Array> channels = new ArrayList<>(3);
		channels.add(redChannel);
		channels.add(greenChannel);
		channels.add(blueChannel);
		
		return channels;
	}
	
	public static RGB8Array mergeChannels(UInt8Array redChannel, UInt8Array greenChannel, UInt8Array blueChannel)
	{
		int[] dims = redChannel.getSize();
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
	

	// =============================================================
	// Specialization of VectorArray interface

	/**
	 * Always returns 3, as this is the number of components of the RGB8 type.
	 * 
	 * @see net.sci.array.data.VectorArray#getVectorLength()
	 */
	@Override
	public default int getVectorLength()
	{
		return 3;
	}


	// =============================================================
	// Specialization of Array interface

	@Override
	public default RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	@Override
	public RGB8Array duplicate();

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<RGB8>
	{
	}

}
