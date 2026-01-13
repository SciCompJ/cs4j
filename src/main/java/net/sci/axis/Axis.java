/**
 * 
 */
package net.sci.axis;

/**
 * Meta-data associated to an individual axis: calibration, unit name...
 * 
 * @author dlegland
 *
 */
public interface Axis
{
    // =============================================================
    // Method declarations

    /**
     * @return the name
     */
    public String getName();
    
    /**
     * Changes the name of the axis (optional operation).
     * 
     * @param name the name to set
     */
    public void setName(String name);

    /**
     * @return a short name (typically one-digit length) used for building
     *         derived names
     */
    public default String getShortName()
    {
        return getName().substring(0, 1);
    }
    
    /**
     * Returns the name a specific item within the axis.
     * 
     * @param index
     *            the index of the item on the axis (starting from 0)
     * @return the name of the item for the given index
     */
    public String itemName(int index);

    /**
     * Returns a new axis based on a selection of this axis elements' specified
     * by an array of indices.
     * 
     * @param indices
     *            the array of indices of the elements to keep. Should not
     *            contain duplicate.
     * @return a new Axis with same type as this axis and containing the
     *         selection of elements
     */
    public Axis selectElements(int[] indices);
    
    /**
     * Duplicates this axis.
     * 
     * @return a duplicated axis
     */
    public Axis duplicate();
}
