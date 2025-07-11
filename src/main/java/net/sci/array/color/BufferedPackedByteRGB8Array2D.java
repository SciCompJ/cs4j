/**
 * 
 */
package net.sci.array.color;

import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array3D;

/**
 * Implementation of 2D array of RGB8, by keeping value in a byte buffer, with
 * packed channels.
 * 
 * The buffer contains first all values for red, then all values for green, then
 * all values for blue.
 * 
 * @author dlegland
 *
 */
public class BufferedPackedByteRGB8Array2D extends RGB8Array2D
{
	// =============================================================
	// Class variables

	UInt8Array3D buffer;
	
	// =============================================================
	// Constructor

	public BufferedPackedByteRGB8Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = UInt8Array3D.create(size0, size1, 3);
	}

	public BufferedPackedByteRGB8Array2D(UInt8Array3D buffer)
	{
		super(buffer.size(0), buffer.size(1));
		if (buffer.size(2) != 3)
		{
			throw new IllegalArgumentException("Requires an array of UInt8 with 3 slices");
		}
		this.buffer = buffer;
	}


    // =============================================================
    // Implementation of the RGB8Array interface

	@Override
    public int getGrayValue(int[] pos)
    {
        int r = this.buffer.getInt(pos[0], pos[1], 0);
        int g = this.buffer.getInt(pos[0], pos[1], 1);
        int b = this.buffer.getInt(pos[0], pos[1], 2);
        return RGB8.grayValue(r, g, b);
    }
    
    @Override
    public int getMaxSample(int[] pos)
    {
        int r = this.buffer.getInt(pos[0], pos[1], 0);
        int g = this.buffer.getInt(pos[0], pos[1], 1);
        int b = this.buffer.getInt(pos[0], pos[1], 2);
        return Math.max(Math.max(r, g), b);
    }
    
    @Override
    public int getIntCode(int[] pos)
    {
        int r = this.buffer.getInt(pos[0], pos[1], 0);
        int g = this.buffer.getInt(pos[0], pos[1], 1);
        int b = this.buffer.getInt(pos[0], pos[1], 2);
        return RGB8.intCode(r, g, b);
    }
    
    @Override
    public void setIntCode(int[] pos, int intCode)
    {
        this.buffer.setInt(pos[0], pos[1], 0, intCode & 0x00FF);
        this.buffer.setInt(pos[0], pos[1], 1, (intCode >> 8) & 0x00FF);
        this.buffer.setInt(pos[0], pos[1], 2, (intCode >> 16) & 0x00FF);
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
    public RGB8 get(int x, int y)
    {
        int r = this.buffer.getInt(x, y, 0);
        int g = this.buffer.getInt(x, y, 1);
        int b = this.buffer.getInt(x, y, 2);
        return new RGB8(r, g, b);
    }

    @Override
    public void set(int x, int y, RGB8 rgb)
    {
        this.buffer.setInt(x, y, 0, rgb.getSample(0));
        this.buffer.setInt(x, y, 1, rgb.getSample(1));
        this.buffer.setInt(x, y, 2, rgb.getSample(2));
    }

    /* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public RGB8 get(int[] pos)
	{
	    return get(pos[0], pos[1]);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int[] pos, RGB8 rgb)
	{
        int x = pos[0];
        int y = pos[1];
        this.buffer.setInt(x, y, 0, rgb.getSample(0));
        this.buffer.setInt(x, y, 1, rgb.getSample(1));
        this.buffer.setInt(x, y, 2, rgb.getSample(2));
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array2D#duplicate()
	 */
	@Override
	public RGB8Array2D duplicate()
	{
		UInt8Array3D newBuffer = this.buffer.duplicate();
		return new BufferedPackedByteRGB8Array2D(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array2D#iterator()
	 */
	@Override
	public RGB8Array.Iterator iterator()
	{
		return new Iterator();
	}
	
	private class Iterator implements RGB8Array.Iterator
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
		public RGB8 next()
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
			case 0: buffer.setInt(posX, posY, 0, UInt8.convert(value));
			case 1: buffer.setInt(posX, posY, 1, UInt8.convert(value));
			case 2: buffer.setInt(posX, posY, 2, UInt8.convert(value));
			default: new IllegalArgumentException(
					"Channel index must be comprised between 0 and 2, not " + c);
			}
		}

		@Override
		public RGB8 get()
		{
			int r = buffer.getInt(posX, posY, 0);
			int g = buffer.getInt(posX, posY, 1);
			int b = buffer.getInt(posX, posY, 2);
			return new RGB8(r, g, b);
		}

		@Override
		public void set(RGB8 rgb)
		{
			buffer.setInt(posX, posY, 0, rgb.getSample(0));
			buffer.setInt(posX, posY, 1, rgb.getSample(0));
			buffer.setInt(posX, posY, 2, rgb.getSample(0));
		}
	}
}
