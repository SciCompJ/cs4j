/**
 * 
 */
package net.sci.image.io.tiff;

/**
 * An entry within an {@code ImageFileDirectory}. An entry is defined by a tag
 * code, an entry type, a count, a value, and a content. Entry type, count and
 * value are used to compute or to read the content of the entry. Interpretation
 * of the entry content depends on the entry tag.
 * 
 * @see ImageFileDirectory
 * @see TiffTag
 */
public class Entry
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
    // Class variables
    
    /**
     * The integer code used to identify this tag. Called "tag" in the TIFF
     * specification.
     */
    public int code;
    
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
    
    public Entry(int code, Type type, int count, int value)
    {
        this.code = code;
        this.type = type;
        this.count = count;
        this.value = value;
    }
    
    /**
     * Returns the identification code of the tag associated to this entry.
     * 
     * @return the tag code of this entry.
     */
    public int code()
    {
        return this.code;
    }
    
    
    /**
     * Sets the {@code value} field from the specified single byte value, and
     * sets up the {@code type} field to BYTE and the {@code count} field to 1.
     * 
     * @param value
     *            the byte value to set up.
     * @return the reference to this tag (for chaining operations)
     */
    public Entry setByteValue(byte value)
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
    public Entry setShortValue(short value)
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
    public Entry setIntValue(int value)
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
     * Sets the value of this entry, to populate either the {@code value} or the
     * {@code content} field. The {@code value} parameter is first casted
     * according to the type of the tag. If {@code value} parameter fits into 4
     * bytes, the {@code value} field of the tag is populated. Otherwise, the
     * {@code content} field is initialized, and the value field will be later
     * populated with offset to content.
     * 
     * @param value
     *            the value of the entry
     */
    public Entry setValue(Object value)
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
                System.err.println("Could not interpret tag with code: " + this.code);
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

    public String toString()
    {
        return new StringBuilder("Entry(")
                .append(this.code)
                .append(",").append(this.type)
                .append(",").append(this.count)
                .append(",").append(this.value)
                .append(")")
                .toString();
    }
}
