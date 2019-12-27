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
    // Enumerations

    enum Type 
    {
        SPACE, 
        CHANNEL, 
        TIME, 
        WAVELENGTH,
        UNKNOWN
    };
    
    
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
     * @return the type of this axis
     */
    public Type getType();
    
    /**
     * @return a short name (typically one-digit length) used for building
     *         derived names
     */
    public default String getShortName()
    {
        return getName().substring(0, 1);
    }
    
    /**
     * Duplicates this axis.
     * 
     * @return a duplicated axis
     */
    public Axis duplicate();
}
