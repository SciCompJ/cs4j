/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.color.BufferedPackedByteRGB8Array2D;
import net.sci.array.color.BufferedPackedShortRGB16Array2D;
import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array2D;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.process.shape.Reshape;
import net.sci.array.scalar.BufferedFloat32Array2D;
import net.sci.array.scalar.BufferedFloat32Array3D;
import net.sci.array.scalar.BufferedInt32Array2D;
import net.sci.array.scalar.BufferedUInt16Array2D;
import net.sci.array.scalar.BufferedUInt16Array3D;
import net.sci.array.scalar.BufferedUInt8Array2D;
import net.sci.array.scalar.BufferedUInt8Array3D;
import net.sci.array.scalar.SlicedUInt8Array3D;
import net.sci.array.scalar.UInt8Array;
import net.sci.axis.Axis;
import net.sci.image.Calibration;
import net.sci.image.DefaultColorMap;
import net.sci.image.Image;
import net.sci.image.ImageAxis;
import net.sci.image.io.tiff.BaselineTags;
import net.sci.image.io.tiff.TiffFileInfo;
import net.sci.image.io.tiff.TiffTag;

/**
 * Provides methods for reading Image files in TIFF Format.
 * 
 * @author David Legland
 *
 */
public class TiffImageReader implements ImageReader
{
	// =============================================================
	// Static
	
	
	// =============================================================
	// Class variables
	
    String filePath;
    
	/**
	 * The file stream from which data are extracted, and which manages data endianness.
	 */
	BinaryDataReader dataReader;

	/**
	 * The byte order used within the open stream.
	 */
	ByteOrder byteOrder;
	
	/**
	 * The list of file info stored in the TIFF file.
	 */
	ArrayList<TiffFileInfo> fileInfoList;
	
	
	// =============================================================
	// Constructor

	public TiffImageReader(String fileName) throws IOException
	{
		this(new File(fileName));
	}

	public TiffImageReader(File file) throws IOException
	{
		this.filePath = file.getPath();
	    createTiffDataReader(file);
		this.fileInfoList = readImageFileDirectories();
	}

	/**
	 * Reads the main header of the TIFF file. The header is composed of 8 bytes:
	 * <ul>
	 * <li>2 bytes for indicating the byte order</li>
	 * <li>2 bytes for the magic number 42</li>
	 * <li>4 bytes for indicating the offset of the first Image File Directory</li>
	 * </ul>
	 * 
     * @throws IOException if a reading problem occured
     * @throws RuntimeException if the endianess of the magic number could not be read
	 */
	private void createTiffDataReader(File file) throws IOException
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

	
	// =============================================================
	// Methods
	
