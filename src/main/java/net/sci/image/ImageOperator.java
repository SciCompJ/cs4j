/**
 * 
 */
package net.sci.image;

/**
 * @author dlegland
 *
 */
public interface ImageOperator
{
	public Image process(Image image);
	
	public default boolean canProcess(Image image)
	{
		return true;
	}
}
