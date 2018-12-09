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
     * @return the type of this axis
     */
    public Type getType();
    
    /**
     * Duplicates this axis.
     * 
     * @return a duplicated axis
     */
    public Axis duplicate();
}
