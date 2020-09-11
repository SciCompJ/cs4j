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
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8Array;
import net.sci.array.scalar.BinaryArray;
import net.sci.array.scalar.Int;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.vector.VectorArray;
import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;
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
	// Public enumerations

	/**
	 * The different types of images.
	 * @author dlegland
	 *
	 */
	public enum Type
	{
		UNKNOWN, 
		GRAYSCALE,
		INTENSITY,
		BINARY,
		LABEL,
		COLOR,
		COMPLEX,
		GRADIENT,
		VECTOR	
	}
	
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
			reader.close();
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
	Type type;
	
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
	public Image(Array<?> data, Type type)
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
	public Image(Array<?> data, Type type, Image parent)
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
		if (this.data instanceof BinaryArray)
		{
			this.type = Type.BINARY;
		} 
		else if (this.data instanceof UInt8Array) 
		{
			this.type = Type.GRAYSCALE;
		} 
		else if (this.data instanceof UInt16Array) 
		{
			this.type = Type.GRAYSCALE;
		}
		else if (this.data instanceof ScalarArray) 
		{
			this.type = Type.INTENSITY;
		}
        else if (this.data instanceof RGB8Array)
        {
            this.type = Type.COLOR;
        } 
        else if (this.data instanceof RGB16Array)
        {
            this.type = Type.COLOR;
        } 
		else if (this.data instanceof VectorArray) 
		{
			this.type = Type.VECTOR;
		}
		else
		{
			System.out.println("Could not determine image type for data of class " + this.data.getClass());
			this.type = Type.UNKNOWN;
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
	    if (this.type == Type.BINARY)
		{
			this.displaySettings.displayRange = new double[]{0, 1};
		}
		else if (this.type == Type.GRAYSCALE || this.type == Type.INTENSITY)
		{
			if (this.data instanceof UInt8Array)
			{
				this.displaySettings.displayRange = new double[]{0, 255};
			}
			else if (this.data instanceof ScalarArray)
			{
				this.displaySettings.displayRange = ((ScalarArray<?>) this.data).finiteValueRange();
			}
			else
			{
				throw new RuntimeException("Grayscale or intensity images require scalar array for data");
			}
		}
        else if (this.type == Type.COLOR)
        {
            // For color images, display range is applied to each channel identically.
            if (this.data instanceof RGB8Array)
            {
                // (in theory not used)
                this.displaySettings.displayRange = new double[]{0, 255};
            }
            else if (this.data instanceof RGB16Array)
            {
                // can be later adjusted
                this.displaySettings.displayRange = new double[]{0, 65535};
            }
        }
		else if (this.type == Type.LABEL)
		{
			// check array type
			if (!(this.data instanceof IntArray))
			{
				throw new RuntimeException("Label images require int array for data");
			}
		
			@SuppressWarnings("unchecked")
			IntArray<? extends Int> array = (IntArray<? extends Int>) this.data;
			int maxInt = array.maxInt();
			this.displaySettings.displayRange = new double[]{0, maxInt};
		}
	}
	
	private void copySettings(Image parent)
	{
		this.name = parent.name;
		this.extension = parent.extension;
		
		// duplicate the spatial calibration
		this.calibration = parent.calibration.duplicate(); 

		// copy display settings only if same data type
		if (this.type == parent.type)
		{
		    if (this.type == Type.COLOR)
		    {
		        // Additional processing to propagate settings only if same data type
		        if (this.data instanceof RGB8Array && parent.data instanceof RGB8Array)
		        {
		            this.displaySettings = parent.displaySettings.duplicate();
		        }
                if (this.data instanceof RGB16Array && parent.data instanceof RGB16Array)
                {
                    this.displaySettings = parent.displaySettings.duplicate();
                }
		    }
		    else
		    {
		        this.displaySettings = parent.displaySettings.duplicate();
		    }
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
	    Image res = new Image(this.data.duplicate(), this);
	    
	    // some fields are not copied by constructor
	    res.type = this.type;
	    return res;
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
    private void initCalibration()
    {
        int nd = this.getDimension();
        this.calibration = new Calibration(nd);
        
        // initialize channel axis depending on image type
        switch (type)
        {
        case GRAYSCALE:
        case INTENSITY:
        case BINARY:
            this.calibration.setChannelAxis(new CategoricalAxis("Value", Axis.Type.CHANNEL, new String[]{"Value"}));
            break;
        case LABEL:
            this.calibration.setChannelAxis(new CategoricalAxis("Label", new String[]{"Label"}));
            break;
            
        case COLOR:
        {
            String[] channelNames = new String[]{"Red", "Green", "Blue"};
            this.calibration.setChannelAxis(new CategoricalAxis("Channels", Axis.Type.CHANNEL, channelNames));
            break;
        }
        case COMPLEX:
        {
            String[] channelNames = new String[]{"Real", "Imag"};
            this.calibration.setChannelAxis(new CategoricalAxis("Parts", Axis.Type.CHANNEL, channelNames));
            break;
        }
        case GRADIENT:
        {
            int nChannels = ((VectorArray<?>) this.data).channelNumber();
            String[] channelNames = new String[nChannels];
            int nDigits = (int) Math.ceil(Math.log10(nChannels));
            String pattern = "G%0" + nDigits + "d";
            for (int c = 0; c < nChannels; c++)
            {
                channelNames[c] = String.format(pattern, c);
            }
            this.calibration.setChannelAxis(new CategoricalAxis("Dimensions", Axis.Type.CHANNEL, channelNames));
            break;
        }
        case VECTOR:
        {
            int nChannels = ((VectorArray<?>) this.data).channelNumber();
            String[] channelNames = new String[nChannels];
            int nDigits = (int) Math.ceil(Math.log10(nChannels));
            String pattern = "C%0" + nDigits + "d";
            for (int c = 0; c < nChannels; c++)
            {
                channelNames[c] = String.format(pattern, c);
            }
            this.calibration.setChannelAxis(new CategoricalAxis("Channels", Axis.Type.CHANNEL, channelNames));
            break;
        }
        case UNKNOWN:
            break;
        }
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
	public Type getType()
	{
		return type;
	}

	/**
     * Changes the way the image data should be interpreted.
     * 
     * @param type
     *            the type of data within this image
     */
	public void setType(Type type)
	{
		this.type = type;
	}
	
    public boolean isScalarImage()
    {
        return isGrayscaleImage() || isLabelImage() || this.type == Type.INTENSITY;
    }

    public boolean isLabelImage()
    {
        return this.type == Type.LABEL || this.type == Type.BINARY;
    }

	public boolean isGrayscaleImage()
	{
		return this.type == Type.GRAYSCALE || this.type == Type.BINARY;
	}

    public boolean isBinaryImage()
    {
        return this.type == Type.BINARY;
    }

	public boolean isVectorImage()
	{
		return this.type == Type.COLOR || this.type == Type.VECTOR || this.type == Type.COMPLEX || this.type == Type.GRADIENT;
	}

    public boolean isColorImage()
    {
        return this.type == Type.COLOR;
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
	    BufferedImage bImg = BufferedImageUtils.createAwtImage(this);
	    
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
