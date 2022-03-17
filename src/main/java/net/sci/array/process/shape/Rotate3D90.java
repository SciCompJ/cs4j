/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.ArrayOperator;

/**
 * Rotate a 3D array by 90 degrees about of the three main axes.
 * 
 * @author dlegland
 *
 */
public class Rotate3D90 extends AlgoStub implements ArrayOperator
{
    // =============================================================
    // Class members

    /**
     * The axis to perform the rotation about. Can be 0, 1 or 2. Default is 2
     * (for rotation around Z-axis).
     */
    int axis = 2;
    
    /**
     * The number of elementary rotations by 90 degrees about the axis. Default
     * is 1.
     */
    int number = 1;
	
    
    // =============================================================
    // Constructors

	/**
	* Rotates array by a single 90 degrees rotation.
	*/
	public Rotate3D90()
	{
	}

	/**
	 * Rotates array by several 90 degrees rotations. Using negative number
	 * rotates in clockwise order.
	 * 
	 * @param number the number of rotations to apply
	 */
	public Rotate3D90(int axis, int number)
	{
	    this.axis = axis;
		this.number = clampRotationNumber(number);
	}

	private int clampRotationNumber(int number)
    {
        // ensure rotation number is between 0 and 3 using modulo
        number %= 4;
        
        // take care of negative values
        while (number < 0)
            number += 4;
        
        return number;
    }


	// =============================================================
    // Methods

    /* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
	 */
	@Override
	public <T> Array<?> process(Array<T> array)
	{
		if (array.dimensionality() == 3)
		{
			return process3d(Array3D.wrap(array));		
        }
        
		throw new IllegalArgumentException("Requires a 3D array");
	}

	/**
     * Rotates the planar array by 90 degrees in clock-wise order.
     * 
     * @param <T>
     *            the type of the array to process
     * @param array
     *            the array to rotate
     * @return the rotated array
     */
    public <T> Array3D<?> process3d(Array3D<T> array)
    {
        switch (this.axis)
        {
        case 0:
            return rotate3dX(array);
        case 1:
            return rotate3dY(array);
        case 2:
            return rotate3dZ(array);
        default:
            throw new IllegalArgumentException("Illegal axis number: " + this.axis);
        }
    }

