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
	 * 
	 */
    protected CategoricalAxis(String name)
    {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }


}
