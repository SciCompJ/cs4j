/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Map;

import net.sci.image.Image;
import net.sci.image.io.BinaryDataReader;

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
        RATIONAL(5, 8);
        
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
        
        public static final Type getType(int typeCode)
        {
            return switch (typeCode)
            {
                case 1 -> BYTE;
                case 2 -> ASCII;
                case 3 -> SHORT;
                case 4 -> LONG;
                case 5 -> RATIONAL;
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
     * The integer code used to identify this tag.
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
     * The type of value stored by this tag.
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
    
    public TiffTag(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
    
    public TiffTag(int code, String name, String description)
    {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    // =============================================================
    // public new methods
    
    /**
     * Initialize the content of the tag from the data reader, given its code
     * and the specified value.
     * 
     * @param dataReader
     *            the instance of DataReader to read optional information from
     * @throws IOException
     *             if tried to read from the file and problem occurred
     */
    public void readContent(BinaryDataReader dataReader) throws IOException
    {
        switch (this.type)
        {
            case BYTE:
                if (this.count == 1)
                    this.content = Integer.valueOf(value);
                else
                    this.content = readByteArray(dataReader);
                break;
            case SHORT:
                if (this.count == 1)
                    this.content = Integer.valueOf(value);
                else
                    // convert short array into int array
                    this.content = readShortArray(dataReader);
                break;
            case LONG:
                if (this.count == 1)
                    this.content = Integer.valueOf(value);
                else
                    this.content = readIntArray(dataReader);
                break;
            case ASCII:
                // Automatically convert byte array to String
                this.content = readAscii(dataReader);
                break;
            case RATIONAL:
                // Assume only one rational is specified // TODO: allow arrays
                this.content = readRational(dataReader);
                break;
            default:
                System.err.println("Could not interpret tag with code: " + this.code + " (" + this.name + ")");
                this.content = null;
                break;
        }
    }
    
    /**
     * Updates the specified FileInfo data structure according to the current
     * value of the tag.
     * 
     * @param info
     *            an instance of TiffFileInfo.
     */
    public void process(TiffFileInfo info)
    {
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
    public TiffTag init(Image image)
    {
        this.type = Type.SHORT;
        this.count = 1;
        this.value = 0;
        return this;
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
    public void setValue(Object value)
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
                this.count = 2;
            }
            default -> {
                System.err.println("Could not interpret tag with code: " + this.code + " (" + this.name + ")");
                 
            }
        }
    }
    
    /**
     * Writes the entry corresponding to this tag into the specified stream,
     * with the specified byte order.
     * 
     * @param out
     *            the stream to write in
     * @param order
     *            the byte order
     * @return the number of bytes that will need to be written on the IFD data
     * @throws IOException 
     */
    public int writeEntry(OutputStream out, ByteOrder order, int currentDataOffset) throws IOException
    { 
        writeShort(out, order, this.code);
        writeShort(out, order, type.code());
        writeInt(out, order, count);
        if (count == 1 && type.equals(TiffTag.Type.SHORT))
        {
            writeShort(out, order, value);
            writeShort(out, order, 0);
        }
        else if (content != null)
        {
            // write the offset to content data 
            writeInt(out, order, value);
        }
        else
        {
            writeInt(out, order, value);
        }
        
        return contentSize();
    }
    
    /**
     * Writes the entry corresponding to this tag into the specified stream,
     * with the specified byte order.
     * 
     * @param out
     *            the stream to write in
     * @param order
     *            the byte order
     * @throws IOException 
     */
    public void writeEntry(OutputStream out, ByteOrder order) throws IOException
    { 
        writeShort(out, order, this.code);
        writeShort(out, order, type.code());
        writeInt(out, order, count);
        if (count == 1 && type.equals(TiffTag.Type.SHORT))
        {
            writeShort(out, order, value);
            writeShort(out, order, 0);
        }
        else if (content != null)
        {
            // write the offset to content data 
            writeInt(out, order, value);
        }
        else
        {
            writeInt(out, order, value);
        }
    }

    /**
     * Returns the number if bytes used by the content of this tag, or 0 if the
     * value fits within less that 4 bytes.
     * 
     * @return the number of bytes used to store the content of this tag.
     */
    public int contentSize()
    {
        return content == null ? 0 : count * type.byteCount();
    }

    /**
     * Writes the data of this entry into the specified stream, with the
     * specified byte order. The number of bytes that will be written must be
     * the same as the result of the {@code writeEntry()} method.
     * 
     * @param out
     *            the stream to write in
     * @param order
     *            the byte order
     * @throws IOException 
     */
    public void writeContent(OutputStream out, ByteOrder order) throws IOException
    {
        if (content == null) return;
        switch (type)
        {
            case BYTE -> {
                byte[] data = (byte[]) content;
                out.write(data);
            }
            case ASCII -> {
                byte[] data = ((String) content).getBytes();
                out.write(data);
            }
            case SHORT -> {
                for (short s : (short[]) content)
                {
                    writeShort(out, order, s);
                }
            }
            case LONG -> {
                for (int data : (int[]) content)
                {
                    writeInt(out, order, data);
                }
            }
            case RATIONAL -> {
                int[] data = (int[]) content;
                writeInt(out, order, data[0]);
                writeInt(out, order, data[1]);
            }
            default -> System.err.println("Unable to write tag with code " + code);
        }
    }
    
    
    // =============================================================
    // protected methods used by subclasses
    
    private String readAscii(BinaryDataReader dataReader) throws IOException
    {
        // Allocate memory for string buffer
        byte[] data = new byte[this.count];
        
        // read string buffer
        if (this.count <= 4)
        {
            // unpack integer
            int value = this.value;
            for (int i = 0; i < this.count; i++)
            {
                data[i] = (byte) (value & 0x00FF);
                value = value >> 8;
            }
        }
        else
        {
            // convert state to long offset for reading large buffer
            long offset = ((long) this.value) & 0xffffffffL;
            
            long pos0 = dataReader.getFilePointer();
            dataReader.seek(offset);
            dataReader.readByteArray(data);
            dataReader.seek(pos0);
        }
        
        return new String(data);
    }
    
    private byte[] readByteArray(BinaryDataReader dataReader) throws IOException
    {
        // allocate memory for result
        byte[] res = new byte[this.count];
        
        // save pointer location
        long saveLoc = dataReader.getFilePointer();
        
        // convert tag value to long offset
        long offset = ((long) this.value) & 0xffffffffL;
        dataReader.seek(offset);
        
        // fill up array
        int nRead = dataReader.readByteArray(res);
        if (nRead != this.count)
        {
            throw new RuntimeException("Could not read all the required bytes");
        }
        
        // restore pointer and return result
        dataReader.seek(saveLoc);
        return res;
    }
    
    private int[] readShortArray(BinaryDataReader dataReader) throws IOException
    {
        // convert tag value to long offset for reading large buffer
        long offset = ((long) this.value) & 0xffffffffL;
        
        // allocate memory for result
        int[] res = new int[this.count];
        
        // save pointer location
        long saveLoc = dataReader.getFilePointer();
        
        // fill up array
        dataReader.seek(offset);
        for (int c = 0; c < this.count; c++)
        {
            res[c] = dataReader.readShort();
        }
        
        // restore pointer and return result
        dataReader.seek(saveLoc);
        return res;
    }
    
    private int[] readIntArray(BinaryDataReader dataReader) throws IOException
    {
        // convert tag value to long offset for reading large buffer
        long offset = ((long) this.value) & 0xffffffffL;
        
        // allocate memory for result
        int[] res = new int[this.count];
        
        // save pointer location
        long saveLoc = dataReader.getFilePointer();
        
        // fill up array
        dataReader.seek(offset);
        for (int c = 0; c < this.count; c++)
        {
            res[c] = dataReader.readInt();
        }
        
        // restore pointer and return result
        dataReader.seek(saveLoc);
        return res;
    }
    
    /**
     * Reads the rational value at the given position, as the ratio of two
     * integers.
     * 
     * @param dataReader
     *            the instance of BinaryDataReader to read from
     * @return the approximated rational content at the specified position, as a
     *         double
     * @throws IOException
     *             if an I/O Exception occurs
     */
    private double readRational(BinaryDataReader dataReader) throws IOException
    {
        // convert tag value to long offset for reading large buffer
        long offset = ((long) this.value) & 0xffffffffL;
        
        long saveLoc = dataReader.getFilePointer();
        dataReader.seek(offset);
        
        int numerator = dataReader.readInt();
        int denominator = dataReader.readInt();
        dataReader.seek(saveLoc);
        
        if (denominator != 0)
            return (double) numerator / denominator;
        else
            return 0.0;
    }
    
    private static final void writeShort(OutputStream out, ByteOrder order, int v) throws IOException
    {
        if (order == ByteOrder.LITTLE_ENDIAN)
        {
            out.write(v & 255);
            out.write((v >>> 8) & 255);
        }
        else
        {
            out.write((v >>> 8) & 255);
            out.write(v & 255);
        }
    }
    
    private static final void writeInt(OutputStream out, ByteOrder order, int v) throws IOException
    {
        if (order == ByteOrder.LITTLE_ENDIAN)
        {
            out.write(v & 255);
            out.write((v >>> 8) & 255);
            out.write((v >>> 16) & 255);
            out.write((v >>> 24) & 255);
        }
        else
        {
            out.write((v >>> 24) & 255);
            out.write((v >>> 16) & 255);
            out.write((v >>> 8) & 255);
            out.write(v & 255);
        }
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
