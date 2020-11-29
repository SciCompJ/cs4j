/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;

import net.sci.image.io.BinaryDataReader;

/**
 * Manages tag information in TIFF files.
 * 
 * For complete Tiff Tag list, see http://www.awaresystems.be/imaging/tiff/tifftags.html  
 * @author dlegland
 *
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
		UNKNOWN,
		BYTE,
		ASCII,
		SHORT,
		LONG,
		RATIONAL;
		
		public static final Type getType(int typeCode)
		{
			switch (typeCode)
			{
			case 1: return BYTE;
			case 2: return ASCII;
			case 3: return SHORT;
			case 4: return LONG;
			case 5: return RATIONAL;
			default: return UNKNOWN;
			}
		}
	};

	
	// =============================================================
	// static methods

	/**
	 * Returns a map of all known tags, indexed by their code.
	 * 
	 * @return a map of known tags, using tag code as key and tag instance as values.
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
	
	/** The TagSet instance this tag belon gto. Can be null."
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
	public Object content;

	
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
		// parse tag data
		switch (this.type)
		{
		case BYTE:
			this.content = Integer.valueOf(value);
			break;
		case SHORT:
			this.content = Integer.valueOf(value);
			break;
		case LONG:
			this.content = Integer.valueOf(value);
			break;
		case ASCII:
			this.content = readAscii(dataReader);
			break;
		case RATIONAL:
			// convert tag value to long offset for reading large buffer
			this.content = readRational(dataReader);
			break;
			
		case UNKNOWN:
			System.err.println("Could not interpret tag with code: "
					+ this.code + " (" + this.name + ")");
			break;
		}		
	}
	
    /**
     * Initializes the content field of this tag, based on value and eventually data reader.
     * 
     * @param dataReader
     *            the instance of DataReader used to read information
     * @throws IOException
     *             if an error occurs
     */
	public void init(BinaryDataReader dataReader) throws IOException
	{
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
    
	
	// =============================================================
	// protected methods used by subclasses
	
	protected int[][] readColorMap(BinaryDataReader dataReader, int lutLength)
			throws IOException
	{
		// Allocate memory for raw array
	    // (each triplet of components is stored in two bytes)
		int nBytes = 3 * lutLength * 2;
		byte[] lut16 = new byte[nBytes];
		
		// convert state to long offset for reading large buffer
		long offset = ((long) this.value) & 0xffffffffL;

		// read the full raw array
		long saveLoc = dataReader.getFilePointer();
		dataReader.seek(offset);
		int nRead = dataReader.readByteArray(lut16);
		dataReader.seek(saveLoc);
		if (nRead != nBytes)
		{
			throw new IOException(
					"Could not decode the color palette from TIFF File");
		}
		
		// convert raw array into N-by-3 look-up table
		int[][] lut = new int[lutLength][3];
		int j = 0;
		if (dataReader.getOrder() == ByteOrder.LITTLE_ENDIAN)
			j++;
		for (int i = 0; i < lutLength; i++)
		{
			lut[i][0] = lut16[j] & 0x00FF;
			lut[i][1] = lut16[j + 512] & 0x00FF;
			lut[i][2] = lut16[j + 1024] & 0x00FF;
			j += 2;
		}
		return lut;
	}

	protected String readAscii(BinaryDataReader dataReader) throws IOException
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

	protected int[] readArray(BinaryDataReader dataReader) throws IOException
	{
		if (this.count == 1)
		{
			return new int[] { this.value };
		}

		if (this.type == TiffTag.Type.SHORT)
		{
			return readShortArray(dataReader);
		}
		else
		{
			return readIntArray(dataReader);
		}
	}

	protected int[] readShortArray(BinaryDataReader dataReader) throws IOException
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

	protected int[] readIntArray(BinaryDataReader dataReader) throws IOException
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
     * Read the short state stored at the specified position
     * 
     * @param dataReader
     *            the instance of BinaryDataReader to read from
     * @return the short content at the specified position, as an integer
     * @throws IOException
     *             if an I/O Exception occurs
     */
	protected int readShort(BinaryDataReader dataReader) throws IOException
	{
		// convert tag value to long offset for reading large buffer
		long offset = ((long) this.value) & 0xffffffffL;

		long pos0 = dataReader.getFilePointer();
		dataReader.seek(offset);
		int result = dataReader.readShort();
		dataReader.seek(pos0);
		return result;
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
	protected double readRational(BinaryDataReader dataReader) throws IOException
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
