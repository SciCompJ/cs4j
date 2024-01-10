package net.sci.image.data;

/**
 * Identifies the position of a pixel in a 2D image by using two integer
 * coordinates.
 *
 */
@Deprecated
public class Cursor2D implements Cursor
{
	private int x = 0;
	private int y = 0;

	public Cursor2D(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public int dimensionality()
	{
		return 2;
	}

	@Override
	public int[] getPosition()
	{
		return new int[]{x, y};
	}

	@Override
	public int get(int dim)
	{
		switch(dim)
		{
		case 0: return x;
		case 1: return y;
		}
		throw new IllegalArgumentException("Dimension must be comprised between 0 and 1");
	}	
	
	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Cursor2D))
			return false;
		
		Cursor2D c = (Cursor2D) other;
		return c.x == this.x && c.y == this.y;
	}
}
