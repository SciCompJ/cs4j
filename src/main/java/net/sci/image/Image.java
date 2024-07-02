/**
 * 
 */
package net.sci.image;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Arrays;
import net.sci.array.binary.Binary;
import net.sci.array.color.RGB16;
import net.sci.array.color.RGB8;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.VectorArray;
import net.sci.image.io.ImageIOImageReader;
import net.sci.image.io.MetaImageReader;
import net.sci.image.io.TiffImageReader;
import net.sci.image.io.tiff.TiffTag;

/**
 * A multi-dimensional image, represented by a multi-dimensional array together
 * with interpretation info and meta-data.
 * 
 * Follows same implementation pattern as "Image" class from Matlab project.
 * 
 * @author dlegland
 *
 */
public class Image
{
	// =============================================================
	// Static methods

	public static final Image readImage(File file) throws IOException 
	{
		// check if file exists
		if (!file.exists()) 
		{
			throw new FileNotFoundException("Could not find file: "
					+ file.getName());
		}
		
		Image image;
		
		if (file.getName().endsWith(".tif"))
		{
			TiffImageReader reader = new TiffImageReader(file);
			image = reader.readImage();
		} 
		else if (file.getName().endsWith(".mhd"))
		{
			MetaImageReader reader = new MetaImageReader(file);
			image = reader.readImage();
		} 
		else
		{
			ImageIOImageReader reader = new ImageIOImageReader(file); 
			image = reader.readImage();
		}
		
		image.setNameFromFileName(file.getName());
		image.setFilePath(file.getPath());

		return image;
	}
	

	// =============================================================
	// Class fields

	/**
	 * The multi-dimensional array containing image data.
	 */
	Array<?> data;
	
	/**
	 * The size of image, including physical and time dimensions.
	 */
	int[] size;
	
	/**
	 * The type of image, giving information on how to interpret image element.
	 */
	ImageType type;
	
	/**
	 * The name of the image, used to identify it and populate GUI widgets.
	 */
	String name = "";
	
	/**
	 * The extension of the image when it was read from a file, or an empty String otherwise.
	 */
	String extension = "";
	
    /**
     * The name of the full path to the image file, if loaded from a file.
     */
    String filePath = "";
    
    /**
     * The calibration of each axis (space, time...) and eventually of the
     * channels.
     */
    Calibration calibration;
    
    /**
     * Information necessary for representing and interpreting intensity values.
     */
    DisplaySettings displaySettings = new DisplaySettings(); 

    // TODO: find a better way to store meta data
    /**
     * The optional list of TIFF tags read from file, indexed by Tiff Tag code
     */
	public Map<Integer, TiffTag> tiffTags = new TreeMap<Integer, TiffTag>();
	

	// =============================================================
	// Constructors

	/**
     * Constructor from data array, trying to infer type from data type and
     * shape.
     * 
     * @param data
     *            the array containing image data
     */
	public Image(Array<?> data)
	{
		this.data = data;
		setImageTypeFromDataType();
		computeImageSize();
		initCalibration();
		setupDisplayRange();
	}

	/**
	 * Constructor from data array and type specifier. 
     *
     * @param data
     *            the array containing image data
     * @param type
     *            the type of image (Intensity, label, binary...)
	 */
	public Image(Array<?> data, ImageType type)
	{
		this.data = data;
		this.type = type;
		computeImageSize();
		initCalibration();
		setupDisplayRange();
	}

	/**
     * Creates a new image, initialized by image data, and keeping meta-data
     * from parent image.
     * 
     * @param data
     *            the initial data array for this image
     * @param parent
     *            the parent image used for inferring meta data
     */
	public Image(Array<?> data, Image parent)
	{
		this(data);

		// additional processing to take into account parent image
		copySettings(parent);
	}
	
	/**
     * Creates a new image, initialized by image data, and keeping meta-data
     * from parent image.
     * 
     * @param data
     *            the initial data array for this image
     * @param parent
     *            the parent image used for inferring meta data
     */
	public Image(Array<?> data, ImageType type, Image parent)
	{
		this(data, type);
		
		// additional processing to take into account parent image
		copySettings(parent);
	}
	
