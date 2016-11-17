/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.data.scalar3d.Float32Array3D;
import net.sci.array.data.vector.Float64VectorArray2D;
import net.sci.array.data.vector.VectorArray2D;
import net.sci.array.type.Scalar;
import net.sci.array.type.Vector;

/**
 * Arrays containing vector of floating-point values.
 *
 * @author dlegland
 *
 */
public interface VectorArray<V extends Vector<?>> extends Array<V>
{
	// =============================================================
	// Static methods
	
	/**
	 * Computes the norm of each element of the given vector array.
	 * 
	 * Current implementation returns the result in a new instance of
	 * FloatArray.
	 * 
	 * @param array
	 *            a vector array
	 * @return a scalar array with the same size at the input array
	 */
	public static ScalarArray<?> norm(VectorArray<? extends Vector<?>> array)
	{
		// allocate memory for result
		Float32Array result = Float32Array.create(array.getSize());
		
		// create array iterators
		Iterator<? extends Vector<?>> iter1 = array.iterator();
		Float32Array.Iterator iter2 = result.iterator();
		
		// iterate over both arrays in parallel
		while (iter1.hasNext() && iter2.hasNext())
		{
			// get current vector
			double[] values = iter1.next().getValues();
			
			// compute norm of current vector
			double norm = 0;
			for (double d : values)
			{
				norm += d * d;
			}
			norm = Math.sqrt(norm);
			
			// allocate result
			iter2.forward();
			iter2.setValue(norm);
		}
		
		return result;
	}
	
	/**
	 * Creates a new instance of VectorArray from a scalar array with three dimensions.
	 * 
	 * @param array
	 *            an instance of scalar array
	 * @return a new instance of vector array, with the one dimension less than
	 *         original array
	 */
	public static VectorArray<?> fromStack(ScalarArray<?> array)
	{
		if (!(array instanceof ScalarArray))
		{
			throw new IllegalArgumentException("Requires a scalar array");
		}
		
		if (array.dimensionality() != 3)
		{
			// TODO: manage more than three dimensions
			throw new IllegalArgumentException("Requires a scalar array with three dimensions");
		}
		
		// size and dimension of input array
		int[] dims = array.getSize();
		int nd = dims.length;
		
		// compute size and dimension of output array
		int[] newDims = new int[nd - 1];
		for (int d= 0; d < nd - 1; d++)
		{
			newDims[d] = dims[d];
		}
		
		// create output array
		VectorArray2D<? extends Vector<?>> result = Float64VectorArray2D.create(newDims[0], newDims[1], dims[2]);
		int[] pos = new int[3];
		for (int c = 0; c < dims[2]; c++)
		{
			pos[2] = c;
			for (int y = 0; y < dims[1]; y++)
			{
				pos[1] = y;
				for (int x = 0; x < dims[0]; x++)
				{
					pos[0] = x;
					result.setValue(x, y, c, array.getValue(pos));
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Converts a vector array to a higher-dimensional array, by considering the
	 * channels as a new dimension.
	 * 
	 * Current implementation returns the result in a new instance of
	 * FloatArray.
	 *
	 * @param array
	 *            a vector array with N dimensions
	 * @return a scalar array with N+1 dimensions
	 */
	public static ScalarArray<?> convertToStack(VectorArray<?> array)
	{
		// TODO: manage more than two dimensions
		if (array.dimensionality() != 2)
		{
			throw new IllegalArgumentException("Requires a vector array with two dimensions");
		}
		
		// size and dimension of input array
		int[] dims = array.getSize();
		int nd = dims.length;
		int nChannels = array.getVectorLength();
		
		// compute size and dimension of output array
		int[] newDims = new int[nd + 1];
		for (int d= 0; d < nd; d++)
		{
			newDims[d] = dims[d];
		}
		newDims[nd] = nChannels;
		
		// create output array
		Float32Array3D result = Float32Array3D.create(newDims[0], newDims[1], newDims[2]);
		int[] pos = new int[2];
		for (int c = 0; c < nChannels; c++)
		{
			for (int y = 0; y < dims[1]; y++)
			{
				pos[1] = y;
				for (int x = 0; x < dims[0]; x++)
				{
					pos[0] = x;
					result.setValue(x, y, c, array.get(pos).getValue(c));
				}
			}
		}
		
		return result;
	}
	
	
	// =============================================================
	// New methods
	
	public int getVectorLength();

	/**
	 * Returns the set of values corresponding to the array element for the
	 * given position.
	 * 
	 * @param pos
	 *            list of indices in each dimension
	 * @return the set of values corresponding to the array element for the
	 *         given position
	 */
	public double[] getValues(int[] pos);
	
	/**
	 * Sets of values corresponding to the array element for the given position.
	 * 
	 * @param pos
	 *            list of indices in each dimension
	 * @param values
	 *            the new set of values to assign to the array
	 */
	public void setValues(int[] pos, double[] values);
	
	
	// =============================================================
	// Specialization of Array interface

	@Override
	public VectorArray<V> newInstance(int... dims);
	
	@Override
	public VectorArray<V> duplicate();

	public VectorArray.Iterator<V> iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator<V extends Vector<? extends Scalar>> extends Array.Iterator<V>
	{
		public default double nextValue()
		{
			forward();
			return getValue();
		}
	}

}
