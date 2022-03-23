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
    public static final class ImageJMetaDataCounts extends TiffTag
    {
        public static final int CODE = 50838;
        public ImageJMetaDataCounts()
        {
            super(CODE, "ImageJMetaDataCounts", "ImageJ Metadata counts");
        }
    }

    /**
     * 50839 - ImageJ Metadata.
     */
    public static final class ImageJMetaData extends TiffTag
    {
        public static final int CODE = 50839;
        public ImageJMetaData()
        {
            super(CODE, "ImageJMetaData", "ImageJ Metadata");
        }
    }


    /* (non-Javadoc)
	 * @see net.sci.image.io.TiffTagSet#getTags()
	 */
	@Override
	public Map<Integer, TiffTag> getTags()
	{
	    Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(2);
	    add(tags, new ImageJMetaDataCounts());
	    add(tags, new ImageJMetaData());
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