	/**
     * Reads the image at the specified index.
     * 
     * @param index
     *            the index of image within this file reader
     * @return the image at the specified index
     * @throws IOException
     *             if an error occurs
     */
	public Image readImage(int index) throws IOException
	{
        // check validity of index input
        if (index >= this.fileInfoList.size())
        {
            throw new IllegalArgumentException("Requires an index below the number of images ("
                    + this.fileInfoList.size() + ")");
        }

        // Read File information of the image stored in the file
        TiffFileInfo fileInfo = this.fileInfoList.get(index);

        // Read image data
        Array<?> data = readImageData(fileInfo);

        // Create new Image
        Image image = new Image(data);
        
        // Add Image meta-data
        if (fileInfo.lut != null)
        {
            image.getDisplaySettings().setColorMap(new DefaultColorMap(fileInfo.lut));
        }
        image.tiffTags = fileInfo.tags;
        
        return image;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see jipl.io.ImageReader#readImage()
	 */
	@Override
	public Image readImage() throws IOException
	{
		// Read the set of image information in the file
		if (this.fileInfoList.size() == 0)
		{
			throw new RuntimeException("Could not read any meta-information from file");
		}

		// Read File information of the first image stored in the file
		TiffFileInfo info = this.fileInfoList.get(0);
        
		// Attempts to read
		// Check if the file was saved by ImageJ software
		if (hasImageJDescription(info))
        {
		    return readImageJImage(info);
        }
		
		
		// Read image data
		Array<?> data = readImageData();

		// Create new Image
		Image image = new Image(data);
        image.tiffTags = info.tags;
		
        // setup the file related to the image
		image.setNameFromFileName(filePath);
        image.setFilePath(this.filePath);

        // Setup spatial calibration
		setupSpatialCalibration(image, info);
		
		// setup LUT
		if (info.lut != null)
		{
			image.getDisplaySettings().setColorMap(new DefaultColorMap(info.lut));
		}
		
		return image;
	}

	private boolean hasImageJDescription(TiffFileInfo info)
    {
        // Get the  description tag, or null if not initialized
        TiffTag tag = info.tags.get(BaselineTags.IMAGE_DESCRIPTION);
        if (tag == null)
        {
            return false;
        }
    
        // extract description string
        String description = (String) tag.content;
        if (!description.startsWith("ImageJ"))
        {
            return false;
        }
        
        Map<String,String> imagejTokens = parseImageJTokens(description);
        return imagejTokens.containsKey("slices");
    }

	/**
     * If one of the Tiff Tags has a description beginning with "ImageJ", this
     * method use a specific process to extract image data as saved by ImageJ.
     * 
     * @param info
     *            The set of Tiff Tags of the image
     * @param description
     *            the content of the tag 270, as a String
     * @return the Image stored within the file
     * @throws IOException
     *             if an I/O error occurred
     */
	private Image readImageJImage(TiffFileInfo info) throws IOException
	{
	    // Get the  description tag, or null if not initialized
	    TiffTag tag = info.tags.get(BaselineTags.IMAGE_DESCRIPTION);
	    if (tag == null)
	    {
	        throw new IllegalArgumentException("Requires a description TiffTag with index 270");
	    }
	    
	    // extract description string
	    String description = (String) tag.content; 
	    if (!description.startsWith("ImageJ"))
	    {
	        throw new IllegalArgumentException("Description tag must start with \"ImageJ\"");
	    }
	    
	    System.out.println("import ImageJ Tiff Image");
	    
	    // iterate over the different tokens stored in description and convert into a map
//	    System.out.println(description);
	    Map<String, String> tokens = parseImageJTokens(description);
        
	    int nImages = 1;
        if (tokens.containsKey("images"))
        {
            nImages = Integer.parseInt(tokens.get("images"));
            System.out.println(String.format("Should read %d images", nImages));
        }

        // determine the size along each of the five dimensions
        int sizeX = info.width;
        int sizeY = info.height;
        int sizeC = getIntValue(tokens, "channels", 1);
        int sizeZ = getIntValue(tokens, "slices", 1);
        int sizeT = getIntValue(tokens, "frames", 1);
        if (sizeC * sizeZ * sizeT != nImages)
        {
            throw new RuntimeException(String.format(
                    "Number of images (%d) does not match image dimensions (%dx%dx%d)", nImages,
                    sizeZ, sizeC, sizeT));
        }

        Array<?> data;
	    if (nImages == 1)
	    {
	        // Read image data
	        data = readImageData(info);
	    }
	    else
	    {
	        System.out.println("read hyperstack data");
            
	        // Use try-with-resource, closing the reader at the end of the try block
            try (ImageBinaryDataReader imageReader = new ImageBinaryDataReader(
                    new File(this.filePath), this.byteOrder))
	        {
	            imageReader.seek(info.stripOffsets[0]);

	            switch(info.pixelType)
	            {
	            case GRAY8:
	            case COLOR8:
	                data = imageReader.readUInt8Array3D(sizeX, sizeY, nImages);
	                break;

	            case BITMAP:
	                throw new RuntimeException("Reading Bitmap Tiff files not supported");
	            
	            case GRAY16_UNSIGNED:
	            case GRAY12_UNSIGNED:
	                data = imageReader.readUInt16Array3D(sizeX, sizeY, nImages);
	                break;
	                
	            case GRAY32_INT:
	                data = imageReader.readInt32Array3D(sizeX, sizeY, nImages);
	                break;
	            
	            case GRAY32_FLOAT:
	                data = imageReader.readFloat32Array3D(sizeX, sizeY, nImages);
	                break;

	            case RGB:
	            case BGR:
	            case ARGB:
	            case ABGR:
	            case BARG:
	            case RGB_PLANAR:
	                
	            case RGB48:

	            default:
	                throw new IOException("Can not read stack with data type " + info.pixelType);
	            }
	        }
	        catch(IOException ex)
	        {
	            throw(ex);
	        }
	    }

	    // number of dimensions of final array
	    int nd = 2;
	    if (sizeZ > 1) nd++;
	    if (sizeC > 1) nd++;
	    if (sizeT > 1) nd++;
	    
	    // initialize dimensions and calibration
	    int[] dims = new int[nd];
	    ImageAxis[] axes = new ImageAxis[nd];
        
	    // Initialize mandatory X and Y axes
	    dims[0] = sizeX;
        double xOrigin = getDoubleValue(tokens, "xorigin", 0.0);
        String unitName = getToken(tokens, "unit", "");
        axes[0] = new ImageAxis.X(info.pixelWidth, xOrigin, unitName);
        dims[1] = sizeY;
        double yOrigin = getDoubleValue(tokens, "yorigin", 0.0);
        String yUnitName = getToken(tokens, "yunit", unitName);
        axes[1] = new ImageAxis.Y(info.pixelHeight, yOrigin, yUnitName);
        
        // Initialize optional C, Z and T axes
        int d = 2;
        if (sizeC > 1)
        {
            dims[d] = sizeC;
            axes[d++] = new ImageAxis("Channel", Axis.Type.CHANNEL, 1, 0, "");
        }
        if (sizeZ > 1)
        {
            dims[d] = sizeZ;
            double spacing = getDoubleValue(tokens, "spacing", 1.0);
            double origin = getDoubleValue(tokens, "zorigin", 0.0);
            String zUnitName = getToken(tokens, "zunit", unitName);
            axes[d++] = new ImageAxis.Z(spacing, origin, zUnitName);
        }
        if (sizeT > 1)
        {
            dims[d] = sizeT;
            double timeStep = getDoubleValue(tokens, "finterval", 1.0);
            String tUnitName = getToken(tokens, "tunit", "sec");
            axes[d++] = new ImageAxis.T(timeStep, 0, tUnitName);
        }
        
        // Additional ImageJ tokens are not managed:
        // info
        // fps
        // loop 
        // mode -> {color} 
        // hyperstack -> boolean
        
        // reshape data array if necessary
        if (sizeC > 1 || sizeT > 1)
        {
            data = new Reshape(dims).process(data);
        }
      
         // Create new Image
        Image image = new Image(data);
        image.setCalibration(new Calibration(axes));
        
        // calibrate display range if information exists
        if (tokens.containsKey("min") && tokens.containsKey("max"))
        {
            double min = getDoubleValue(tokens, "min", 0.0);
            double max = getDoubleValue(tokens, "max", 1.0);
            image.getDisplaySettings().setDisplayRange(new double[] {min, max});
        }
        
        image.tiffTags = info.tags;
	    
	    // setup LUT
	    if (info.lut != null)
	    {
	        image.getDisplaySettings().setColorMap(new DefaultColorMap(info.lut));
	    }
	    
        // setup the file related to the image
		image.setNameFromFileName(filePath);
        image.setFilePath(this.filePath);
        
	    return image;
	}
	
	private Map<String, String> parseImageJTokens(String description)
	{
	    // iterate over the different tokens stored in description and convert into a map
	    Map<String, String> imagejTokens = new HashMap<>();
	    String[] items = description.split("\n");
        for (String item : items)
        {
            // split key and value, separated by "="
            String[] tokens = item.split("=");
            if (tokens.length < 2)
            {
                continue;
            }
            imagejTokens.put(tokens[0], tokens[1]);
        }
        
        return imagejTokens;
	}
	
    private String getToken(Map<String, String> tokens, String tokenName, String defaultValue)
    {
        if (tokens.containsKey(tokenName))
        {
            return tokens.get(tokenName);
        }
        return defaultValue;
    }
    
    private int getIntValue(Map<String, String> tokens, String tokenName, int defaultValue)
    {
        if (tokens.containsKey(tokenName))
        {
            return Integer.parseInt(tokens.get(tokenName));
        }
        return defaultValue;
    }
    
    private double getDoubleValue(Map<String, String> tokens, String tokenName, double defaultValue)
    {
        if (tokens.containsKey(tokenName))
        {
            return Double.parseDouble(tokens.get(tokenName));
        }
        return defaultValue;
    }
    
	/**
     * The function called by the "readImage()" method, that reads the image
     * data and returns either an instance of Array2D or Array3D.
     * 
     * @see #isStackImage()
     * 
     * @return an instance of Array (2D or 3D) containing image data.
     * @throws IOException
     *             if an I/O error occurred.
     */
    private Array<?> readImageData() throws IOException
    {
        // Read image data
        if (isStackImage())
        {
            // Read all images and return a 3D array
            return readImageStack();
        }
        else
        {
            // Read File information of the first image stored in the file
            TiffFileInfo info0 = this.fileInfoList.get(0);
            return readImageData(info0);
        }
    }
    
    /**
     * @return true if the list of FileInfo stored within this reader can be
     *         seen as a 3D stack
     */
    private boolean isStackImage()
    {
        // single image is not stack by definition
        if (fileInfoList.size() == 1)
        {
            return false;
        }
        
        // Read File information of the first image stored in the file
        TiffFileInfo info0 = fileInfoList.iterator().next();
        
        // If file contains several images, check if we should read a stack
        // Condition: all images must have same size
        // TODO: detect multi-channel images
        for (TiffFileInfo info : fileInfoList)
        {
            if (info.width != info0.width || info.height != info0.height)
            {
                return false;
            }
            //		    if (!info.hasSameTags(info0))
            //		    {
            //		        return false;
            //		    }
        }
        
        // if all items declare the same size, we can load a stack
        return true;
    }
    
    private void setupSpatialCalibration(Image image, TiffFileInfo info)
	{
	    String unit = info.unit;
	    int nd = image.getDimension();
	    ImageAxis[] axes = new ImageAxis[nd];
	    axes[0] = new ImageAxis.X(info.pixelWidth, 0, unit);
	    axes[1] = new ImageAxis.Y(info.pixelHeight, 0, unit);
	    if (axes.length > 2)
	    {
	        axes[2] = new ImageAxis.Z();
	    }
	    
	    image.setCalibration(new Calibration(axes));
	}
	
    /**
     * Closes the stream open by this ImageReader.
     * 
     * @throws IOException
     *             if an error occurs
     */
	public void close() throws IOException
	{
		this.dataReader.close();
	}

	/**
     * Returns the set of image file directories stored within this TIFF File.
     * 
     * @return the collection of TiffFileInfo stored within this file.
     */
    public Collection<TiffFileInfo> getImageFileDirectories()
    {
        return this.fileInfoList;
    }

	/**
	 * Reads the set of image file directories within this TIFF File.
     * 
     * @return the collection of TiffFileInfo stored within this file.
     * @throws IOException
     *             if an error occurs
	 */
	private ArrayList<TiffFileInfo> readImageFileDirectories()
			throws IOException
	{
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

		if (infoList.size() == 0)
		{
			throw new RuntimeException("Could not read any image info in file");
		}
		
		return infoList;
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

	/**
     * Reads all the image into this file as a single 3D array. All images must
     * have the same dimensions.
     * 
     * @return an instance of Array3D containing all image data within the file.
     * @throws IOException
     *             if an error occurs.
     */
	public Array3D<?> readImageStack()
			throws IOException
	{
		// Compute image size
		TiffFileInfo info0 = this.fileInfoList.get(0);
		int sizeX = info0.width;
		int sizeY = info0.height;
		int sizeZ = this.fileInfoList.size();
		
		// Compute size of buffer buffer for each plane
		int pixelsPerPlane = sizeX * sizeY;
		int bytesPerPlane  = pixelsPerPlane * info0.pixelType.getByteNumber();
		
		// compute total number of expected bytes
		int nBytes = bytesPerPlane * sizeZ;
		
        // when number of bytes in larger than Integer.MAXINT, creates a new
        // instance of SlicedUInt8Array3D
		if (nBytes < 0)
		{
		    System.out.println("Large array! Switch to sliced array...");
		    // check type limit
		    if(info0.pixelType != TiffFileInfo.PixelType.GRAY8) 
		    {
		        throw new RuntimeException("Can only process UInt8 arrays");
		    }
		    
		    // create the container
		    ArrayList<UInt8Array> arrayList = new ArrayList<>(sizeZ);
		    
		    // iterate over slices to create each 2D array
		    for (TiffFileInfo info : this.fileInfoList)
		    {
	            byte[] buffer = new byte[bytesPerPlane];
	            int nRead = readByteArray(buffer, info);

	            // Check the whole buffer has been read
	            if (nRead != bytesPerPlane)
	            {
	                throw new IOException("Could read only " + nRead
	                        + " bytes over the " + bytesPerPlane + " expected");
	            }
	            
	            arrayList.add(new BufferedUInt8Array2D(sizeX, sizeY, buffer));
		    }
		    
		    // create a new instance of 3D array that stores each slice
		    System.out.println("create 3D array");
            return new SlicedUInt8Array3D(arrayList);
		}
		
		// Allocate buffer array
		byte[] buffer = new byte[nBytes];
		
		// Read the byte array
		int offset = 0;
		int nRead = 0;
		for (TiffFileInfo info : this.fileInfoList)
		{
			nRead += readByteArray(buffer, info, offset);
			offset += bytesPerPlane;
		}

        // Check the whole buffer has been read
		if (nRead != nBytes)
		{
			throw new IOException("Could read only " + nRead
					+ " bytes over the " + nBytes + " expected");
		}
        
		// Transform raw buffer into interpreted buffer
		switch (info0.pixelType) {
		case GRAY8:
		case COLOR8:
			return new BufferedUInt8Array3D(sizeX, sizeY, sizeZ, buffer);
		case GRAY16_UNSIGNED:
		case GRAY12_UNSIGNED:
		{
			// Store data as short array
		    short[] shortBuffer = convertToShortArray(buffer, this.byteOrder);
			return new BufferedUInt16Array3D(sizeX, sizeY, sizeZ, shortBuffer);
		}	

		case GRAY32_FLOAT:
		{
		    float[] floatBuffer = convertToFloatArray(buffer, this.byteOrder);
		    return new BufferedFloat32Array3D(sizeX, sizeY, sizeZ, floatBuffer);
		}

		case BITMAP:
            throw new RuntimeException("Reading Bitmap Tiff files not supported");
            
		default:
			throw new IOException("Can not read stack with data type "
					+ info0.pixelType);
		}
	}
	
    /**
     * Reads the image data for the specified IFD (Image File Directory) index.
     * 
     * @param index
     *            the index of image data to read
     * @return the data array corresponding to the specified index
     * @throws IOException
     *             if an error occurs
     */
    public Array<?> readImageData(int index) throws IOException
    {
        // check validity of index input
        if (index >= this.fileInfoList.size())
        {
            throw new IllegalArgumentException("Requires an index below the number of images ("
                    + this.fileInfoList.size() + ")");
        }
        
        TiffFileInfo info = this.fileInfoList.get(index);
        return readImageData(info);
    }
    
	/**
     * Reads the buffer from the current stream and specified info.
     * 
     * @param info
     *            an instance of TiffFileInfo
     * @return the data array corresponding to the specified TiffFileInfo
     * @throws IOException
     *             if an error occurs
     */
	public Array<?> readImageData(TiffFileInfo info) throws IOException
	{
		// Size of image
		int nPixels = info.width * info.height;

		// size of buffer
		int nBytes = nPixels * info.pixelType.getByteNumber();

		// Read the byte array
		byte[] buffer = new byte[nBytes];
		int nRead = readByteArray(buffer, info);

		// Check all buffer have been read
		if (nRead != nBytes)
		{
			throw new IOException("Could read only " + nRead
					+ " bytes over the " + nBytes + " expected");
		}

		
		// Transform raw buffer into interpreted buffer
		switch (info.pixelType) {
		case GRAY8:
		case COLOR8:
		{
			// Case of data coded with 8 bits
			return new BufferedUInt8Array2D(info.width, info.height, buffer);
		}

		case BITMAP:
            throw new RuntimeException("Reading Bitmap Tiff files not supported");
        
		case GRAY16_UNSIGNED:
		case GRAY12_UNSIGNED:
		{
			// Store data as short array
			short[] shortBuffer = convertToShortArray(buffer, this.byteOrder);
			return new BufferedUInt16Array2D(info.width, info.height, shortBuffer);
		}	

		case GRAY32_INT:
		{
			// Store data as int array
			int[] intBuffer = convertToIntArray(buffer, this.byteOrder);
            return new BufferedInt32Array2D(info.width, info.height, intBuffer);
		}	
		
		case GRAY32_FLOAT:
		{
			// Store data as short array
			float[] floatBuffer = convertToFloatArray(buffer, this.byteOrder);
			return new BufferedFloat32Array2D(info.width, info.height, floatBuffer);
		}	

		case RGB:
		case BGR:
		case ARGB:
		case ABGR:
		case BARG:
		case RGB_PLANAR:
		{
			// allocate memory for array
			RGB8Array2D rgb2d = new BufferedPackedByteRGB8Array2D(info.width, info.height);
			
			// fill array with re-ordered buffer content
			int index = 0;
			for (int y = 0; y < info.height; y++)
			{
				for (int x = 0; x < info.width; x++)
				{
					int r = buffer[index++] & 0x00FF;
					int g = buffer[index++] & 0x00FF;
					int b = buffer[index++] & 0x00FF;
					rgb2d.set(new RGB8(r, g, b), x, y);
				}
			}
			return rgb2d;
		}
		
		case RGB48:
		{
		    RGB16Array2D rgb2d = new BufferedPackedShortRGB16Array2D(info.width, info.height);
		    
		    ByteOrder order = this.byteOrder;
		    // fill array with re-ordered buffer content
		    int index = 0;
		    for (int y = 0; y < info.height; y++)
		    {
		        for (int x = 0; x < info.width; x++)
		        {
                    int r = convertBytesToShort(buffer[index++], buffer[index++], order) & 0x00FFFF;
                    int g = convertBytesToShort(buffer[index++], buffer[index++], order) & 0x00FFFF;
                    int b = convertBytesToShort(buffer[index++], buffer[index++], order) & 0x00FFFF;
		            rgb2d.set(new RGB16(r, g, b), x, y);
		        }
		    }
		    return rgb2d;
		}

		default:
			throw new IOException("Can not process file info of type " + info.pixelType);
		}
	}
	
	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArray(byte[] buffer, TiffFileInfo info)
			throws IOException
	{
		switch (info.compression) {
		case NONE:
			return readByteArrayUncompressed(buffer, info);
		case PACK_BITS:
			return readByteArrayPackBits(buffer, info);
		default:
			throw new RuntimeException("Unsupported compression mode: "
					+ info.compression);
		}
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArray(byte[] buffer, TiffFileInfo info, int offset)
			throws IOException
	{
		switch (info.compression) {
		case NONE:
			return readByteArrayUncompressed(buffer, info, offset);
		case PACK_BITS:
			return readByteArrayPackBits(buffer, info, offset);
		default:
			throw new RuntimeException("Unsupported compression mode: "
					+ info.compression);
		}
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArrayUncompressed(byte[] buffer, TiffFileInfo info)
			throws IOException
	{
		int totalRead = 0;
		int offset = 0;

		// read each strip successively
		int nStrips = info.stripOffsets.length;
		for (int i = 0; i < nStrips; i++)
		{
			dataReader.seek(info.stripOffsets[i] & 0xffffffffL);
			int nRead = dataReader.readByteArray(buffer, offset, info.stripLengths[i]);
			offset += nRead;
			totalRead += nRead;
		}

		return totalRead;
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArrayUncompressed(byte[] buffer, TiffFileInfo info,
			int offset) throws IOException
	{
		int totalRead = 0;

		// read each strip successively
		int nStrips = info.stripOffsets.length;
		for (int i = 0; i < nStrips; i++)
		{
			dataReader.seek(info.stripOffsets[i] & 0xffffffffL);
			int nRead = dataReader.readByteArray(buffer, offset, info.stripLengths[i]);
			offset += nRead;
			totalRead += nRead;
		}

		return totalRead;
	}

	private int readByteArrayPackBits(byte[] buffer, TiffFileInfo info)
			throws IOException
	{
		// Number of strips
		int nStrips = info.stripOffsets.length;

		// Compute the number of bytes per strip
		int nBytes = 0;
		for (int i = 0; i < nStrips; i++)
			nBytes += info.stripLengths[i];
		byte[] compressedBytes = new byte[nBytes];

		// read each compressed strip
		int offset = 0;
		for (int i = 0; i < nStrips; i++)
		{
			dataReader.seek(info.stripOffsets[i] & 0xffffffffL);
            int nRead = dataReader.readByteArray(compressedBytes, offset, info.stripLengths[i]);
			offset += nRead;
		}

		int nRead = PackBits.uncompressPackBits(compressedBytes, buffer);
		return nRead;
	}

    private int readByteArrayPackBits(byte[] buffer, TiffFileInfo info, int offset)
            throws IOException
	{
		// Number of strips
		int nStrips = info.stripOffsets.length;

		// Compute the number of bytes per strip
		int nBytes = 0;
		for (int i = 0; i < nStrips; i++)
		{
			nBytes += info.stripLengths[i];
		}
		byte[] compressedBytes = new byte[nBytes];

		// read each compressed strip
		int offset0 = 0;
		for (int i = 0; i < nStrips; i++)
		{
			dataReader.seek(info.stripOffsets[i] & 0xffffffffL);
            int nRead = dataReader.readByteArray(compressedBytes, offset0, info.stripLengths[i]);
			offset0 += nRead;
		}

		int nRead = PackBits.uncompressPackBits(compressedBytes, buffer, offset);
		return nRead;
	}
    
    private final static short[] convertToShortArray(byte[] byteBuffer, ByteOrder order)
    {
        // Store data as short array
        int nPixels = byteBuffer.length / 2;
        short[] shortBuffer = new short[nPixels];
        
        // convert byte array into short array
        for (int i = 0; i < nPixels; i++)
        {
            int b1 = byteBuffer[2 * i] & 0x00FF;
            int b2 = byteBuffer[2 * i + 1] & 0x00FF;

            // encode bytes to short
            if (order == ByteOrder.LITTLE_ENDIAN)
                shortBuffer[i] = (short) ((b2 << 8) | b1);
            else
                shortBuffer[i] = (short) ((b1 << 8) | b2);
        }
        
        return shortBuffer;
    }

    private final static int[] convertToIntArray(byte[] byteBuffer, ByteOrder order)
    {
        // Store data as short array
        int nPixels = byteBuffer.length / 4;
        int[] intBuffer = new int[nPixels];
        
        // convert byte array into int array
        for (int i = 0; i < nPixels; i++)
        {
            int b1 = byteBuffer[4 * i + 0] & 0x00FF;
            int b2 = byteBuffer[4 * i + 1] & 0x00FF;
            int b3 = byteBuffer[4 * i + 2] & 0x00FF;
            int b4 = byteBuffer[4 * i + 3] & 0x00FF;

            // encode bytes to short
            if (order == ByteOrder.LITTLE_ENDIAN)
                intBuffer[i] = (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
            else
                intBuffer[i] = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
        }
        
        return intBuffer;
    }

    private final static float[] convertToFloatArray(byte[] byteBuffer, ByteOrder order)
    {
        // Store data as short array
        int nFloats = byteBuffer.length / 4;
        float[] floatBuffer = new float[nFloats];
        
        // convert byte array into float array
        for (int i = 0; i < nFloats; i++)
        {
            int b1 = byteBuffer[4 * i + 0] & 0x00FF;
            int b2 = byteBuffer[4 * i + 1] & 0x00FF;
            int b3 = byteBuffer[4 * i + 2] & 0x00FF;
            int b4 = byteBuffer[4 * i + 3] & 0x00FF;

            // encode bytes to float
            if (order == ByteOrder.LITTLE_ENDIAN)
                floatBuffer[i] = Float.intBitsToFloat((b4 << 24) | (b3 << 16) | (b2 << 8) | b1);
            else
                floatBuffer[i] = Float.intBitsToFloat((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);
        }
        
        return floatBuffer;
    }

    private static final short convertBytesToShort(byte b1, byte b2, ByteOrder order)
    {
        int v1 = b1 & 0x00FF;
        int v2 = b2 & 0x00FF;
        
        // encode bytes to short
        if (order == ByteOrder.LITTLE_ENDIAN)
            return (short) ((v2 << 8) | v1);
        else
            return (short) ((v1 << 8) | v2);
    }

}