	/**
	 * Determines the type of image from the type of the inner data array.
	 */
	private void setImageTypeFromDataType()
	{
		if (this.data.elementClass() == Binary.class)
		{
			this.type = ImageType.BINARY;
		}
		else if (this.data.elementClass() == UInt8.class)
		{
			this.type = ImageType.GRAYSCALE;
		} 
        else if (this.data.elementClass() == UInt16.class)
		{
			this.type = ImageType.GRAYSCALE;
		}
		else if (this.data instanceof ScalarArray) 
		{
			this.type = ImageType.INTENSITY;
		}
        else if (this.data.elementClass() == RGB8.class)
        {
            this.type = ImageType.COLOR;
        } 
        else if (this.data.elementClass() == RGB16.class)
        {
            this.type = ImageType.COLOR;
        } 
		else if (this.data instanceof VectorArray) 
		{
			this.type = ImageType.VECTOR;
		}
		else
		{
			System.out.println("Could not determine image type for data of class " + this.data.getClass());
			this.type = ImageType.UNKNOWN;
		}
	}

	/**
	 * Compute the size of image from size of data and image type.
	 */
	private void computeImageSize()
	{
		// get data infos
		int dataND = this.data.dimensionality();
		int[] dataSize = this.data.size();
		
		// compute size of image
		int nd = dataND;
		this.size = new int[nd];
		
		// create size array depending on image type
		System.arraycopy(dataSize, 0, this.size, 0, nd);
	}

	private void setupDisplayRange()
	{
	    this.type.setupDisplaySettings(this);
	}
	
	private void copySettings(Image parent)
	{
		this.name = parent.name;
		this.extension = parent.extension;
		
        // Check if parent type is compatible with data
        // (escape the case of binary images, that should not be considered as intensity nor distance)
        if (parent.type.isCompatibleWith(this.data) && this.data.elementClass() != Binary.class)
		{
		    // update type and refresh calibration
		    this.type = parent.type;
		    this.type.setupCalibration(this);
		    
		    if (this.type == ImageType.COLOR)
		    {
		        // Additional processing to propagate settings only if same data type
		        if (this.data.elementClass() == RGB8.class && parent.data.elementClass() == RGB8.class)
		        {
		            this.displaySettings = parent.displaySettings.duplicate();
		        }
                if (this.data.elementClass() == RGB16.class && parent.data.elementClass() == RGB16.class)
                {
                    this.displaySettings = parent.displaySettings.duplicate();
                }
		    }
		    else
		    {
		        this.displaySettings = parent.displaySettings.duplicate();
		    }
		}
		
        // duplicate the spatial calibration if appropriate
        if (Arrays.isSameSize(this.data, parent.data))
        {
            this.calibration.axes = parent.calibration.duplicateAxes(); 
        }
        
        // copy meta-data if any (may be obsolete...)
        this.tiffTags = parent.tiffTags;
	}
	
	
    // =============================================================
    // Methods

	/**
	 * Duplicates image data, and copy meta-data.
	 * 
	 * @return a duplicate of this image
	 */
	public Image duplicate()
	{
	    return new Image(this.data.duplicate(), this.type, this);
	}
	
	
    // =============================================================
    // Management of value calibration

	public Array<?> getData()
	{
		return this.data;
	}
	
	/**
	 * @return the displaySettings
     */
    public DisplaySettings getDisplaySettings()
    {
        return displaySettings;
    }

    /**
     * @param displaySettings the displaySettings to set
     */
    public void setDisplaySettings(DisplaySettings displaySettings)
    {
        this.displaySettings = displaySettings;
    }
    

	// =============================================================
    // Management of axes calibration

    public Calibration getCalibration()
    {
        return this.calibration;
    }
    
    public void setCalibration(Calibration calibration)
    {
        this.calibration = calibration;
    }
    
    /**
     * Creates default calibration using spatial axis for each dimension.
     */
    public void clearCalibration()
    {
        initCalibration();
    }
    
    /**
     * Creates default calibration using spatial axis for each dimension.
     */
    private void initCalibration()
    {
        this.calibration = new Calibration(this.getDimension());
        this.type.setupCalibration(this);
    }
    
    
    // =============================================================
    // Identification meta-data

    /**
     * @return the name of this image (without the file name extension).
     */
    public String getName()
	{
		return this.name;
	}

    /**
     * @return the extension associated to this image, or an empty String.
     */
    public String getExtension()
	{
		return this.extension;
	}

