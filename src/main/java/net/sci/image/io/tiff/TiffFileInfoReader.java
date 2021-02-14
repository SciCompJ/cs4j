/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;

import net.sci.image.io.BinaryDataReader;

/**
 * Read TiffFileInfo data from a Tiff File. These file infos can later be used
 * to read image data from the same TIFF file.
 * 
 * @author David Legland
 *
 */
public class TiffFileInfoReader
{
	// =============================================================
	// Class variables
	
    /**
     * The name of the file to read the data from.
     * Initialized at construction.
     */
    String filePath;
    
	/**
	 * The file stream from which data are extracted, and which manages data endianness.
	 */
	BinaryDataReader dataReader;

	/**
	 * The byte order used within the open stream.
	 */
	ByteOrder byteOrder;
	
	
	// =============================================================
	// Constructor

	public TiffFileInfoReader(String fileName) throws IOException
	{
		this(new File(fileName));
	}

	public TiffFileInfoReader(File file) throws IOException
	{
		this.filePath = file.getPath();
	}
	
	
	// =============================================================
	// Methods
	
    /**
     * Reads the set of image file directories within this TIFF File.
     * 
     * @return the collection of TiffFileInfo stored within this file.
     * @throws IOException
     *             if an error occurs
     */
    public ArrayList<TiffFileInfo> readImageFileDirectories() throws IOException
	{
        // open the stream from file name
        createDataReader(new File(this.filePath));
        
        // Read file offset of first Image
		dataReader.seek(4);
		long offset = ((long) dataReader.readInt()) & 0xffffffffL;
		// System.out.println("offset: " + offset);

		if (offset < 0L)
		{
			dataReader.close();
			throw new RuntimeException("Found negative offset in tiff file");
		}

		ArrayList<TiffFileInfo> infoList = new ArrayList<TiffFileInfo>();
		while (offset > 0L)
		{
			dataReader.seek(offset);
			TiffFileInfo info = readNextImageFileDirectory();
			if (info != null)
			{
				infoList.add(info);
			}

			offset = ((long) dataReader.readInt()) & 0xffffffffL;
		}
		dataReader.close();
        
		if (infoList.size() == 0)
		{
			throw new RuntimeException("Could not read any Tiff File Info from file: " + this.filePath);
		}
		
		return infoList;
	}
	
	/**
     * Opens the input stream, and reads the main header of the TIFF file. The header is composed of 8 bytes:
     * <ul>
     * <li>2 bytes for indicating the byte order</li>
     * <li>2 bytes for the magic number 42</li>
     * <li>4 bytes for indicating the offset of the first Image File Directory</li>
     * </ul>
     * 
     * @throws IOException if a reading problem occured
     * @throws RuntimeException if the endianess of the magic number could not be read
     */
    private void createDataReader(File file) throws IOException
    {
        // open the stream
    	RandomAccessFile inputStream = new RandomAccessFile(file, "r");
    	
    	// read bytes indicating endianness
    	int b1 = inputStream.read();
    	int b2 = inputStream.read();
    	int byteOrderInfo = ((b2 << 8) + b1);
    
    	// Determine file endianness
    	// If a problem occur, this may be the sign of an file in another format
    	if (byteOrderInfo == 0x4949) // "II"
    	{
    	    this.byteOrder = ByteOrder.LITTLE_ENDIAN;
    	}
    	else if (byteOrderInfo == 0x4d4d) // "MM"
    	{
            this.byteOrder = ByteOrder.BIG_ENDIAN;
    	}
    	else
    	{
    		inputStream.close();
    		throw new RuntimeException(
    				"Could not decode endianness of TIFF File: " + file.getName());
    	}
    
    	// Create binary data reader from input stream
    	this.dataReader = new BinaryDataReader(inputStream, this.byteOrder);
    	
    	// Read the magic number indicating tiff format
    	int magicNumber = dataReader.readShort();
    	if (magicNumber != 42)
    	{
    		inputStream.close();
    		throw new RuntimeException(
    				"Invalid TIFF file, magic number is different from 42");
    	}
    }

    /**
	 * Reads the next Image File Directory structure from the input stream.
	 */
	private TiffFileInfo readNextImageFileDirectory() throws IOException
	{
		// Read and control the number of entries
		int nEntries = dataReader.readShort();
		if (nEntries < 1 || nEntries > 1000)
		{
			throw new RuntimeException("Number of entries is out of range: "
					+ nEntries);
		}

		// create a new FileInfo instance
		TiffFileInfo info = new TiffFileInfo();
		info.byteOrder = this.byteOrder;
		
		// retrieve the list of Tiff Tags that can be interpreted
		Map<Integer, TiffTag> tagMap = TiffTag.getAllTags();

		// Read each entry
		for (int i = 0; i < nEntries; i++)
		{
			// read tag code
			int tagCode = dataReader.readShort();
			
			// read type of tag data
			TiffTag.Type type = readTagType(tagCode);
			
			// reader number of data and value / offset
			int count = dataReader.readInt();
			int value = readTagValue(type, count);

			TiffTag tag = tagMap.get(tagCode);
			
			// if tag was not found, create a default empty tag
			boolean unknownTag = tag == null;
			if (unknownTag)
			{
				tag = new TiffTag(tagCode, "Unknown");
			}
			
			// init tag info
			tag.type = type;
			tag.count = count;
			tag.value = value;
			tag.readContent(dataReader);

			if (unknownTag)
			{
				System.out.println("Unknown tag with code " + tagCode + ". Type="
						+ type + ", count=" + count + ", value=" + tag.content);
			}

			// call the initialization procedure specific to tag
			tag.init(this.dataReader);

			// populates the current TiffFileInfo instance
            tag.process(info);

			info.tags.put(tagCode, tag);
		}

		return info;
	}

	private TiffTag.Type readTagType(int tagCode) throws IOException
	{
		// read tag data info
		int typeValue = dataReader.readShort();
		TiffTag.Type type;
		try 
		{
			type = TiffTag.Type.getType(typeValue); 
		}
		catch(IllegalArgumentException ex)
		{
			throw new RuntimeException(String.format("Tag with code %d has incorrect type value: %d", tagCode, typeValue));
		}
		
		return type;
	}

	private int readTagValue(TiffTag.Type type, int count) throws IOException
	{
		int value;
		if (type == TiffTag.Type.SHORT && count == 1)
		{
			value = dataReader.readShort();
			dataReader.readShort();
		}
		else
		{
			value = dataReader.readInt();
		}
		return value;
	}

    // =============================================================
    // Methods
    
    /**
     * Closes the stream open by this class.
     * 
     * @throws IOException
     *             if an error occurs
     */
    public void close() throws IOException
    {
    	this.dataReader.close();
    }

}
