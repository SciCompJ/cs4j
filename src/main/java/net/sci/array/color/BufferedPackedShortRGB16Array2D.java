/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array3D;

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
public class BufferedPackedShortRGB16Array2D extends RGB16Array2D
{
	// =============================================================
	// Class variables

	UInt16Array3D buffer;
	
	// =============================================================
	// Constructor

	public BufferedPackedShortRGB16Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = UInt16Array3D.create(size0, size1, 3);
	}

	public BufferedPackedShortRGB16Array2D(UInt16Array3D buffer)
	{
		super(buffer.size(0), buffer.size(1));
		if (buffer.size(2) != 3)
		{
			throw new IllegalArgumentException("Requires an array of UInt16 with 3 slices");
		}
		this.buffer = buffer;
	}


    // =============================================================
    // Implementation of the IntVectorArray2D interface

    @Override
    public int[] getSamples(int x, int y)
    {
        int[] values = new int[3];
        for (int c = 0; c < 3; c++)
        {
            values[c] = this.buffer.getInt(x, y, c);
        }
        return values;
    }

    @Override
    public int[] getSamples(int x, int y, int[] values)
    {
        for (int c = 0; c < 3; c++)
        {
            values[c] = this.buffer.getInt(x, y, c);
        }
        return values;
    }

    @Override
    public void setSamples(int x, int y, int[] intValues)
    {
        for (int c = 0; c < 3; c++)
        {
            this.buffer.setInt(x, y, c, intValues[c]);
        }
    }

    @Override
    public int getSample(int x, int y, int c)
    {
        return this.buffer.getInt(x, y, c);
    }

    @Override
    public void setSample(int x, int y, int c, int intValue)
    {
        this.buffer.setInt(x, y, c, intValue);
    }

    
	// =============================================================
	// Implementation of the VectorArray2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#getValues(int, int)
	 */
	@Override
	public double[] getValues(int x, int y)
	{
		double[] values = new double[3];
		for (int c = 0; c < 3; c++)
		{
			values[c] = this.buffer.getInt(x, y, c);
		}
		return values;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#setValues(int, int, double[])
	 */
	@Override
	public void setValues(int x, int y, double[] values)
	{
		for (int c = 0; c < 3; c++)
		{
			this.buffer.setValue(x, y, c, values[c]);
		}
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int c)
	{
		return this.buffer.getValue(x, y, c);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray2D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int c, double value)
	{
		this.buffer.setValue(x, y, c, value);
	}

	
	// =============================================================
	// Implementation of the Array2D interface

	@Override
    public void set(int x, int y, RGB16 rgb)
    {
        this.buffer.setInt(x, y, 0, rgb.getSample(0));
        this.buffer.setInt(x, y, 1, rgb.getSample(1));
        this.buffer.setInt(x, y, 2, rgb.getSample(2));
    }

    /* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public RGB16 get(int... pos)
	{
        int x = pos[0];
        int y = pos[1];
		int r = this.buffer.getInt(x, y, 0);
		int g = this.buffer.getInt(x, y, 1);
		int b = this.buffer.getInt(x, y, 2);
		return new RGB16(r, g, b);
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public RGB16Array newInstance(int... dims)
	{
		return RGB16Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array2D#duplicate()
	 */
	@Override
	public RGB16Array2D duplicate()
	{
		UInt16Array3D newBuffer = this.buffer.duplicate();
		return new BufferedPackedShortRGB16Array2D(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array2D#iterator()
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
		
		public Iterator() 
		{
		}
		
		@Override
		public boolean hasNext()
		{
			return this.posX < size0 - 1 || posY < size1 - 1;
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
			}
		}

		@Override
		public double getValue(int c)
		{
			switch(c)
			{
			case 0: return buffer.getInt(posX, posY, 0);
			case 1: return buffer.getInt(posX, posY, 1);
			case 2: return buffer.getInt(posX, posY, 2);
			default: throw new IllegalArgumentException(
					"Channel index must be comprised between 0 and 2, not " + c);
			}
		}

        @Override
        public double[] getValues(double[] values)
        {
            values[0] = buffer.getInt(posX, posY, 0);
            values[1] = buffer.getInt(posX, posY, 1);
            values[2] = buffer.getInt(posX, posY, 2);
            return values;
        }

        @Override
		public void setValue(int c, double value)
		{
			switch(c)
			{
			case 0: buffer.setInt(posX, posY, 0, UInt16.clamp(value));
			case 1: buffer.setInt(posX, posY, 1, UInt16.clamp(value));
			case 2: buffer.setInt(posX, posY, 2, UInt16.clamp(value));
			default: new IllegalArgumentException(
					"Channel index must be comprised between 0 and 2, not " + c);
			}
		}

		@Override
		public RGB16 get()
		{
			int r = buffer.getInt(posX, posY, 0);
			int g = buffer.getInt(posX, posY, 1);
			int b = buffer.getInt(posX, posY, 2);
			return new RGB16(r, g, b);
		}

		@Override
		public void set(RGB16 rgb)
		{
			buffer.setInt(posX, posY, 0, rgb.getSample(0));
			buffer.setInt(posX, posY, 1, rgb.getSample(1));
			buffer.setInt(posX, posY, 2, rgb.getSample(2));
		}
	}
}
