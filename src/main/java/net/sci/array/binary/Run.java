/**
 * 
 */
package net.sci.array.binary;

/**
 * A one-dimensional interval of integers, defined by its left and right bounds.
 * 
 * @author dlegland
 *
 */
public class Run implements Comparable<Run>
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
     * Creates a new {@code Run}, defined by its to inclusive bounds. The right
     * bound must be larger than (or equal to) the left bound. In case of
     * equality, the {@code Run} is composed of a single element.
     * 
     * @param left
     *            the left bound of the {@code Run} (inclusive)
     * @param right
     *            the right bound of the {@code Run} (inclusive)
     */
    public Run(int left, int right)
    {
        if (right < left)
        {
            throw new RuntimeException("Right extremity must be larger than left extremity");
        }
        this.left = left;
        this.right = right;
    }
    
    /**
     * Returns the length of this run.
     * 
     * @return then number of elements within this run.
     */
    public int length()
    {
        return right - left + 1;
    }
    
    /**
     * Checks if this {@code Run} contains the element at the specified position.
     * 
     * @param pos
     *            the index of the element (0-based).
     * @return true if this runlength contains the specified position.
     */
    public boolean contains(int pos)
    {
        return pos >= left && pos <= right;
    }
    
    /**
     * Duplicates this run.
     * @return a copy of this run.
     */
    public Run duplicate()
    {
        return new Run(this.left, this.right);
    }

    /**
     * Compares solely on the start position value.
     */
    @Override
    public int compareTo(Run that)
    {
        return this.left - that.left;
    }
    
    @Override
    public String toString()
    {
        return String.format("Run(%d, %d)", this.left, this.right);
    }
}
