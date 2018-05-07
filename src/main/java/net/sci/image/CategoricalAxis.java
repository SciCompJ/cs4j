/**
 * 
 */
package net.sci.image;

/**
 * Categorical image axis, for example for channel axes (if not implemented as
 * vector arrays).
 * 
 * @author dlegland
 *
 */
public class CategoricalAxis implements ImageAxis
{
    /**
     * The name for this axis.
     */
    String name;

    /**
     * Creates a new axis by specifying its name.
     * 
	 * @param name the name of the axis 
	 */
    protected CategoricalAxis(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the axis.
     * 
     * @return the name of the axis
     */
    public String getName()
    {
        return name;
    }

    /**
     * Changes the name of the axis
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }


}
