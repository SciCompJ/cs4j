/**
 * 
 */
package net.sci.array.numeric.impl;

/**
 * A one-dimensional interval of integers, defined by its left and right bounds.
 * 
 * @see net.sci.array.binary.Run
 * @author dlegland
 */
public class Int32Run implements Comparable<Int32Run>
{
    /**
     * The left position of this run.
     */
    public final int left;

    /**
     * The right position of this run.
     */
    public final int right;
    
    /**
     * The value stored within this run, as an int.
     */
    public final int value;
    
    public Int32Run(int left, int right, int value)
    {
        if (right < left)
        {
            throw new RuntimeException("Right extremity must be larger than left extremity");
        }
        this.left = left;
        this.right = right;
        this.value = value;
    }
    
    public int length()
    {
        return right - left + 1;
    }
    
    public int value()
    {
        return value;
    }
    
    /**
     * Checks if this RunLength contains the element at the specified position.
     * 
     * @param pos
     *            the index of the element (0-based).
     * @return true if this runlength contains the specified position.
     */
    public boolean contains(int pos)
    {
        return pos >= left && pos <= right;
    }
    
    public Int32Run duplicate()
    {
        return new Int32Run(this.left, this.right, this.value);
    }

    /**
     * Compares solely on the start position value.
     */
    @Override
    public int compareTo(Int32Run that)
    {
        return this.left - that.left;
    }
    
    @Override
    public String toString()
    {
        return String.format("Int32Run(%d, %d, %d)", this.left, this.right, this.value);
    }
}
