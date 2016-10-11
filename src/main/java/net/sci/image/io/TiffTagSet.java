/**
 * 
 */
package net.sci.image.io;

import java.util.List;

/**
 * Provides a list of Tiff Tags corresponding to a specific context or
 * application.
 * 
 * @author dlegland
 *
 */
public interface TiffTagSet
{
	public List<TiffTag> getTags();
}
