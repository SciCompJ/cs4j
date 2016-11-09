package net.sci.image.data;

/**
 * Identifies the position of a voxel in a 3D image by using 3 integer
 * coordinates.
 *
 */
public class Cursor3D implements Cursor
{
	private int x = 0;
	private int y = 0;
	private int z = 0;
	
	public Cursor3D(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getZ()
	{
		return z;
	}

	@Override
	public int dimensionality()
	{
		return 3;
	}

	@Override
	public int[] getPosition()
	{
		return new int[]{x, y, z};
	}

	@Override
	public int get(int dim)
	{
		switch(dim)
		{
		case 0: return x;
		case 1: return y;
		case 2: return z;
		}
		throw new IllegalArgumentException("Dimension must be comprised between 0 and 2");
	}	
	
	@Override
	public boolean equals( Object other )
	{
	    if (other == null) return false;
	    if (other == this) return true;
	    if ( !( other instanceof Cursor3D ) )
	    	return false;
	    
	    Cursor3D c = (Cursor3D) other;
	    return c.x == this.x && c.y == this.y && c.z == this.z;
	}
}
