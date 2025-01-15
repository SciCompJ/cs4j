package net.sci.image.morphology.strel;

/**
 * Computes the maximum floating point value in a local buffer. Used by several
 * in-place strel implementations.
 *
 * This implementation considers a circular buffer: when a value is added, it
 * replaces the first value that was inserted. This makes it possible to update
 * extrema only when necessary needed.
 * 
 * Works for floating point values, using double primitive type.
 * 
 * @see LocalExtremumBufferInt
 * @see LinearHorizontalStrel
 * @see VerticalHorizontalStrel
 */
public class LocalExtremumBufferDouble implements LocalExtremum 
{
	/**
	 * Current max value
	 */
	double maxValue = Double.NEGATIVE_INFINITY;

	boolean updateNeeded = false;
	
	/**
	 * Uses a sign flag for managing both min and max.
	 * 
	 * sign = +1 -> computes maximum values
	 * sign = -1 -> computes minimum values
	 */
	int sign;
	
	/**
	 * Circular buffer of stored values
	 */
	double[] buffer;
	
	/**
	 * Current index in circular buffer
	 */
	int bufferIndex = 0;
	
	/**
	 * Main constructor.
	 *
	 * @param n
	 *            the size of the buffer
	 */
	public LocalExtremumBufferDouble(int n) 
	{
        initializeBuffer(n, Double.NEGATIVE_INFINITY);
	}
	
	/**
	 * Constructor from size and type of extremum (minimum or maximum).
	 *
	 * @param n
	 *            the size of the buffer
	 * @param type
	 *            the type of extremum (maximum or minimum)
	 */
	public LocalExtremumBufferDouble(int n, LocalExtremum.Type type)
	{
		this(n);
		switch (type)
		{
		case MINIMUM: setMinMaxSign(-1); break;
		case MAXIMUM: setMinMaxSign(+1); break;
		}
	}
	
	/**
	 * Initializes an histogram filled with the given value.
	 *
	 * @param n
	 *            the size of the buffer
	 * @param value
	 *            the initial value of all elements in buffer
	 */
	public LocalExtremumBufferDouble(int n, double value) 
	{
	    initializeBuffer(n, value);
		this.maxValue = value;
	}
	
	private void initializeBuffer(int n, double value)
	{
        this.buffer = new double[n];
        for (int i = 0; i < n; i++)
            this.buffer[i] = value;
        this.maxValue = value;
	}

	/**
	 * Changes the sign used for distinguishing minimum and maximum.
	 * 
	 * @param sign
	 *            +1 for maximum, -1 for minimum
	 */
	public void setMinMaxSign(int sign) 
	{
		this.sign = sign;
	}
	
	/**
	 * Adds a value to the local histogram, and update bounds if needed. 
	 * Then removes the last stored value, and update bounds if needed.
	 * 
	 * @param value the value to add
	 */
	public void add(double value) 
	{
		// add the new value, and remove the oldest one
		notifyAddValue(value);
		notifyRemoveValue(this.buffer[this.bufferIndex]);
		
		// update local circular buffer
		this.buffer[this.bufferIndex] = value;
		this.bufferIndex = (++this.bufferIndex) % this.buffer.length;
	}
	
	/**
	 * Updates local extremum with the specified value.
	 * 
	 * @param value
	 *            the value to add
	 */
	private void notifyAddValue(double value) 
	{
		// update max value
		if (value * sign > this.maxValue * sign) 
		{
			this.maxValue = value;
			updateNeeded = false;
		}
	}
	
	/**
	 * Updates local extremum with the specified value.
	 * 
	 * @param value
	 *            the value to remove
	 */
	private void notifyRemoveValue(double value) 
	{
		// update max value if needed
		if (value == this.maxValue) 
		{
			updateNeeded = true;
		}
	}
	
	/**
	 * Reset inner counts with infinite values.
	 */
	public void clear() 
	{
		if (this.sign == 1)
			this.fill(Double.NEGATIVE_INFINITY);
		else
			this.fill(Double.POSITIVE_INFINITY);
	}
	
	/**
	 * Resets histogram by considering it is filled with the given value. 
	 * Update max value accordingly.
	 * 
	 * @param value
	 *            the new value of all elements in buffer
	 */
	public void fill(double value) 
	{
		// get buffer size
		int n = this.buffer.length;

		// Clear the circular buffer
		for (int i = 0; i < n; i++)
			buffer[i] = value;

		// update max and max values
		this.maxValue = value;
	}

	/**
	 * Returns the maximum value stored in this local buffer
	 * @return the maximum value in buffer
	 */
	public double getMax() 
	{
		if (updateNeeded) 
		{
			recomputeMaxValue();
		}
		
		return this.maxValue;
	}

    private void recomputeMaxValue() 
    {
    	if (sign == 1)
    	{
    		// find the maximum value in the buffer
    		this.maxValue = Double.NEGATIVE_INFINITY;
    		for (int i = 0; i < buffer.length; i++) 
    		{
    			this.maxValue = Math.max(this.maxValue, this.buffer[i]);
    		}
    	}
    	else
    	{
    		// find the maximum value in the buffer
    		this.maxValue = Double.POSITIVE_INFINITY;
    		for (int i = 0; i < buffer.length; i++) 
    		{
    			this.maxValue = Math.min(this.maxValue, this.buffer[i]);
    		}
    	}
    	
    	updateNeeded = false;
    }
}
