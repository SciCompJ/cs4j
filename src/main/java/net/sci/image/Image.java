/**
 * 
 */
package net.sci.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.BinaryArray;
import net.sci.array.data.IntArray;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.UInt16Array;
import net.sci.array.data.UInt8Array;
import net.sci.array.data.VectorArray;
import net.sci.array.data.color.RGB8Array;
import net.sci.array.type.Int;
import net.sci.image.io.ImageIOImageReader;
import net.sci.image.io.MetaImageReader;
import net.sci.image.io.TiffImageReader;
import net.sci.image.io.TiffTag;

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
		
		image.setName(file.getName());
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
	
	String name = "";
	
	/**
	 * The min and max displayable values of scalar images. Default is [0, 255].
	 */
	double[] displayRange = new double[]{0, 255};

	int[][] colorMap = null;
	
	//TODO: find a better way to store meta data
	public ArrayList<TiffTag> tiffTags = new ArrayList<>(0);
	

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
		int[] dataSize = this.data.getSize();
		
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
			this.displayRange = new double[]{0, 1};
		}
		else if (this.type == Type.GRAYSCALE || this.type == Type.INTENSITY)
		{
			if (this.data instanceof UInt8Array)
			{
				this.displayRange = new double[]{0, 255};
			}
			else if (this.data instanceof ScalarArray)
			{
				this.displayRange = ((ScalarArray<?>) this.data).valueRange();
			}
			else
			{
				throw new RuntimeException("Grayscale or intensity images require scalar array for data");
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
			this.displayRange = new double[]{0, maxInt};
		}
	}
	
	private void copySettings(Image parent)
	{
		if (this.type == parent.type)
		{
			this.displayRange = parent.displayRange;
		}
		this.colorMap = parent.colorMap;

		this.name = parent.name;
	}
	
	// =============================================================
	// Methods

	public Array<?> getData()
	{
		return this.data;
	}
	
	public int[][] getColorMap()
	{
		return this.colorMap;
	}
	
	public void setColorMap(int[][] map)
	{
		this.colorMap = map;
	}
	
	public double[] getDisplayRange()
	{
		return displayRange;
	}

	public void setDisplayRange(double[] displayRange)
	{
		this.displayRange = displayRange;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	// =============================================================
	// Basic accessors

	/**
	 * @return the number of physical dimensions of this image.
	 */
	public int getDimension()
	{
		return this.size.length;
	}

	/**
	 * @return the physical size of the image.
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
	
	public boolean isBinaryImage()
	{
		return this.type == Type.BINARY;
	}

	public boolean isGrayscaleImage()
	{
		return this.type == Type.GRAYSCALE || this.type == Type.BINARY;
	}

	public boolean isColorImage()
	{
		return this.type == Type.COLOR;
	}

	public boolean isVectorImage()
	{
		return this.type == Type.COLOR || this.type == Type.VECTOR || this.type == Type.COMPLEX || this.type == Type.GRADIENT;
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

}
