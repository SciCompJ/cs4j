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
import net.sci.array.color.DefaultColorMap;
import net.sci.array.numeric.impl.FileMappedFloat32Array3D;
import net.sci.array.numeric.impl.FileMappedUInt16Array3D;
import net.sci.array.numeric.impl.FileMappedUInt8Array3D;
import net.sci.array.process.shape.Reshape;
import net.sci.axis.Axis;
import net.sci.image.Calibration;
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
	// Management of Image File directories
	
	/**
     * Returns the set of image file directories stored within this TIFF File.
     * 
     * @return the collection of TiffFileInfo stored within this file.
     */
    public Collection<TiffFileInfo> getImageFileDirectories()
    {
        return this.fileInfoList;
    }
    
    
	// =============================================================
    // Methods for reading images
    
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
        
    	// Check if the file was saved by ImageJ software
    	// in that case, use specific processing
    	if (hasImageJDescription(info))
        {
    	    System.out.println("Found ImageJ description, use special processing");
    	    return readImageJImage(info, false);
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
        
        // Check if the file was saved by ImageJ software
        if (hasImageJDescription(info))
        {
            System.out.println("Found ImageJ description, use special processing");
            return readImageJImage(info, true);
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
        Array<?> data = createFileMappedArray(info, this.fileInfoList.size());
        
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
    
    
    // =============================================================
    // Management of Images saved by the ImageJ software

    private boolean hasImageJDescription(TiffFileInfo info)
    {
        // Get the  description tag, or null if not initialized
        TiffTag tag = info.tags.get(BaselineTags.ImageDescription.CODE);
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
        
        return ImageJTokens.parse(description).hasToken("images");
    }

    /**
     * If the description tag has a content beginning with "ImageJ", this
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
    private Image readImageJImage(TiffFileInfo info, boolean virtual) throws IOException
    {
        // Get the description tag, or null if not initialized
        TiffTag tag = info.tags.get(BaselineTags.ImageDescription.CODE);
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

        // iterate over the different tokens stored in description and convert into a map
        ImageJTokens tokens = ImageJTokens.parse(description);

        // retrieve number of images within file
        int nImages = tokens.getIntValue("images", 1);

        // determine the size along each of the five dimensions
        int sizeX = info.width;
        int sizeY = info.height;
        int sizeC = tokens.getIntValue("channels", 1);
        int sizeZ = tokens.getIntValue("slices", 1);
        int sizeT = tokens.getIntValue("frames", 1);

        // The number of slices is sometimes replaced by image count
        if (sizeC * sizeZ * sizeT == 1 && nImages > 1)
        {
            sizeZ = nImages;
        }

        // check consistency of parameters
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
        else if (!virtual)
        {
            // Read the totality of image data as a 3D array stored in memory
            data = readImage3DData(info, nImages);
        }
        else
        {
            // Read a virtual image by creating a file-mapped array 
            data = createFileMappedArray(info, nImages);
        }

        // number of dimensions of final array
        int nd = 2 + (sizeZ > 1 ? 1 : 0) + (sizeC > 1 ? 1 : 0) + (sizeT > 1 ? 1 : 0);
        int[] dims = new int[nd];

        // initialize dimensions array
        dims[0] = sizeX;
        dims[1] = sizeY;
        int d = 2;
        if (sizeC > 1) dims[d++] = sizeC;
        if (sizeZ > 1) dims[d++] = sizeZ;
        if (sizeT > 1) dims[d++] = sizeT;

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
        image.setCalibration(tokens.createCalibration(info, sizeC, sizeZ, sizeT));

        // calibrate display range if information exists
        if (tokens.hasToken("min") && tokens.hasToken("max"))
        {
            double min = tokens.getDoubleValue("min", 0.0);
            double max = tokens.getDoubleValue("max", 1.0);
            image.getDisplaySettings().setDisplayRange(new double[] {min, max});
        }
        
        // setup LUT
        if (info.lut != null)
        {
            image.getDisplaySettings().setColorMap(new DefaultColorMap(info.lut));
        }

        // setup the file related to the image
        image.setNameFromFileName(filePath);
        image.setFilePath(this.filePath);

        // keep the tiff tags within Image
        image.tiffTags = info.tags;

        return image;
    }

    
    // =============================================================
    // Methods for reading image data
    
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
        for (TiffFileInfo info : fileInfoList)
        {
            if (info.width != info0.width || info.height != info0.height)
            {
                return false;
            }
        }
        
        // if all items declare the same size, we can load as 3D Image
        return true;
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
     * Reads the whole image data array from the chosen file, and the specified info.
     * 
     * @param info
     *            an instance of TiffFileInfo
     * @param nImages
     *            the number of images to read (product of channel, slice, and
     *            frame counts)
     * @return the data array corresponding to the specified TiffFileInfo
     * @throws IOException
     *             if an error occurs
     */
    private Array<?> readImage3DData(TiffFileInfo info, int nImages) throws IOException
    {
        // Use try-with-resource, closing the reader at the end of the try block
        try (ImageBinaryDataReader reader = new ImageBinaryDataReader(new File(this.filePath), info.byteOrder))
        {
            // Catch algorithm events of the data reader and propagate them to the TiffImageReader listeners
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

            return switch(info.pixelType)
            {
                case GRAY8, COLOR8 
                    -> reader.readUInt8Array3D(info.width, info.height, nImages);
                case BITMAP 
                    -> throw new RuntimeException("Reading Bitmap Tiff files not supported");
                case GRAY16_UNSIGNED, GRAY12_UNSIGNED 
                    -> reader.readUInt16Array3D(info.width, info.height, nImages);
                case GRAY32_INT 
                    -> reader.readInt32Array3D(info.width, info.height, nImages);
                case GRAY32_FLOAT 
                    -> reader.readFloat32Array3D(info.width, info.height, nImages);
                case RGB, BGR, ARGB, ABGR, BARG, RGB_PLANAR, RGB48 
                    -> throw new IOException("Can not read stack with data type " + info.pixelType);
                default -> throw new IOException("Can not read stack with data type " + info.pixelType);
            };
        }
        catch(IOException ex)
        {
            throw(ex);
        }
    }
    
    private Array<?> createFileMappedArray(TiffFileInfo info, int nImages) throws IOException
    {
        return switch (info.pixelType)
        {
            case GRAY8, COLOR8 -> new FileMappedUInt8Array3D(this.filePath, info.stripOffsets[0], info.width,
                    info.height, nImages);
            case GRAY32_FLOAT -> new FileMappedFloat32Array3D(this.filePath, info.stripOffsets[0], info.width,
                    info.height, nImages);
            case GRAY16_UNSIGNED -> new FileMappedUInt16Array3D(this.filePath, info.stripOffsets[0], info.width,
                    info.height, nImages);
            case BITMAP, GRAY12_UNSIGNED, GRAY32_INT -> throw new IOException(
                    "Virtual images not supported for data type: " + info.pixelType);
            case RGB, BGR, ARGB, ABGR, BARG, RGB_PLANAR, RGB48 -> throw new IOException(
                    "Virtual images not supported for color type: " + info.pixelType);
        
            default -> throw new IOException("Can not read stack with data type " + info.pixelType);
        };
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

    private static void setupSpatialCalibration(Image image, TiffFileInfo info)
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
     * Encapsulates the management of ImageJ tokens within a private class.
     * 
     * The ImageJTokens class works similarly as a Map of String to String, and
     * provides additional methods for converting tokens into integer or double
     * values.
     */
    private static class ImageJTokens
    {
        Map<String, String> tokens;
        
        public static ImageJTokens parse(String description)
        {
            Map<String, String> imagejTokens = new HashMap<>();
            
            // iterate over the different tokens stored in description and convert into a map
            for (String item : description.split("\n"))
            {
                // split key and value, separated by "="
                String[] tokens = item.split("=");
                if (tokens.length < 2)
                {
                    continue;
                }
                imagejTokens.put(tokens[0].trim(), tokens[1].trim());
            }
            
            return new ImageJTokens(imagejTokens);
        }
        
        private ImageJTokens(Map<String, String> tokens)
        {
            this.tokens = tokens;
        }
        
        public Calibration createCalibration(TiffFileInfo info, int sizeC, int sizeZ, int sizeT)
        {
            ArrayList<ImageAxis> axes = new ArrayList<ImageAxis>(5);

            // Initialize mandatory X and Y axes
            axes.add(createXAxis(info.pixelWidth, ""));
            axes.add(createYAxis(info.pixelHeight, axes.get(0).getUnitName()));

            // Initialize optional C, Z and T axes
            if (sizeC > 1) axes.add(new ImageAxis("Channel", Axis.Type.CHANNEL, 1, 0, ""));
            if (sizeZ > 1) axes.add(createZAxis(info.pixelHeight, axes.get(0).getUnitName()));
            if (sizeT > 1) axes.add(createTAxis(1.0, "sec"));
            
            return new Calibration(axes.toArray(new ImageAxis[] {}));
        }
        
        public ImageAxis createXAxis(double pixelWidth, String defaultUnitName)
        {
            double origin = getDoubleValue("xorigin", 0.0);
            String unitName = getToken("unit", defaultUnitName);
            return new ImageAxis.X(pixelWidth, origin, unitName);
        }
        
        public ImageAxis createYAxis(double pixelHeight, String defaultUnitName)
        {
            double origin = getDoubleValue("yorigin", 0.0);
            String unitName = getToken("yunit", defaultUnitName);
            return new ImageAxis.Y(pixelHeight, origin, unitName);
        }
        
        public ImageAxis createZAxis(double spacing, String defaultUnitName)
        {
            double origin = getDoubleValue("zorigin", 0.0);
            String unitName = getToken("zunit", defaultUnitName);
            return new ImageAxis.Z(spacing, origin, unitName);
        }
        
        public ImageAxis createTAxis(double spacing, String defaultUnitName)
        {
            double timeStep = getDoubleValue("finterval", 1.0);
            String tUnitName = getToken("tunit", "sec");
            return new ImageAxis.T(timeStep, 0, tUnitName);
        }
        
        public boolean hasToken(String tokenName)
        {
            return this.tokens.containsKey(tokenName);
        }
        
        public String getToken(String tokenName, String defaultValue)
        {
            if (tokens.containsKey(tokenName))
            {
                return tokens.get(tokenName);
            }
            return defaultValue;
        }
        
        public int getIntValue(String tokenName, int defaultValue)
        {
            if (tokens.containsKey(tokenName))
            {
                return Integer.parseInt(tokens.get(tokenName));
            }
            return defaultValue;
        }
        
        public double getDoubleValue(String tokenName, double defaultValue)
        {
            if (tokens.containsKey(tokenName))
            {
                return Double.parseDouble(tokens.get(tokenName));
            }
            return defaultValue;
        }
    }
}
