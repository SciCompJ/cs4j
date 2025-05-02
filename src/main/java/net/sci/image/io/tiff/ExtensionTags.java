/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.HashMap;
import java.util.Map;

import net.sci.image.io.PixelType;

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
    public static final class DocumentName extends TiffTag
    {
        public static final int CODE = 269;
        public DocumentName()
        {
            super(CODE, "DocumentName", "The name of the document from which this image was scanned");
        }
    }

    /**
     * 285 - The name of the page from which this image was scanned.
     */
    public static final class PageName extends TiffTag
    {
        public static final int CODE = 285;
        public PageName()
        {
            super(CODE, "PageName", "The name of the page from which this image was scanned");
        }
    }

    /**
     * 317 - A mathematical operator that is applied to the image data before an encoding scheme is applied.
     */
    public static final class Predictor extends TiffTag
    {
        public static final int CODE = 317;
        public Predictor()
        {
            super(CODE, "Predictor", "A mathematical operator that is applied to the image data before an encoding scheme is applied");
        }
    }

    /**
     * 339 - Specifies how to interpret each data sample in a pixel.
     */
    public static final class SampleFormat extends TiffTag
    {
        public static final int CODE = 339;
        
        public static final short UNSIGNED_INTEGER = 1;
        public static final short SIGNED_INTEGER = 2;
        public static final short FLOATING_POINT = 3;
        public static final short UNDEFINED = 4;
        
        public SampleFormat()
        {
            super(CODE, "SampleFormat", "Specifies how to interpret each data sample in a pixel");
            this.type = Type.SHORT;
            this.count = 1;
            this.value = UNDEFINED;
        }
        
        /**
         * Initializes from pixel type.
         * @param pixelType the type of pixel
         * @return a reference to this tag.
         */
        public TiffTag init(PixelType pixelType)
        {
            setShortValue(pixelType.isInteger()
                    ? pixelType.isSigned() ? SampleFormat.SIGNED_INTEGER : SampleFormat.UNSIGNED_INTEGER
                    : SampleFormat.FLOATING_POINT);
            return this;
        }
    }


    /* (non-Javadoc)
     * @see net.sci.image.io.tiff.TagSet#getTags()
     */
    @Override
    public  Map<Integer, TiffTag> getTags()
    {
        Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(4);
        
        add(tags, new DocumentName()); 
        add(tags, new PageName()); 
        add(tags, new Predictor()); 
        add(tags, new SampleFormat()); 
       
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
