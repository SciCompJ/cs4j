/**
 * 
 */
package net.sci.array.scalar;

/**
 * Combines a scalar array and a threshold value and views the result as a
 * binary array.
 * 
 * @see ScalarArrayUInt8View
 * @deprecated use ThresholdedArray instead
 * @author dlegland
 */
@Deprecated
public class ScalarArrayThresholdView implements BinaryArray
{
	// =============================================================
	// Class members

    /** The array to threshold */
	ScalarArray<?> array;
	
    /** The threshold value */
	double value;
	

	// =============================================================
	// Constructors

	/**
     * Creates a threshold view from a scalar array and the threshold value.
     * 
     * @param array
     *            the array to threshold
     * @param value
     *            the threshold value
     */
	public ScalarArrayThresholdView(ScalarArray<?> array, double value)
	{
		this.array = array;
		this.value = value;
	}


	// =============================================================
	// Overloaded methods

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray#getBoolean(int[])
	 */
	@Override
	public boolean getBoolean(int... pos)
	{
		return this.array.getValue(pos) > this.value;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray#setBoolean(int[], boolean)
	 */
	@Override
	public void setBoolean(int[] pos, boolean state)
	{
		throw new RuntimeException("Unauthorized operation exception");
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#get(int[])
	 */
	@Override
	public Binary get(int... pos)
	{
		return new Binary(this.array.getValue(pos) > this.value);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#set(int[], java.lang.Object)
	 */
	@Override
	public void set(int[] pos, Binary value)
	{
		throw new RuntimeException("Unauthorized operation exception");
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] size()
	{
		return array.size();
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize(int)
	 */
	@Override
	public int size(int dim)
	{
		return array.size(dim);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#positionIterator()
	 */
	@Override
	public net.sci.array.Array.PositionIterator positionIterator()
	{
		return array.positionIterator();
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#dimensionality()
	 */
	@Override
	public int dimensionality()
	{
		return array.dimensionality();
	}

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray#iterator()
	 */
	@Override
	public Iterator iterator()
	{
		return new BinaryArray.Iterator()
		{
			ScalarArray.Iterator<?> iter = array.iterator();
			
			@Override
			public boolean hasNext()
			{
				return iter.hasNext();
			}
						
			@Override
			public void forward()
			{
				iter.forward();
			}
			
			@Override
			public void setBoolean(boolean b)
			{
				throw new RuntimeException("Unauthorized operation exception");
			}
			
			@Override
			public boolean getBoolean()
			{
				return iter.getValue() > value;
			}
		};
	}

}
