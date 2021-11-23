/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.process.shape.Reshape;
import net.sci.array.scalar.FileMappedUInt8Array3D;
import net.sci.axis.Axis;
import net.sci.image.Calibration;
import net.sci.image.DefaultColorMap;
import net.sci.image.Image;
import net.sci.image.ImageAxis;
import net.sci.image.io.tiff.BaselineTags;
import net.sci.image.io.tiff.TiffFileInfo;
import net.sci.image.io.tiff.TiffFileInfoReader;
import net.sci.image.io.tiff.TiffImageDataReader;
import net.sci.image.io.tiff.TiffTag;

/**
 * Provides methods for reading Image files in TIFF Format. Relies on the classes
 * TiffFileInfoReader (for reading Tiff File Infos) and TiffImageDataReader
 * (for reading image data).
 * 
 * @see net.sci.image.io.tiff.TiffFileInfoReader
 * @see net.sci.image.io.tiff.TiffImageDataReader
 * 
 * @author David Legland
 *
 */
public class TiffImageReader extends AlgoStub implements ImageReader
{
	// =============================================================
	// Class variables
	
    /**
     * The name of the file to read the data from.
     * Initialized at construction.
     */
    String filePath;

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
		this.fileInfoList = new TiffFileInfoReader(this.filePath).readImageFileDirectories();
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
        TiffImageDataReader reader = createImageDataReader();
        Array<?> data = reader.readImageData(fileInfo);

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
		    System.out.println("read ImageJ Tiff Image");
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

    /*
     * (non-Javadoc)
     * 
     * @see jipl.io.ImageReader#readImage()
     */
    public Image readVirtualImage3D() throws IOException
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
            return readImageJVirtualImage(info);
        }
        
        if (info.pixelType != TiffFileInfo.PixelType.GRAY8)
        {
            throw new RuntimeException("Virtual stacks are available only for UInt8 arrays.");
        }
        if (info.compression != TiffFileInfo.Compression.NONE)
        {
            throw new RuntimeException("Virtual stacks not implemented for TIFF files compressed with mode " + info.compression);
        }
        
       
        // Read (virtual) image data
        System.out.println("create file-mapped uint8 array");
        Array<?> data = new FileMappedUInt8Array3D(this.filePath, info.stripOffsets[0], info.width, info.height, this.fileInfoList.size());

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
        return imagejTokens.containsKey("images");
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
            try (ImageBinaryDataReader reader = new ImageBinaryDataReader(
                    new File(this.filePath), info.byteOrder))
	        {
                reader.addAlgoListener(new AlgoListener()
                {
                    @Override
                    public void algoProgressChanged(AlgoEvent evt)
                    {
                        TiffImageReader.this.fireProgressChanged(reader, evt.getCurrentProgress(), evt.getTotalProgress());
                    }

                    @Override
                    public void algoStatusChanged(AlgoEvent evt)
                    {
                        TiffImageReader.this.fireStatusChanged(reader, evt.getStatus());
                    }
                });
                
                
	            reader.seek(info.stripOffsets[0]);

	            switch(info.pixelType)
	            {
	            case GRAY8:
	            case COLOR8:
	                data = reader.readUInt8Array3D(sizeX, sizeY, nImages);
	                break;

	            case BITMAP:
	                throw new RuntimeException("Reading Bitmap Tiff files not supported");
	            
	            case GRAY16_UNSIGNED:
	            case GRAY12_UNSIGNED:
	                data = reader.readUInt16Array3D(sizeX, sizeY, nImages);
	                break;
	                
	            case GRAY32_INT:
	                data = reader.readInt32Array3D(sizeX, sizeY, nImages);
	                break;
	            
	            case GRAY32_FLOAT:
	                data = reader.readFloat32Array3D(sizeX, sizeY, nImages);
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
	
    /**
     * If one of the Tiff Tags has a description beginning with "ImageJ", this
     * method use a specific process to extract image data as saved by ImageJ.
     * 
     * @param info
     *            The set of Tiff Tags of the image
     * @return the Image stored within the file
     * @throws IOException
     *             if an I/O error occurred
     */
    private Image readImageJVirtualImage(TiffFileInfo info) throws IOException
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
//      System.out.println(description);
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
            System.out.println("read virtual hyperstack data");
            
            // Use try-with-resource, closing the reader at the end of the try block
//            try (ImageBinaryDataReader imageReader = new ImageBinaryDataReader(
//                    new File(this.filePath), this.byteOrder))
//            {
//                imageReader.seek(info.stripOffsets[0]);

                switch(info.pixelType)
                {
                case GRAY8:
                case COLOR8:
                    System.out.println("create file-mapped uint8 array");
                    data = new FileMappedUInt8Array3D(this.filePath, info.stripOffsets[0], sizeX, sizeY, nImages);
                    
//                    data = imageReader.readUInt8Array3D(sizeX, sizeY, nImages);
                    break;

//                case BITMAP:
//                    throw new RuntimeException("Reading Bitmap Tiff files not supported");
//                
//                case GRAY16_UNSIGNED:
//                case GRAY12_UNSIGNED:
//                    data = imageReader.readUInt16Array3D(sizeX, sizeY, nImages);
//                    break;
//                    
//                case GRAY32_INT:
//                    data = imageReader.readInt32Array3D(sizeX, sizeY, nImages);
//                    break;
//                
//                case GRAY32_FLOAT:
//                    data = imageReader.readFloat32Array3D(sizeX, sizeY, nImages);
//                    break;

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
//            }
//            catch(IOException ex)
//            {
//                throw(ex);
//            }
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
        TiffImageDataReader reader = createImageDataReader();

        // Read image data
        if (isStackImage())
        {
            // Read all images and return a 3D array
            return reader.readImageStack(this.fileInfoList);
        }
        else
        {
            // Read File information of the first image stored in the file
            TiffFileInfo info0 = this.fileInfoList.get(0);
            return reader.readImageData(info0);
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
     * Returns the set of image file directories stored within this TIFF File.
     * 
     * @return the collection of TiffFileInfo stored within this file.
     */
    public Collection<TiffFileInfo> getImageFileDirectories()
    {
        return this.fileInfoList;
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
        TiffImageDataReader reader = createImageDataReader();

        // Read all images and return a 3D array
        return reader.readImageStack(this.fileInfoList);
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
        
        TiffImageDataReader reader = createImageDataReader();
        return reader.readImageData(info);
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
        TiffImageDataReader reader = createImageDataReader();
        return reader.readImageData(info);
	}
	
	/**
     * Creates a new TiffImageDataReader initialized with the current file path,
     * and adds defaults listener that propagate events to the listener(s) of
     * the TiffImageReader class
     * 
     * @return an initialized instance of TiffImageDataReader
     * @throws IOException
     *             if a problem occurred
     */
	private TiffImageDataReader createImageDataReader() throws IOException
	{
        TiffImageDataReader reader = new TiffImageDataReader(this.filePath);
        
        // add an algo listener that simply propagates the events to the
        // listener(s) of the TiffImageReader class
        reader.addAlgoListener(new AlgoListener()
        {
            @Override
            public void algoProgressChanged(AlgoEvent evt)
            {
                TiffImageReader.this.fireProgressChanged(reader, evt.getCurrentProgress(), evt.getTotalProgress());
            }

            @Override
            public void algoStatusChanged(AlgoEvent evt)
            {
                TiffImageReader.this.fireStatusChanged(reader, evt.getStatus());
            }
        });
        
        return reader;
	}

}