    private <T> Array3D<?> rotate3dX(Array3D<T> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        switch (this.number)
        {
        case 0:
        {
            // no rotation -> simply duplicate the array
            return array.duplicate();
        }
        case 1:
        {
            // one rotation -> swap Y and Z coords and reverse Y
            this.fireStatusChanged(this, "Create result array");
            Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeX, sizeZ, sizeY});
            
            // iterate over slices
            this.fireStatusChanged(this, "Processing values");
            for(int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                int y2 = sizeZ - 1 - z;
                for(int y = 0; y < sizeY; y++)
                {
                    int z2 = y;
                    for(int x = 0; x < sizeX; x++)
                    {
                        int x2 = x;
                        output.set(x2, y2, z2, array.get(x, y, z));
                    }
                }
            }
            return output;
        }
        case 2:
        {
            // two rotations -> reverse Y and Z coordinates
            this.fireStatusChanged(this, "Create result array");
            Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeX, sizeY, sizeZ});
            
            // iterate over slices
            this.fireStatusChanged(this, "Processing values");
            for(int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                int z2 = sizeZ - 1 - z;
                for(int y = 0; y < sizeY; y++)
                {
                    int y2 = sizeY - 1 - y;
                    for(int x = 0; x < sizeX; x++)
                    {
                        int x2 = x;
                        output.set(x2, y2, z2, array.get(x, y, z));
                    }
                }
            }
            return output;
        }
        case 3:
        {
            // three rotations -> swap Y and Z coords and reverse Z
            this.fireStatusChanged(this, "Create result array");
            Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeX, sizeZ, sizeY});
            
            // iterate over slices
            this.fireStatusChanged(this, "Processing values");
            for(int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                int y2 = z;
                for(int y = 0; y < sizeY; y++)
                {
                    int z2 = sizeY - 1 - y;
                    for(int x = 0; x < sizeX; x++)
                    {
                        int x2 = x;
                        output.set(x2, y2, z2, array.get(x, y, z));
                    }
                }
            }
            return output;
        }
        default:
            throw new RuntimeException("Problem in choosing rotation number");
        }       
    }
    
    private <T> Array3D<?> rotate3dY(Array3D<T> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        switch (this.number)
        {
        case 0:
        {
            // no rotation -> simply duplicate the array
            return array.duplicate();
        }
        case 1:
        {
            // one rotation -> swap X and Z and reverse Z
            this.fireStatusChanged(this, "Create result array");
            Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeZ, sizeY, sizeX});
            
            // iterate over slices
            this.fireStatusChanged(this, "Processing values");
            for(int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                int x2 = z;
                for(int y = 0; y < sizeY; y++)
                {
                    int y2 = y;
                    for(int x = 0; x < sizeX; x++)
                    {
                        int z2 = sizeX - 1 - x;
                        output.set(x2, y2, z2, array.get(x, y, z));
                    }
                }
            }
            return output;
        }
        case 2:
        {
            // two rotations -> reverse X and Z coordinates
            this.fireStatusChanged(this, "Create result array");
            Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeX, sizeY, sizeZ});
            
            // iterate over slices
            this.fireStatusChanged(this, "Processing values");
            for(int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                int z2 = sizeZ - 1 - z;
                for(int y = 0; y < sizeY; y++)
                {
                    int y2 = y;
                    for(int x = 0; x < sizeX; x++)
                    {
                        int x2 = sizeX - 1 - x;
                        output.set(x2, y2, z2, array.get(x, y, z));
                    }
                }
            }
            return output;
        }
        case 3:
        {
            // three rotations -> swap X and Z coords and reverse X
            this.fireStatusChanged(this, "Create result array");
            Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeZ, sizeY, sizeX});
            
            // iterate over slices
            this.fireStatusChanged(this, "Processing values");
            for(int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                int x2 = sizeZ - 1 - z;
                for(int y = 0; y < sizeY; y++)
                {
                    int y2 = y;
                    for(int x = 0; x < sizeX; x++)
                    {
                        int z2 = x;
                        output.set(x2, y2, z2, array.get(x, y, z));
                    }
                }
            }
            return output;
        }
        default:
            throw new RuntimeException("Problem in choosing rotation number");
        }       
    }

    /**
     * Rotates the 3D array around the Z axis.
     * 
     * @param <T>
     *            the type of the array to process
     * @param array
     *            the array to rotate
     * @return the rotated array
     */
	private <T> Array3D<?> rotate3dZ(Array3D<T> array)
	{
		int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
		switch (this.number)
		{
		case 0:
		{
			// no rotation -> simply duplicate the array
			return array.duplicate();
		}
		case 1:
		{
			// one rotation -> swap X and Y coords, and reverse one
		    Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeY, sizeX, sizeZ});
		    for(int z = 0; z < sizeZ; z++)
		    {
                this.fireProgressChanged(this, z, sizeZ);
		        int z2 = z;
		        for(int y = 0; y < sizeY; y++)
		        {
		            int x2 = sizeY - 1 - y;
		            for(int x = 0; x < sizeX; x++)
		            {
		                int y2 = x;
		                output.set(x2, y2, z2, array.get(x, y, z));
		            }
		        }
		    }
		    return output;
		}
		case 2:
		{
		    // two rotations -> reverse X and Y coordinates
		    Array3D<T> output = array.duplicate();
		    for(int z = 0; z < sizeZ; z++)
		    {
                this.fireProgressChanged(this, z, sizeZ);
		        int z2 = z;
		        for(int y = 0; y < sizeY; y++)
		        {
		            int y2 = sizeY - 1 - y;
		            for(int x = 0; x < sizeX; x++)
		            {
		                int x2 = sizeX - 1 - x;
		                output.set(x2, y2, z2, array.get(x, y, z));
		            }
		        }
		    }
		    return output;
		}
		case 3:
		{
            // three rotation -> swap X and Y coords, and reverse one
		    Array3D<T> output = (Array3D<T>) array.newInstance(new int[]{sizeY, sizeX, sizeZ});
            for(int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                int z2 = z;
                for(int y = 0; y < sizeY; y++)
                {
                    int x2 = y;
                    for(int x = 0; x < sizeX; x++)
                    {
                        int y2 = sizeX - 1 - x;
                        output.set(x2, y2, z2, array.get(x, y, z));
                    }
                }
            }
            return output;
		}
		default:
			throw new RuntimeException("Problem in choosing rotation number");
		}		
	}
	
	@Override
	public boolean canProcess(Array<?> array)
	{
		return array.dimensionality() == 3;
	}
}
