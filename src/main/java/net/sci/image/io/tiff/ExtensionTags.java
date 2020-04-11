/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.HashMap;
import java.util.Map;

/**
 * An incomplete list of extension tags.
 * 
 * @see <a href="https://www.awaresystems.be/imaging/tiff/tifftags/extension.html">https://www.awaresystems.be/imaging/tiff/tifftags/extension.html</a>
 * 
 * @author dlegland
 */
public class ExtensionTags implements TagSet
{
    /**
     * 269 - The name of the document from which this image was scanned.
     */
    public static final int DOCUMENT_NAME = 269;

    /**
     * 285 - The name of the page from which this image was scanned.
     */
    public static final int PAGE_NAME = 285;

    /**
     * 317 - A mathematical operator that is applied to the image data before an encoding scheme is applied.
     */
    public static final int PREDICTOR = 317;

    /**
     * 339 - Specifies how to interpret each data sample in a pixel.
     */
    public static final int SAMPLE_FORMAT = 339;


    /* (non-Javadoc)
     * @see net.sci.image.io.tiff.TagSet#getTags()
     */
    @Override
    public  Map<Integer, TiffTag> getTags()
    {
        Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(2);
        
        add(tags, new TiffTag(DOCUMENT_NAME, "DocumentName", 
                "The name of the document from which this image was scanned"));
        add(tags, new TiffTag(PAGE_NAME, "PageName", 
                "The name of the page from which this image was scanned"));
        
        add(tags, new TiffTag(PREDICTOR, "Predictor", 
                "A mathematical operator that is applied to the image data before an encoding scheme is applied"));
        add(tags, new TiffTag(SAMPLE_FORMAT, "SampleFormat", 
                "Specifies how to interpret each data sample in a pixel"));
        
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
        return "Extension";
    }
    
}
