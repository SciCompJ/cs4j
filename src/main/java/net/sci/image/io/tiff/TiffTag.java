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
    // Static constants
    
    /**
     * The type of data stored by a tag.
     */
    public static enum Type
    {
        /** Unknown type of tag (code 0) */
        UNKNOWN(0, 0),
        /** Tag value stored with single byte(s) (code 1) */
        BYTE(1, 1),
        /** Tag content stored in ASCII (code 2) */
        ASCII(2, 1),
        /** Tag value or content stored with 16-bits integer (code 3) */
        SHORT(3, 2),
        /** Tag value or content stored with 32-bits integer (code 4) */
        LONG(4, 4),
        /**
         * Tag content stored with rational (code 5), i.e. by storing each value
         * with a pair of 4-byte integers.
         */
        RATIONAL(5, 8),
        /** Tag value(s) stored with double precision (64-bits) floating point (code 12) */
        DOUBLE(12, 8);
        
        int code;
        int byteCount;
        
        private Type(int code, int byteCount)
        {
            this.code = code;
            this.byteCount = byteCount;
        }
        
        public int code()
        {
            return code;
        }
        
        /**
         * Identifies the type from an integer code as read from a tiff entry.
         * 
         * @param typeCode
         *            the code of the type
         * @return the corresponding type
         */
        public static final Type getType(int typeCode)
        {
            return switch (typeCode)
            {
                case 1 -> BYTE;
                case 2 -> ASCII;
                case 3 -> SHORT;
                case 4 -> LONG;
                case 5 -> RATIONAL;
                case 12 -> DOUBLE;
                default -> UNKNOWN;
            };
        }
        
        public int byteCount()
        {
            return byteCount;
        }
    };
    
    
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
    public Type type;
    
    
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
        this.type = Type.SHORT;
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
    public TiffTag(int code, Type type, String name, String description)
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
        Entry.Type entryType = convertType(this.type);
        return new Entry(this.code, entryType, 1, 0);
    }
    
    private static final Entry.Type convertType(TiffTag.Type tagType)
    {
        return switch (tagType)
        {
            case BYTE -> Entry.Type.BYTE;
            case ASCII -> Entry.Type.ASCII;
            case SHORT -> Entry.Type.SHORT;
            case LONG -> Entry.Type.LONG;
            case RATIONAL -> Entry.Type.RATIONAL;
            case DOUBLE -> Entry.Type.DOUBLE;
            default -> Entry.Type.UNKNOWN;
        };
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
