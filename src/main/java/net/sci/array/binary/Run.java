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
    
    public Run(int left, int right)
    {
        if (right < left)
        {
            throw new RuntimeException("Right extremity must be larger than left extremity");
        }
        this.left = left;
        this.right = right;
    }
    
    public int length()
    {
        return right - left + 1;
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
