/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.Map;

/**
 * Provides a list of Tiff Tags corresponding to a specific context or
 * application.
 * 
 * New user-defined tag sets can be added to the application via TagSetManager
 * class.
 *
 * @see net.sci.image.io.tiff.BaselineTags
 * @see net.sci.image.io.tiff.ExtensionTags
 * @see net.sci.image.io.tiff.TagSetManager
 * 
 * @author dlegland
 */
public interface TagSet
{
    /**
     * @return the collection of Tiff tags within this set, indexed by the tag codes.
     */
	public Map<Integer, TiffTag> getTags();
	
	/**
	 * @return a name to identify this tag set.
	 */
	public String getName();
	
}
