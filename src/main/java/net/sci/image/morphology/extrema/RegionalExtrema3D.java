/**
 * 
 */
package net.sci.image.morphology.extrema;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.sci.array.binary.Binary.TRUE;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.process.ScalarArrayOperator;
import net.sci.image.Connectivity3D;
import net.sci.image.ImageArrayOperator;
import net.sci.image.morphology.FloodFill;
import net.sci.image.morphology.MinimaAndMaxima;

/**
 * Interface for computing regional extrema (regional minima and regional maxima).
 * 
 * Example of use:
 * <pre>
{@code
 * ImageStack image = IJ.getImage().getStack();
 * RegionalExtrema3DAlgo algo = new RegionalExtrema3DFlooding(); 
 * algo.setConnectivity(6);
 * algo.setExtremaType(ExtremaType.MAXIMA);
 * ImageStack result = algo.applyTo(image);
 * ImagePlus resPlus = new ImagePlus("Regional Extrema", result); 
 * resPlus.show(); 
 * }</pre>
 * 
 * @author David Legland
 *
 */
public class RegionalExtrema3D extends AlgoStub
implements ImageArrayOperator, ScalarArrayOperator
{
	// ==============================================================
	// class variables
	
	/** 
	 * Choose between minima or maxima 
	 */
	MinimaAndMaxima.Type type = MinimaAndMaxima.Type.MINIMA;
	
	/** 
	 * The value of connectivity, usually either C6 or C26.  
	 */
	Connectivity3D connectivity = Connectivity3D.C6;
	
	
	// ==============================================================
	// Constructors
	
	/**
	 * Creates a new algorithm for computing regional extrema, that computes
	 * regional minima with connectivity 6.
	 */
	public RegionalExtrema3D() 
	{
	}
	
	/**
	 * Creates a new algorithm for computing regional extrema, by choosing type
	 * of minima and connectivity.
	 * 
	 * @param extremaType
	 *            the type of extrema (minima or maxima)
	 * @param connectivity
	 *            should be C6 or C26
	 */
	public RegionalExtrema3D(MinimaAndMaxima.Type extremaType, Connectivity3D connectivity)
	{
		this.type = extremaType;
		this.connectivity = connectivity;
	}
	

	// ==============================================================
	// getter and setters
	
	public MinimaAndMaxima.Type getExtremaType() 
	{
		return type;
	}

	public void setExtremaType(MinimaAndMaxima.Type extremaType)
	{
		this.type = extremaType;
	}

	public Connectivity3D getConnectivity() 
	{
		return this.connectivity;
	}
	
	public void setConnectivity(Connectivity3D conn) 
	{
		this.connectivity = conn;
	}

	
	// ==============================================================
    // Processing methods
    
    public void process(ScalarArray<?> source, BinaryArray target)
	{
        ScalarArray3D<?> source3d = ScalarArray3D.wrap(source);
        BinaryArray3D target3d = BinaryArray3D.wrap(target);
		
		if (this.connectivity == Connectivity3D.C6)
		{
			processScalar3dC6(source3d, target3d);
		}
		else if (this.connectivity == Connectivity3D.C26)
		{
			processScalar3dC26(source3d, target3d);
		}
		else
		{
			throw new RuntimeException("Unable to process connectivity: " + this.connectivity);
		}
	}

	/**
	 * Computes regional extrema in current input image, using
	 * flood-filling-like algorithm with 6 connectivity.
	 * 
	 * Computations are made with floating point values.
	 */
	private void processScalar3dC6(ScalarArray3D<?> source, BinaryArray3D target) 
	{
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int sizeZ = source.size(2);

		// initialize local data depending on extrema type
		int sign = 1;
		if (this.type == MinimaAndMaxima.Type.MAXIMA) 
		{
			sign = -1;
		}

		// initialize result array with true everywhere
		fireStatusChanged(this, "Initialize regional extrema");
		target.fill(TRUE);

		fireStatusChanged(this, "Compute regional extrema");
		
		// iterate on image voxels
		for (int z = 0; z < sizeZ; z++) 
		{
			fireProgressChanged(this, z, sizeZ);
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
                    // If current output voxel is set to false, then it was
                    // already processed and does not need update
					if (!target.getBoolean(x, y, z))
						continue;
					
					// current value
					double currentValue = source.getValue(x, y, z) * sign;
					
					// compute extremum value in 6-neighborhood
					double value = currentValue;
					if (x > 0) 
						value = min(value, source.getValue(x-1, y, z) * sign); 
					if (x < sizeX - 1) 
						value = min(value, source.getValue(x+1, y, z) * sign); 
					if (y > 0) 
						value = min(value, source.getValue(x, y-1, z) * sign); 
					if (y < sizeY - 1) 
						value = min(value, source.getValue(x, y+1, z) * sign);
					if (z > 0) 
						value = min(value, source.getValue(x, y, z-1) * sign); 
					if (z < sizeZ - 1) 
						value = min(value, source.getValue(x, y, z+1) * sign); 

					// if one of the neighbors has lower value, the local pixel 
					// is not a minima. All connected pixels with same value are 
					// set to the marker for non-minima.
					if (value < currentValue) 
					{
						FloodFill.floodFill(source, x, y, z, target, 0, Connectivity3D.C6);
					}
				}
			}
		}		

		fireProgressChanged(this, sizeZ, sizeZ);
	}

	/**
	 * Computes regional extrema in current input image, using
	 * flood-filling-like algorithm with 26 connectivity.
	 * 
	 * Computations are made with floating point values.
	 */
	private void processScalar3dC26(ScalarArray3D<?> source, BinaryArray3D target) 
	{
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int sizeZ = source.size(2);

		// initialize local data depending on extrema type
		int sign = 1;
		if (this.type == MinimaAndMaxima.Type.MAXIMA) 
		{
			sign = -1;
		}

		// initialize result array with true everywhere
		fireStatusChanged(this, "Initialize regional extrema");
		target.fill(TRUE);

		fireStatusChanged(this, "Compute regional extrema");
		
		// iterate on image voxels
		for (int z = 0; z < sizeZ; z++) 
		{
			fireProgressChanged(this, z, sizeZ);
			
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++)
				{
                    // If current output voxel is set to false, then it was
                    // already processed and does not need update
					if (!target.getBoolean(x, y, z))
						continue;
					
					// current value
					double currentValue = source.getValue(x, y, z) * sign;
					
					// compute extremum value in 26-neighborhood
					double value = currentValue;
					for (int z2 = max(z-1, 0); z2 <= min(z+1, sizeZ-1); z2++) 
					{
						for (int y2 = max(y-1, 0); y2 <= min(y+1, sizeY-1); y2++) 
						{
							for (int x2 = max(x-1, 0); x2 <= min(x+1, sizeX-1); x2++) 
							{
								value = min(value, source.getValue(x2, y2, z2) * sign);
							}
						}
					}
					
					// if one of the neighbors has lower value, the local voxel
					// is not a minima. All connected pixels with same value are
					// set to the marker for non-minima.
					if (value < currentValue) 
					{
						FloodFill.floodFill(source, x, y, z, target, 0, Connectivity3D.C26);
					}
				}
			}
		}
		
		fireProgressChanged(this, sizeZ, sizeZ);
	}
	
    // ==============================================================
    // Implementation of ScalarArrayOperator interface

    @Override
    public BinaryArray3D processScalar(ScalarArray<?> input)
    {
        BinaryArray3D output = BinaryArray3D.create(input.size(0),input.size(1), input.size(2));
        process(input, output);
        return output;
    }
    
    // ==============================================================
    // Implementation of ArrayOperator interface

    @Override
    public <T> BinaryArray3D process(Array<T> array)
    {
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        
        return processScalar((ScalarArray<?>) array);
    }
}
