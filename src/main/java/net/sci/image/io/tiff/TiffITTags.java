/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dlegland
 *
 */
public class TiffITTags implements TagSet
{
    
    /* (non-Javadoc)
     * @see net.sci.image.io.tiff.TagSet#getTags()
     */
    @Override
    public Map<Integer, TiffTag> getTags()
    {
        Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(17);
        
        add(tags, new TiffTag(34016, "Site"));
        add(tags, new TiffTag(34017, "ColorSequence"));
        add(tags, new TiffTag(34018, "IT8Header"));
        add(tags, new TiffTag(34019, "RasterPadding"));
        add(tags, new TiffTag(34020, "BitsPerRunLength"));
        
        add(tags, new TiffTag(34021, "BitsPerExtendedRunLength"));
        add(tags, new TiffTag(34022, "ColorTable"));
        add(tags, new TiffTag(34023, "ImageColorIndicator"));
        add(tags, new TiffTag(34024, "BackgroundColorIndicator"));
        
        add(tags, new TiffTag(34025, "ImageColorValue"));
        add(tags, new TiffTag(34026, "BackgroundColorValue"));
        add(tags, new TiffTag(34027, "PixelIntensityRange"));
        add(tags, new TiffTag(34028, "TransparencyIndicator"));
        
        add(tags, new TiffTag(34029, "ColorCharacterization"));
        add(tags, new TiffTag(34030, "HCUsage"));
        add(tags, new TiffTag(34031, "TrapIndicator"));
        add(tags, new TiffTag(34032, "CMYKEquivalent"));
                
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
        return "IT";
    }
}
