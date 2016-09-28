/**
 * 
 */
package net.sci.image.io;

import java.util.ArrayList;
import java.util.List;

/**
 * The set of tags used by ImageJ to save oerlays.
 * 
 * @author dlegland
 *
 */
public class ImageJTiffTags implements TiffTagSet
{

	/**
	 * 
	 */
	public ImageJTiffTags()
	{
	}

	/* (non-Javadoc)
	 * @see net.sci.image.io.TiffTagSet#getTags()
	 */
	@Override
	public List<TiffTag> getTags()
	{
		ArrayList<TiffTag> tags = new ArrayList<TiffTag>(2);
		tags.add(new TiffTag(50838, "ImageJ_ROI"));
		tags.add(new TiffTag(50839, "ImageJ_ROI"));
		return tags;
	}

}
