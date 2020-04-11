/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.HashMap;
import java.util.Map;

/**
 * The set of tags used by ImageJ to save overlays.
 * 
 * @author dlegland
 *
 */
public class ImageJTags implements TagSet
{
    /**
     * 50838 - ImageJ Metadata counts.
     */
    public static final int IMAGEJ_METADATA_COUNTS = 50838;

    /**
     * 50839 - ImageJ Metadata.
     */
    public static final int IMAGEJ_METADATA = 50839;


    /* (non-Javadoc)
	 * @see net.sci.image.io.TiffTagSet#getTags()
	 */
	@Override
	public Map<Integer, TiffTag> getTags()
	{
	    Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(2);
	    add(tags, new TiffTag(IMAGEJ_METADATA_COUNTS, "ImageJMetaDataCounts"));
	    add(tags, new TiffTag(IMAGEJ_METADATA, "ImageJMetaData"));
		return tags;
	}

    /**
     * Adds a tag into a map by indexing it with its key.
     * 
     * @param map
     *            the map to populate.
     * @param tag
     *            the tag to add.
     */
    private void add(Map<Integer, TiffTag> map, TiffTag tag)
    {
        tag.tagSet = this;
        map.put(tag.code, tag);
    }

    @Override
    public String getName()
    {
        return "ImageJ";
    }

}
