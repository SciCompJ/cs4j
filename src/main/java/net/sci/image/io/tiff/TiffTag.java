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
    
    /**
     * The number of data stored by this tag.
     */
    public int count;
    
    /**
     * The integer value associated to this tag, that may have a different
     * meaning depending on tag type.
     */
    public int value;
    
    /**
     * The data contained by this tag, that have to be interpreted depending on
     * the type.
     */
    public Object content = null;
    
    
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
        this.count = 1;
        this.value = 0;
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
        this.count = 1;
        this.value = 0;
    }
    
    
    // =============================================================
    // public methods
    
    /**
     * Initializes the type, count, and value of this tag.
     * 
     * @param type
     *            the type of data this tag contain
     * @param count
     *            the number of elementary data
     * @param value
     *            an initial value for the tag
     * @return the reference to this tag (for chaining operations)
     */
    public TiffTag init(Type type, int count, int value)
    {
        this.type = type;
        this.count = count;
        this.value = value;
        return this;
    }
    
    /**
     * Changes the initial type of this tag. 
     * 
     * @param type
     *            the new type of data contained within this tag
     * @return the reference to this tag (for chaining operations)
     */
    public TiffTag setType(Type type)
    {
        this.type = type;
        return this;
    }
    
    /**
     * Sets the {@code value} field from the specified single byte value, and
     * sets up the {@code type} field to BYTE and the {@code count} field to 1.
     * 
     * @param value
     *            the byte value to set up.
     * @return the reference to this tag (for chaining operations)
     */
    public TiffTag setByteValue(byte value)
    {
        if (this.type != Type.BYTE)
        {
            System.err.println(String.format("Set value of tag %d as a BYTE, while its type is %s",
                    this.code, this.type));
        }
        this.type = Type.BYTE;
        this.count = 1;
        this.value = value;
        return this;
    }

    /**
     * Sets the {@code value} field from the specified single short value, and
     * sets up the {@code type} field to SHORT and the {@code count} field to 1.
     * 
     * @param value
     *            the shirt integer value to set up.
     * @return the reference to this tag (for chaining operations)
     */
    public TiffTag setShortValue(short value)
    {
        if (this.type != Type.SHORT)
        {
            System.err.println(String.format("Set value of tag %d as a SHORT, while its type is %s",
                    this.code, this.type));
        }
        this.type = Type.SHORT;
        this.count = 1;
        this.value = value;
        return this;
    }

    /**
     * Sets the {@code value} field from the specified single integer value, and
     * sets up the {@code type} field to LONGs and the {@code count} field to 1.
     * 
     * @param value
     *            the integer value to set up.
     * @return the reference to this tag (for chaining operations)
     */
    public TiffTag setIntValue(int value)
    {
        if (this.type != Type.LONG)
        {
            System.err.println(String.format("Set value of tag %d as a LONG, while its type is %s",
                    this.code, this.type));
        }
        this.type = Type.LONG;
        this.count = 1;
        this.value = value;
        return this;
    }

    /**
     * Initializes the type, count, and value (and eventually also the content)
     * of this tag, based on the content and the meta-data of the specified
     * image.
     * 
     * This method is called during image writing. Default: initializes to
     * type=SHORT, count=1, and value=0.
     * 
     * @param image
     *            the image used to initialize the tag
     * @return the reference to this tag (for chaining operations)
     */
    public TiffTag initFrom(Image image)
    {
        this.type = Type.SHORT;
        this.count = 1;
        this.value = 0;
        return this;
    }

    /**
     * Creates a short summary of the content, based on the value or content
     * stored within the tag.
     * 
     * @return a short summary of the content of this tag.
     */
    public String contentSummary()
    {
        return switch (this.type)
        {
            case BYTE -> count == 1 ? Integer.toString(value) : createDesc((byte[]) this.content, 5);
            case SHORT -> count == 1 ? Integer.toString(value) : createDesc((short[]) this.content, 5);
            case LONG -> count == 1 ? Integer.toString(value) : createDesc((int[]) this.content, 5);
            case ASCII -> (String) this.content;
            case RATIONAL -> Double.toString((double) this.content);
            case DOUBLE -> createDesc((double[]) this.content, 5);
            default -> null;
        };
    }
    
    private static final String createDesc(byte[] array, int nMax)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(Byte.toString(array[0]));
        for (int i = 1; i < Math.min(array.length, nMax); i++)
        {
            sb.append(",").append(Byte.toString(array[i]));
        }
        if (array.length > nMax)
        {
            sb.append("...");
        }
        sb.append("}");
        return sb.toString();
    }
    
    private static final String createDesc(short[] array, int nMax)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(Short.toString(array[0]));
        for (int i = 1; i < Math.min(array.length, nMax); i++)
        {
            sb.append(",").append(Short.toString(array[i]));
        }
        if (array.length > nMax)
        {
            sb.append("...");
        }
        sb.append("}");
        return sb.toString();
    }
    
    private static final String createDesc(int[] array, int nMax)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(Integer.toString(array[0]));
        for (int i = 1; i < Math.min(array.length, nMax); i++)
        {
            sb.append(",").append(Integer.toString(array[i]));
        }
        if (array.length > nMax)
        {
            sb.append("...");
        }
        sb.append("}");
        return sb.toString();
    }
    
    private static final String createDesc(double[] array, int nMax)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(Double.toString(array[0]));
        for (int i = 1; i < Math.min(array.length, nMax); i++)
        {
            sb.append(",").append(Double.toString(array[i]));
        }
        if (array.length > nMax)
        {
            sb.append("...");
        }
        sb.append("}");
        return sb.toString();
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
    
	/**
     * Sets the value of this tag, to populate either the {@code value} or the
     * {@code content} field. The {@code value} parameter is first casted
     * according to the type of the tag. If {@code value} parameter fits into 4
     * bytes, the {@code value} field of the tag is populated. Otherwise, the
     * {@code content} field is initialized, and the value field will be later
     * populated with offset to content.
     * 
     * @param value
     *            the value of the entry
     */
    public TiffTag setValue(Object value)
    {
        // check the contents correspond to tag type
        switch(this.type)
        {
            case BYTE -> {
                if (value instanceof byte[] array)
                {
                    this.count = array.length;
                    if (array.length == 1)
                    {
                        this.value = array[0];
                        this.content = null;
                    }
                    else
                    {
                        this.content = array;
                    }
                }
                else
                {
                    throw new RuntimeException("If tag type is BYTE, content must be an array of bytes");
                }
            }
            case SHORT -> {
                if (value instanceof short[] array)
                {
                    this.count = array.length;
                    if (array.length == 1)
                    {
                        this.value = array[0];
                        this.content = null;
                    }
                    else
                    {
                        this.content = array;
                    }
                }
                else
                {
                    throw new RuntimeException("If tag type is SHORT, content must be an array of short");
                }
            }
            case LONG -> {
                if (value instanceof int[] array)
                {
                    this.count = array.length;
                    if (array.length == 1)
                    {
                        this.value = array[0];
                        this.content = null;
                    }
                    else
                    {
                        this.content = array;
                    }
                }
                else
                {
                    throw new RuntimeException("If tag type is LONG, content must be an array of int");
                }
            }
            case ASCII -> {
                if (value instanceof String string)
                {
                    byte[] bytes = string.getBytes();
                    this.content = bytes;
                    this.count = bytes.length;
                }
                else
                {
                    throw new RuntimeException("If tag type is ASCII, content must be a String");
                }
            }
            case RATIONAL -> {
                if (!(value instanceof int[]))
                {
                    throw new RuntimeException("If tag type is RATIONAL, content must be an array of int");
                }
                
                this.content = value;
                this.count = 1;
            }
            default -> {
                System.err.println("Could not interpret tag with code: " + this.code + " (" + this.name + ")");
            }
        }
        
        return this;
    }
    
    /**
     * Returns the number of bytes necessary to write the content of this tag,
     * or 0 if the value fits within less that 4 bytes. This method is used to
     * determine the size of the tag data when writing a file.
     * 
     * @return the number of bytes used to store the content of this tag.
     */
    public int contentSize()
    {
        return content == null ? 0 : count * type.byteCount();
    }
    
    /**
     * Returns the code of this tag as a hashcode.
     * 
     * @return the code associated to this tag.
     */
    @Override
    public int hashCode()
    {
        return this.code;
    }
    
    public boolean equals(Object obj)
    {
        // check class type
        if (!(obj instanceof TiffTag))
        {
            return false;
        }
        
        // class cast, and check class membrs
        TiffTag that = (TiffTag) obj;
        if (this.code != that.code) return false;
        if (this.type != that.type) return false;
        if (this.count != that.count) return false;
        
        // compare contents
        return this.content.equals(that.content);
    }
}