    /**
     * @return the name of this image together with the extension, if it is not null.
     */
    public String getFullName()
	{
    	if (this.extension.isEmpty())
    		return this.name;
    	return this.name + "." + this.extension;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setExtension(String extension)
	{
		this.extension = extension;
	}
	
    public String getFilePath()
    {
        return this.filePath;
    }
    
    public void setFilePath(String path)
    {
        this.filePath = path;
    }
    
    /**
	 * Initializes the name and the extension from the file name.
	 * 
	 * @param fileName
	 *            the name of the file used to load this image.
	 */
    public void setNameFromFileName(String fileName)
    {
		String[] fileParts = splitFileName(fileName);
		if (!fileParts[0].isEmpty())
		{
			this.name = fileParts[0];
			this.extension = fileParts[1];
		}
		else
		{
			// special case of a file name starting with a dot -> keep the full name.
			this.name = fileName;
		}
    }
    
	/**
	 * Converts a file name (or a file path) into a String array containing the
	 * base file name and the extension. Either the base name or the extension
	 * may be empty.
	 * 
	 * @param fileName
	 *            the original file name
	 * @return a String array containing the base file name and the extension
	 */
	private static final String[] splitFileName(String fileName)
	{
	    // Remove the path up to the last separator
	    String separator = System.getProperty("file.separator");
	    int sepIndex = fileName.lastIndexOf(separator);
	    String baseName = sepIndex == -1 ? fileName : fileName.substring(sepIndex + 1);

	    // find extension index
	    int extIndex = baseName.lastIndexOf(".");
	    if (extIndex == -1)
	    {
	    	// no extension
		    return new String[] {baseName, ""};
	    }
	    else
	    {
	    	// split before and after extension
			return new String[] { 
					baseName.substring(0, extIndex),
					baseName.substring(extIndex + 1) };
	    }
	}
	



	// =============================================================
	// Basic accessors

	/**
	 * @return the number of dimensions of this image.
	 */
	public int getDimension()
	{
		return this.size.length;
	}

	/**
	 * @return the size of the image.
	 */
	public int[] getSize()
	{
		return this.size;
	}
	
	/**
     * Returns the size of the image along the specified dimension, starting
     * from 0.
     * 
     * @param dim
     *            the dimension for computing size
     * @return the size in the specified dimension
     */
	public int getSize(int dim)
	{
		return this.size[dim];
	}

	/**
	 * @return the image type
	 */
	public ImageType getType()
	{
		return type;
	}

	/**
     * Changes the way the image data should be interpreted.
     * 
     * @param type
     *            the type of data within this image
     */
	public void setType(ImageType type)
	{
		this.type = type;
		this.type.setupDisplaySettings(this);
	}
	
    public boolean isScalarImage()
    {
        return isGrayscaleImage() || isLabelImage() || this.type == ImageType.INTENSITY || this.type == ImageType.DISTANCE;
    }

    public boolean isLabelImage()
    {
        return this.type == ImageType.LABEL || this.type == ImageType.BINARY;
    }

	public boolean isGrayscaleImage()
	{
		return this.type == ImageType.GRAYSCALE || this.type == ImageType.BINARY;
	}

    public boolean isBinaryImage()
    {
        return this.type == ImageType.BINARY;
    }

	public boolean isVectorImage()
	{
		return this.type == ImageType.COLOR || this.type == ImageType.VECTOR || this.type == ImageType.COMPLEX || this.type == ImageType.GRADIENT;
	}

    public boolean isColorImage()
    {
        return this.type == ImageType.COLOR;
    }

	/**
	 * Applies the given operator to image data, and returns a new image with
	 * the result.
	 * 
	 * @param op
	 *            the operator to apply on image data
	 * @return a new Image instance
	 */
	public Image apply(ArrayOperator op)
	{
		Array<?> newData = op.process(this.data);
		return new Image(newData, this);
	}

	public void show()
	{
	    BufferedImage bImg = this.type.createAwtImage(this);
	    
	    JFrame frame = new JFrame(this.name);
	    frame.setTitle(this.name);
	    JPanel imagePanel = new JPanel()
	    {
            private static final long serialVersionUID = 1L;

            public Dimension getPreferredSize()
	        {
	            return getDisplaySize();
	        }
	        
	        public Dimension getDisplaySize() 
	        {
	            return new Dimension(bImg.getWidth(), bImg.getHeight());
	        }

	        public void paintComponent(Graphics g) 
	        {
	            super.paintComponent(g);
                Dimension dim = this.getDisplaySize();
                g.drawImage(bImg, 0, 0, dim.width, dim.height, null);
	        }
	    };
	    frame.add(imagePanel);
	    
	    frame.pack();
	    frame.setVisible(true);
	}
}
