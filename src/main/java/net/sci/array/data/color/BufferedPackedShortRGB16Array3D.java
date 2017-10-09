/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.UInt16Array;
import net.sci.array.type.RGB16;
import net.sci.array.type.UInt16;

/**
 * Implementation of 2D array of RGB16, by keeping value in a buffer of short
 * values, with packed channels.
 * 
 * The buffer contains first all values for red, then all values for green, then
 * all values for blue.
 * 
 * @author dlegland
 *
 */
public class BufferedPackedShortRGB16Array3D extends RGB16Array3D
{
	// =============================================================
	// Class variables

	UInt16Array buffer;
	
	// =============================================================
	// Constructor

	public BufferedPackedShortRGB16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = UInt16Array.create(size0, size1, size2, 3);
	}

	public BufferedPackedShortRGB16Array3D(UInt16Array buffer)
	{
		super(buffer.getSize(0), buffer.getSize(1), buffer.getSize(1));
		if (buffer.dimensionality() != 4)
		{
			throw new IllegalArgumentException("Requires an array of UInt16 with 4 dimensions");
		}
		if (buffer.getSize(3) != 3)
		{
			throw new IllegalArgumentException("Requires an array of UInt16 with 3 slices");
		}
		this.buffer = buffer;
	}


	// =============================================================
	// Implementation of the VectorArray3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray3D#getValues(int, int, int)
	 */
	@Override
	public double[] getValues(int x, int y, int z)
	{
		double[] values = new double[3];
		for (int c = 0; c < 3; c++)
		{
			values[c] = this.buffer.getInt(new int[]{x, y, z, c});
		}
		return values;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray3D#setValues(int, int, int, double[])
	 */
	@Override
	public void setValues(int x, int y, int z, double[] values)
	{
		for (int c = 0; c < 3; c++)
		{
			this.buffer.setValue(new int[]{x, y, z, c}, values[c]);
		}
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray3D#getValue(int, int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z, int c)
	{
		return this.buffer.getValue(new int[]{x, y, z, c});
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray3D#setValue(int, int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, int c, double value)
	{
		this.buffer.setValue(new int[]{x, y, z, c}, value);
	}

	
	// =============================================================
	// Implementation of the Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#get(int, int, int)
	 */
	@Override
	public RGB16 get(int x, int y, int z)
	{
		int r = this.buffer.getInt(new int[]{x, y, z, 0});
		int g = this.buffer.getInt(new int[]{x, y, z, 1});
		int b = this.buffer.getInt(new int[]{x, y, z, 2});
		return new RGB16(r, g, b);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, int z, RGB16 rgb)
	{
		this.buffer.setInt(new int[]{x, y, z, 0}, rgb.getSample(0));
		this.buffer.setInt(new int[]{x, y, z, 1}, rgb.getSample(1));
		this.buffer.setInt(new int[]{x, y, z, 2}, rgb.getSample(2));
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public RGB16Array newInstance(int... dims)
	{
		return RGB16Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array3D#duplicate()
	 */
	@Override
	public RGB16Array3D duplicate()
	{
		UInt16Array newBuffer = this.buffer.duplicate();
		return new BufferedPackedShortRGB16Array3D(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array3D#iterator()
	 */
	@Override
	public RGB16Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements RGB16Array.Iterator
	{
		int posX = -1;
		int posY = 0;
		int posZ = 0;
		
		public Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.posX < size0 - 1 || posY < size1 - 1 || posZ < size2 - 1;
		}

		@Override
		public RGB16 next()
		{
			forward();
			return get();
		}

		@Override
		public void forward()
		{
			this.posX++;
			if (posX >= size0)
			{
				posX = 0;
				posY++;
				if (posY >= size1)
				{
					posY = 0;
					posZ++;
				}
			}
		}

		@Override
		public double getValue(int c)
		{
			switch(c)
			{
			case 0: return buffer.getInt(new int[]{posX, posY, posZ, 0});
			case 1: return buffer.getInt(new int[]{posX, posY, posZ, 1});
			case 2: return buffer.getInt(new int[]{posX, posY, posZ, 2});
			default: throw new IllegalArgumentException(
					"Channel index must be comprised between 0 and 2, not " + c);
			}
		}

        @Override
        public double[] getValues(double[] values)
        {
            values[0] = buffer.getInt(new int[]{posX, posY, posZ, 0});
            values[1] = buffer.getInt(new int[]{posX, posY, posZ, 1});
            values[2] = buffer.getInt(new int[]{posX, posY, posZ, 2});
            return values;
        }

        @Override
		public void setValue(int c, double value)
		{
			switch(c)
			{
			case 0: buffer.setInt(new int[]{posX, posY, posZ, 0}, UInt16.clamp(value));
			case 1: buffer.setInt(new int[]{posX, posY, posZ, 1}, UInt16.clamp(value));
			case 2: buffer.setInt(new int[]{posX, posY, posZ, 2}, UInt16.clamp(value));
			default: new IllegalArgumentException(
					"Channel index must be comprised between 0 and 2, not " + c);
			}
		}

		@Override
		public RGB16 get()
		{
			int r = buffer.getInt(new int[]{posX, posY, posZ, 0});
			int g = buffer.getInt(new int[]{posX, posY, posZ, 1});
			int b = buffer.getInt(new int[]{posX, posY, posZ, 2});
			return new RGB16(r, g, b);
		}

		@Override
		public void set(RGB16 rgb)
		{
			buffer.setInt(new int[]{posX, posY, posZ, 0}, rgb.getSample(0));
			buffer.setInt(new int[]{posX, posY, posZ, 1}, rgb.getSample(1));
			buffer.setInt(new int[]{posX, posY, posZ, 2}, rgb.getSample(2));
		}

		@Override
		public double getValue()
		{
			return get().getValue();
		}

		@Override
		public void setValue(double value)
		{
			int val = UInt16.clamp(value);
			buffer.setInt(new int[]{posX, posY, posZ, 0}, val);
			buffer.setInt(new int[]{posX, posY, posZ, 1}, val);
			buffer.setInt(new int[]{posX, posY, posZ, 2}, val);
		}
	}
}
