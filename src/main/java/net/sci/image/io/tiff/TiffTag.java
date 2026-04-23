/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.Map;

import net.sci.image.Image;

/**
 * Manages tag information in TIFF files.
 * 
 * For complete Tiff Tag list, see:
 * http://www.awaresystems.be/imaging/tiff/tifftags.html
 * 
 * Tags are gathered within "TagSets". Additional tag sets can be provided by
 * user to manage additional features or formats.
 * 
 * @see TagSet
 * 
 * @author dlegland
 */
public class TiffTag
{
    // =============================================================
    // static methods
    
    /**
     * Returns a map of all known tags, indexed by their code.
     * 
     * @return a map of known tags, using tag code as key and tag instance as
     *         values.
     */
    public static final Map<Integer, TiffTag> getAllTags()
    {
        return TagSetManager.getInstance().getAllTags();
    }
    
    
    // =============================================================
    // Class variables
    
    /**
     * The integer code used to identify this tag. Called "tag" in the TIFF
     * specification.
     */
    public int code;
    
    /** The name of this tag, for easy identification in GUI */
    public String name;
    
    /** An optional description of this tag */
    public String description = null;
    
    /**
     * The TagSet instance this tag belongs to. Can be null."
     */
    public TagSet tagSet = null;
    
    /**
     * The type of value stored by this tag. Type is usually defined at
     * creation, but in some cases it may be necessary to adapt the type to the
     * image.
     */
    public Entry.Type type;
    
    
    // =============================================================
    // Constructor
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag (in the TIFF specification, the code is
     *            called the tag, and the TiffTag is called an "entry").
     * @param name
     *            the short name of the tag, to facilitate interpretation
     */
    public TiffTag(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag (in the TIFF specification, the code is
     *            called the tag, and the TiffTag is called an "entry").
     * @param name
     *            the short name of the tag, to facilitate interpretation
     * @param description
     *            a more complete description of the tag
     */
    public TiffTag(int code, String name, String description)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        
        // setup default values
        this.type = Entry.Type.SHORT;
    }
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag (in the TIFF specification, the code is
     *            called the tag, and the TiffTag is called an "entry").
     * @param type
     *            the type of the data stored in the tag
     * @param name
     *            the short name of the tag, to facilitate interpretation
     * @param description
     *            a more complete description of the tag
     */
    public TiffTag(int code, Entry.Type type, String name, String description)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        
        // setup default values
        this.type = type;
    }
    
    
    // =============================================================
    // public methods
    
    
    public Entry newEntry()
    {
        return new Entry(this.code, this.type, 1, 0);
    }
    
    /**
     * After the value has been read, updates the specified image according to
     * the current value of the tag.
     * 
     * Default implementation does nothing. For custom tags, this method can be
     * used to update image metadata or annotations.
     * 
     * @param image
     *            the image to update
     * @param ifd
     *            the ImageFileDirectory this tag belongs to, in case the tag
     *            requires to know the value of additional tags.
     */
    public void update(Image image, ImageFileDirectory ifd)
    {
    }
}
