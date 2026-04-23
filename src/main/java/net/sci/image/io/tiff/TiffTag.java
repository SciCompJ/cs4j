/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.List;
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
     * The list of valid types for the value stored by entries with this.
     */
    List<Entry.Type> validTypes;
    
    /**
     * The default value used to initialize new entries.
     */
    int defaultValue;
    
    
    // =============================================================
    // Constructor
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag
     * @param name
     *            the short name of the tag, to facilitate interpretation
     */
    public TiffTag(int code, String name)
    {
        this(code, name, null);
    }
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag
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
        this.validTypes = List.of(Entry.Type.BYTE, Entry.Type.SHORT, Entry.Type.LONG);
    }
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag
     * @param type
     *            the type of the data stored in the tag
     * @param name
     *            the short name of the tag, to facilitate interpretation
     * @param description
     *            a more complete description of the tag
     */
    public TiffTag(int code, Entry.Type type, String name, String description)
    {
        this(code, type, name, description, 0);
    }
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag
     * @param type
     *            the type of the data stored in the tag
     * @param name
     *            the short name of the tag, to facilitate interpretation
     * @param default
     *            the default value used to initialize new entries
     * @param description
     *            a more complete description of the tag
     */
    public TiffTag(int code, Entry.Type type, String name, String description, int defaultValue)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        
        // setup default values
        this.validTypes = List.of(type);
        this.defaultValue = 0;
    }
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag
     * @param type
     *            the type of the data stored in the tag
     * @param name
     *            the short name of the tag, to facilitate interpretation
     * @param description
     *            a more complete description of the tag
     */
    public TiffTag(int code, List<Entry.Type> validTypes, String name, String description)
    {
        this(code, validTypes, name, description, 0);
    }
    
    /**
     * Creates a new Tiff Tag.
     * 
     * @param code
     *            the code of the tag
     * @param type
     *            the type of the data stored in the tag
     * @param name
     *            the short name of the tag, to facilitate interpretation
     * @param description
     *            a more complete description of the tag
     */
    public TiffTag(int code, List<Entry.Type> validTypes, String name, String description, int defaultValue)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        
        // setup default values
        this.validTypes = validTypes;
        this.defaultValue = defaultValue;
    }
    
    
    // =============================================================
    // public methods
    
    
    public Entry newEntry()
    {
        return new Entry(this.code, this.validTypes.getFirst(), 1, this.defaultValue);
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
