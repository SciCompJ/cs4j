/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.color.DefaultColorMap;
import net.sci.array.shape.Reshape;
import net.sci.image.Calibration;
import net.sci.image.Image;
import net.sci.image.ImageAxis;
import net.sci.image.io.tiff.BaselineTags;
import net.sci.image.io.tiff.ImageFileDirectory;
import net.sci.image.io.tiff.ImageFileDirectoryReader;
import net.sci.image.io.tiff.TiffImageDataReader;
import net.sci.image.io.tiff.TiffTag;

/**
 * Provides methods for reading Image files in TIFF Format. Relies on the
 * classes ImageFileDirectoryReader (for reading Tiff File Image Directories)
 * and TiffImageDataReader (for reading image data).
 * 
 * @see net.sci.image.io.tiff.ImageFileDirectoryReader
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
     * The file to read the data from. Initialized at construction.
     */
    Path path;
    
    /**
     * The list of file info stored in the TIFF file.
     */
    ArrayList<ImageFileDirectory> fileDirectories;
    
    /**
     * A boolean flag that toggles the display of messages about the reading
     * process. Default is false (no message display).
     */
    public boolean verbose = false;
    
    
    // =============================================================
    // Constructor
    
    public TiffImageReader(String fileName) throws IOException
    {
        this(new File(fileName).toPath());
    }
    
    public TiffImageReader(File file) throws IOException
    {
        this(file.toPath());
    }
    
    public TiffImageReader(Path path) throws IOException
    {
        this.path = path;
        this.fileDirectories = new ImageFileDirectoryReader(this.path.toFile()).readImageFileDirectories();
    }
    
    
    // =============================================================
    // Management of Image File directories
    
    /**
     * Returns the set of image file directories stored within this TIFF File.
     * 
     * @return the collection of TiffFileInfo stored within this file.
     */
    public Collection<ImageFileDirectory> getImageFileDirectories()
    {
        return this.fileDirectories;
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
        if (index >= this.fileDirectories.size())
        {
            throw new IllegalArgumentException(
                    "Requires an index below the number of images (" + this.fileDirectories.size() + ")");
        }
        
        // Read File information of the image stored in the file
        ImageFileDirectory ifd = this.fileDirectories.get(index);
        
        // Read image data
        TiffImageDataReader reader = createImageDataReader(path.toFile(), ifd.getByteOrder());
        Array<?> data = reader.readImageData(ifd);
        
        // Create new Image
        Image image = new Image(data);
        setupImageMetaData(image, ifd);
        
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
        if (this.fileDirectories.size() == 0)
        {
            throw new RuntimeException("Could not read any meta-information from file");
        }
        
        // Read File information of the first image stored in the file
        ImageFileDirectory ifd = this.fileDirectories.get(0);
        
        // Check if the file was saved by ImageJ software
        // in that case, use specific processing
        if (hasImageJDescription(ifd))
        {
            if (verbose)
            {
                System.out.println("Found ImageJ description, use special processing");
            }
            return readImageJImage(ifd, false);
        }
        
        // Read image data
        Array<?> data = readImageData();
        
        // Create new Image
        Image image = new Image(data);
        setupImageMetaData(image, ifd);
        
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
        if (this.fileDirectories.size() == 0)
        {
            throw new RuntimeException("Could not read any meta-information from file");
        }
        
        // Read File information of the first image stored in the file
        ImageFileDirectory ifd = this.fileDirectories.get(0);
        
        // Check if the file was saved by ImageJ software
        if (hasImageJDescription(ifd))
        {
            if (verbose)
            {
                System.out.println("Found ImageJ description, use special processing");
            }
            return readImageJImage(ifd, true);
        }
        
        if (ifd.determinePixelType() != PixelType.UINT8)
        {
            throw new RuntimeException("Virtual stacks are available only for UInt8 arrays.");
        }
        if (ifd.getValue(BaselineTags.Compression.CODE) != 1)
        {
            throw new RuntimeException("Virtual stacks not implemented for TIFF files compressed with mode " + "NONE");
        }
        
        // Read (virtual) image data
        Array<?> data = createFileMappedArray(ifd, this.fileDirectories.size());
        
        // Create new Image
        Image image = new Image(data);
        setupImageMetaData(image, ifd);
        
        return image;
    }
    
    
    // =============================================================
    // Management of Images saved by the ImageJ software

    private boolean hasImageJDescription(ImageFileDirectory info)
    {
        // Get the  description tag, or null if not initialized
        TiffTag tag = info.getEntry(BaselineTags.ImageDescription.CODE);
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
     * @param ifd
     *            The set of Tiff Tags of the image
     * @param description
     *            the content of the tag 270, as a String
     * @return the Image stored within the file
     * @throws IOException
     *             if an I/O error occurred
     */
    private Image readImageJImage(ImageFileDirectory ifd, boolean virtual) throws IOException
    {
        // Get the description tag, or null if not initialized
        TiffTag tag = ifd.getEntry(BaselineTags.ImageDescription.CODE);
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
        int sizeX = ifd.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd.getValue(BaselineTags.ImageHeight.CODE);
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
            data = readImageData(ifd);
        }
        else if (!virtual)
        {
            // Read the totality of image data as a 3D array stored in memory
            data = readImage3DData(ifd, nImages);
        }
        else
        {
            // Read a virtual image by creating a file-mapped array 
            data = createFileMappedArray(ifd, nImages);
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
        
        // setup default calibration from standard tags
        setupImageMetaData(image, ifd);

        // continue calibration setup using ImageJ tokens
        image.setCalibration(tokens.createCalibration(ifd, sizeC, sizeZ, sizeT));

        // calibrate display range if information exists
        if (tokens.hasToken("min") && tokens.hasToken("max"))
        {
            double min = tokens.getDoubleValue("min", 0.0);
            double max = tokens.getDoubleValue("max", 1.0);
            image.getDisplaySettings().setDisplayRange(new double[] {min, max});
        }
        
        return image;
    }

    /**
     * Creates a new hash map of the tags indexed with their key, and add the
     * map to the metadata of the image with the "tiff-tags" key.
     * 
     * @param image
     *            the image to update
     * @param tags
     *            the list of tags
     */
    private static final void addTiffTags(Image image, Collection<TiffTag> tags)
    {
        Map<Integer, TiffTag> map = new TreeMap<Integer, TiffTag>(); 
        for (TiffTag tag : tags)
        {
            map.put(tag.code, tag);
        }
        
        image.metadata.put("tiff-tags", map);
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
    public Array3D<?> readImageStack() throws IOException
	{
        ImageFileDirectory ifd0 = this.fileDirectories.get(0);
        TiffImageDataReader reader = createImageDataReader(path.toFile(), ifd0.getByteOrder());

        // Read all images and return a 3D array
        return reader.readImageStack(this.fileDirectories);
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
        if (index >= this.fileDirectories.size())
        {
            throw new IllegalArgumentException("Requires an index below the number of images ("
                    + this.fileDirectories.size() + ")");
        }
        
        ImageFileDirectory ifd = this.fileDirectories.get(index);
        TiffImageDataReader reader = createImageDataReader(path.toFile(), ifd.getByteOrder());
        return reader.readImageData(ifd);
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
        ImageFileDirectory ifd0 = this.fileDirectories.get(0);
        TiffImageDataReader reader = createImageDataReader(path.toFile(), ifd0.getByteOrder());
    
        // Read image data
        if (isStackImage())
        {
            // Read all images and return a 3D array
            return reader.readImageStack(this.fileDirectories);
        }
        else
        {
            // Read File information of the first image stored in the file
            return reader.readImageData(ifd0);
        }
    }

    /**
     * @return true if the list of FileInfo stored within this reader can be
     *         seen as a 3D stack
     */
    private boolean isStackImage()
    {
        // single image is not stack by definition
        if (fileDirectories.size() == 1)
        {
            return false;
        }
        
        // Read File information of the first image stored in the file
        ImageFileDirectory ifd0 = fileDirectories.iterator().next();
        int refSizeX = ifd0.getValue(BaselineTags.ImageWidth.CODE);
        int refSizeY = ifd0.getValue(BaselineTags.ImageHeight.CODE);
        
        // If file contains several images, check if we should read a stack
        // Condition: all images must have same size
        for (ImageFileDirectory ifd : fileDirectories)
        {
            int sizeX = ifd.getValue(BaselineTags.ImageWidth.CODE);
            int sizeY = ifd.getValue(BaselineTags.ImageHeight.CODE);
            if (sizeX != refSizeX || sizeY != refSizeY)
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
     * @param ifd
     *            an instance of TiffFileInfo
     * @return the data array corresponding to the specified TiffFileInfo
     * @throws IOException
     *             if an error occurs
     */
    public Array<?> readImageData(ImageFileDirectory ifd) throws IOException
    {
        TiffImageDataReader reader = createImageDataReader(path.toFile(), ifd.getByteOrder());
        return reader.readImageData(ifd);
    }
    
    /**
     * Reads the whole image data array from the chosen file, and the specified
     * info.
     * 
     * @param ifd
     *            an instance of TiffFileInfo
     * @param nImages
     *            the number of images to read (product of channel, slice, and
     *            frame counts)
     * @return the data array corresponding to the specified TiffFileInfo
     * @throws IOException
     *             if an error occurs
     */
    private Array<?> readImage3DData(ImageFileDirectory ifd, int nImages) throws IOException
    {
        // Use try-with-resource, closing the reader at the end of the try block
        File file = this.path.toFile();
        try (ImageBinaryDataReader reader = new ImageBinaryDataReader(file, ifd.getByteOrder()))
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

            int sizeX = ifd.getValue(BaselineTags.ImageWidth.CODE);
            int sizeY = ifd.getValue(BaselineTags.ImageHeight.CODE);
            
            int[] stripOffsets = ifd.getIntArrayValue(BaselineTags.StripOffsets.CODE, null);
            reader.seek(stripOffsets[0]);

            PixelType pixelType = ifd.determinePixelType();
            if (pixelType == PixelType.UINT8)
            {
                return reader.readUInt8Array3D(sizeX, sizeY, nImages);
            }
            else if (pixelType == PixelType.UINT12 || pixelType == PixelType.UINT16)
            {
                return reader.readUInt16Array3D(sizeX, sizeY, nImages);
            }
            else if (pixelType == PixelType.INT32)
            {
                return reader.readInt32Array3D(sizeX, sizeY, nImages);
            }
            else if (pixelType == PixelType.FLOAT32)
            {
                return reader.readFloat32Array3D(sizeX, sizeY, nImages);
            }
            else
            {
                throw new IOException("Can not read stack with data type " + pixelType + " pixel typee");
            }
        }
    }
    
    private Array<?> createFileMappedArray(ImageFileDirectory ifd, int nImages) throws IOException
    {
        long offset = (long) ifd.getIntArrayValue(BaselineTags.StripOffsets.CODE, null)[0];
        int sizeX = ifd.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd.getValue(BaselineTags.ImageHeight.CODE);
        int[] dims = new int[] {sizeX, sizeY, nImages};
        PixelType pixelType = ifd.determinePixelType();
        ByteOrder byteOrder = ifd.getByteOrder();
        
        return ImageIO.createFileMappedArray(path, offset, dims, pixelType, byteOrder);
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
    private TiffImageDataReader createImageDataReader(File file, ByteOrder byteOrder) throws IOException
    {
        TiffImageDataReader reader = new TiffImageDataReader(file, byteOrder);
        
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
    
    private void setupImageMetaData(Image image, ImageFileDirectory ifd)
    {
        // Setup spatial calibration
        setupSpatialCalibration(image, ifd);
        
        // setup LUT
        int[][] lut = retrieveLut(ifd);
        if (lut != null)
        {
            image.getDisplaySettings().setColorMap(new DefaultColorMap(lut));
        }
        
        addTiffTags(image, ifd.entries());
        
        // setup the file related to the image
        image.setNameFromFileName(path.getFileName().toString());
        image.setFilePath(path.toString());
        
        // iterate over tags to call the "update()" method
        for (TiffTag tag : ifd.entries())
        {
            tag.update(image, ifd);
        }
    }

    private static void setupSpatialCalibration(Image image, ImageFileDirectory ifd)
    {
        String unit = unitString(ifd);
        int nd = image.getDimension();
        ImageAxis[] axes = new ImageAxis[nd];
        double spacingX = 1.0 / ifd.getDoubleValue(BaselineTags.XResolution.CODE, 1.0);
        double spacingY = 1.0 / ifd.getDoubleValue(BaselineTags.YResolution.CODE, 1.0);
        axes[0] = new ImageAxis.X(spacingX, 0, unit);
        axes[1] = new ImageAxis.Y(spacingY, 0, unit);
        if (axes.length > 2)
        {
            axes[2] = new ImageAxis.Z();
        }
        
        image.setCalibration(new Calibration(axes));
    }
    
    private static final String unitString(ImageFileDirectory ifd)
    {
        TiffTag unitTag = ifd.getEntry(BaselineTags.ResolutionUnit.CODE);
        if (unitTag == null) return "";
        return switch (unitTag.value)
        {
            case 1 -> "";
            case 2 -> "Inch";
            case 3 -> "Centimeter";
            default -> "";
        };
    }
    
    private static final int[][] retrieveLut(ImageFileDirectory ifd)
    {
        int[][] lut = null;
        TiffTag tag = ifd.getEntry(BaselineTags.ColorMap.CODE);
        if (tag != null)
        {
            // class cast
            int[] lut16 = (int[]) tag.content;
            
            // Allocate memory for resulting LUT
            int lutLength = tag.count / 3;
            lut = new int[lutLength][3];
            
            // convert raw array into N-by-3 look-up table
            int j = 0;
            
            // for each color, keep only the most significant byte of the component
            for (int i = 0; i < lutLength; i++)
            {
                lut[i][0] = lut16[j] >> 8;
                lut[i][1] = lut16[j + 256] >> 8;
                lut[i][2] = lut16[j + 512] >> 8;
                j ++;
            }
         }
        return lut;
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
        
        public Calibration createCalibration(ImageFileDirectory ifd, int sizeC, int sizeZ, int sizeT)
        {
            ArrayList<ImageAxis> axes = new ArrayList<ImageAxis>(5);

            // Initialize mandatory X and Y axes
            double spacingX = 1.0 / ifd.getDoubleValue(BaselineTags.XResolution.CODE, 1.0);
            double spacingY = 1.0 / ifd.getDoubleValue(BaselineTags.YResolution.CODE, 1.0);
            axes.add(createXAxis(spacingX, ""));
            axes.add(createYAxis(spacingY, axes.get(0).getUnitName()));

            // Initialize optional C, Z and T axes
            if (sizeC > 1) axes.add(new ImageAxis("Channel", ImageAxis.Type.CHANNEL, 1, 0, ""));
            if (sizeZ > 1) axes.add(createZAxis(1.0, axes.get(0).getUnitName()));
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
