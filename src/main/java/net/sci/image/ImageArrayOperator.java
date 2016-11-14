/**
 * 
 */
package net.sci.image;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;

/**
 * An image operator that operates on image data array.
 * 
 * As methods provide default implementation, it suffices to add the
 * 'implements' tag to an array operator to make it applicable to image as well.
 * 
 * @author dlegland
 *
 */
public interface ImageArrayOperator extends ArrayOperator, ImageOperator
{
	@Override
	public default Image process(Image image)
	{
		Array<?> array = image.getData();
		Array<?> result = process(array);
		return new Image(result, image);
	}
}
