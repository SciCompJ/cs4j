/**
 * 
 */
package net.sci.image;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import net.sci.array.Array;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.color.Color;
import net.sci.array.color.ColorMap;
import net.sci.array.color.ColorMaps;
import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.VectorArray;
import net.sci.image.process.shape.ImageSlicer;

/**
 * Collection of methods for managing conversion to java BufferedImage
 * class.
 * 
 * @deprecated conversion to BufferedImage is now managed by ImageType instances
 * 
 * @author dlegland
 *
 */
@Deprecated
public class BufferedImageUtils
{
	/**
	 * Private constructor to prevent instantiation.
	 */
	private BufferedImageUtils()
	{
	}

    public static final java.awt.image.BufferedImage createAwtImage(Image image, int sliceIndex)
    {
        // extract specified slice from image
        Image image2d = ImageSlicer.slice2d(image, 0, 1, new int[]{0, 0, sliceIndex});
        return createAwtImage(image2d);
    }
    
    public static final java.awt.image.BufferedImage createAwtImage(Image image)
    {
        // extract LUT from image, or create one otherwise
        ColorMap lut = image.getDisplaySettings().getColorMap();
        if (lut == null)
        {
            lut = ColorMaps.GRAY.createColorMap(256); 
        }

        Array<?> array = image.getData();
        if (array.dimensionality() != 2)
        {
            throw new RuntimeException("Requires image data array to have 2 dimensions, not " + array.dimensionality());
        }
        
        // Dispatch process depending on image type
        if (image.isBinaryImage())
        {
            // Check adequacy of array type with image type
            if (array.elementClass() != Binary.class)
            {
                throw new RuntimeException("Binary images must refere to array of Binary");
            }
            
            // ensure array is binary class
            BinaryArray2D binaryArray = BinaryArray2D.wrap(BinaryArray.wrap(array)); 
            
            // convert the binary image to bi-color image
            return createAwtImage(binaryArray, RGB8.RED, RGB8.WHITE);
        } 
        else if (image.isLabelImage())
        {
            if (!(array instanceof IntArray))
            {
                throw new RuntimeException("Label images assume inner array implements IntArray");
            }
            IntArray2D<?> intArray = IntArray2D.wrap((IntArray<?>) array);
            return labelToAwtImage(intArray, lut, image.getDisplaySettings().getBackgroundColor());
        }
        else if (image.isColorImage())
        {
            // Check if the array contains RGB8 data
            if (array.elementClass() == RGB8.class)
            {
                 return createAwtImageRGB8(RGB8Array.wrap(array));
            }
            
            // convert RBG16 image to AWT image, using display range
            if (array.elementClass() == RGB16.class)
            {
                double[] displayRange = image.getDisplaySettings().getDisplayRange();
                return createAwtImageRGB16(RGB16Array.wrap(array), displayRange);
            }
            
            throw new RuntimeException("Could not process color image with array of class " + array.getClass().getName());
        }
        else if (image.getType() == ImageType.DISTANCE)
        {
            DisplaySettings settings = image.getDisplaySettings();
            return distanceMapToAwtImage((ScalarArray2D<?>) array, settings.displayRange[1], lut, settings.getBackgroundColor());
        }
        
        // Process array depending on its data type
        if (array instanceof ScalarArray)
        {
            // scalar images use display range and current LUT
            double[] displayRange = image.getDisplaySettings().getDisplayRange();
            
            // convert to ScalarArray2D either by class cast or by wrapping
            ScalarArray2D<?> array2d = ScalarArray2D.wrapScalar2d((ScalarArray<?>) array);
            
            return createAwtImage(array2d, displayRange, lut);
        }
        else if (array instanceof VectorArray)
        {
            // Compute the norm of the vector
            // (and keep the result as 2D)
            ScalarArray2D<?> norm = ScalarArray2D.wrapScalar2d(VectorArray.norm((VectorArray<?,?>) array));
            
            // convert image of the norm to AWT image
            double[] valueRange = norm.finiteValueRange();
            double[] displayRange = new double[]{0.0, valueRange[1]};
            return createAwtImage(norm, displayRange, lut);
        } 

        throw new RuntimeException("Could not process image of type " + image.getType() +
                ", with array of class " + array.getClass().getName());
    }
    
    
    public static final java.awt.image.BufferedImage createAwtImage(UInt8Array2D array, int[][] lut)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // Computes the color model
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        for(int i = 0; i < 256; i++) 
        {
            red[i] = (byte) lut[i][0];
            green[i] = (byte) lut[i][1];
            blue[i] = (byte) lut[i][2];
        }
        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
        
        // Create the AWT image
        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
        
        // Populate the raster
        WritableRaster raster = bufImg.getRaster();
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int value = array.getInt(x, y);
                raster.setSample(x, y, 0, value); 
            }
        }

        return bufImg;
    }

//  private static final int[][] createGrayLut()
//  {
//      int[][] lut = new int[256][];
//      for (int i = 0; i < 256; i++)
//      {
//          lut[i] = new int[]{i, i, i};
//      }
//      return lut;
//  }
    
    public static final java.awt.image.BufferedImage createAwtImage(
            BinaryArray2D array, Color fgColor, Color bgColor)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // Computes the color model
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        red[0]      = (byte) (bgColor.red() * 255);
        green[0]    = (byte) (bgColor.green() * 255);
        blue[0]     = (byte) (bgColor.blue() * 255);
        red[255]    = (byte) (fgColor.red() * 255);
        green[255]  = (byte) (fgColor.green() * 255);
        blue[255]   = (byte) (fgColor.blue() * 255);
        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
        
        // Create the AWT image
        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
        
        // Populate the raster
        WritableRaster raster = bufImg.getRaster();
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int value = array.getBoolean(x, y) ? 255 : 0;
                raster.setSample(x, y, 0, value); 
            }
        }

        return bufImg;
    }


    public static final java.awt.image.BufferedImage createAwtImage(
            ScalarArray2D<?> array, double[] displayRange, int[][] colormap)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // Computes the color model
        IndexColorModel cm = createIndexColorModel(colormap);  
        
        // compute slope for intensity conversions
        double extent = displayRange[1] - displayRange[0];
        
        // Create the AWT image
        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
        
        // Populate the raster
        WritableRaster raster = bufImg.getRaster();
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                double value = array.getValue(x, y);
                if (!Double.isFinite(value))
                {
                    continue;
                }
                int sample = (int) Math.min(Math.max(255 * (value - displayRange[0]) / extent, 0), 255);
                raster.setSample(x, y, 0, sample); 
            }
        }

        return bufImg;
    }

    public static final java.awt.image.BufferedImage createAwtImage(
            ScalarArray2D<?> array, double[] displayRange, ColorMap colormap)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // Computes the color model
        IndexColorModel cm = createIndexColorModel(colormap);  
        
        // compute slope for intensity conversions
        double extent = displayRange[1] - displayRange[0];
        
        // Create the AWT image
        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
        
        // Populate the raster
        WritableRaster raster = bufImg.getRaster();
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                double value = array.getValue(x, y);
                if (!Double.isFinite(value))
                {
                    continue;
                }
                int sample = (int) Math.min(Math.max(255 * (value - displayRange[0]) / extent, 0), 255);
                raster.setSample(x, y, 0, sample); 
            }
        }

        return bufImg;
    }
    
    public static final java.awt.image.BufferedImage labelToAwtImage(
            IntArray2D<?> array, ColorMap colormap, Color backgroundColor)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // Computes the color model
        IndexColorModel cm = createIndexColorModel(colormap, backgroundColor);  
        
        // Create the AWT image
        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
        
        // Populate the raster
        WritableRaster raster = bufImg.getRaster();
        int nLabels = colormap.size() - 1;
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int index = array.getInt(x, y);
                if (index > 0)
                {
                    index = ((index - 1) % nLabels) + 1;
                }
                raster.setSample(x, y, 0, index); 
            }
        }

        return bufImg;
    }
    
    public static final java.awt.image.BufferedImage distanceMapToAwtImage(
            ScalarArray2D<?> array, double distMax, ColorMap colormap, Color backgroundColor)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // Computes the color model
        IndexColorModel cm = createIndexColorModel(colormap, backgroundColor);  
        
        // Create the AWT image
        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
        
        // Populate the raster
        WritableRaster raster = bufImg.getRaster();
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                double value = array.getValue(x, y);
                int index = (value == 0) ? 0 : (int) Math.floor((value * 254 / (distMax+0.001)) + 1);
                raster.setSample(x, y, 0, index); 
            }
        }

        return bufImg;
    }
    
    /**
     * Convert the colormap given as N-by-3 array into an IndexColorModel.
     * 
     * @param colormap the colormap as 256 array of 3 components
     * @return the corresponding IndexColorModel
     */
    private final static IndexColorModel createIndexColorModel(int[][] colormap)
    {
        // Computes the color model
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        for(int i = 0; i < 256; i++) 
        {
            red[i]      = (byte) colormap[i][0];
            green[i]    = (byte) colormap[i][1];
            blue[i]     = (byte) colormap[i][2];
        }
        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
        return cm;
    }
    
    /**
     * Convert the colormap given as N-by-3 array into an IndexColorModel.
     * 
     * @param colormap the colormap as 256 array of 3 components
     * @return the corresponding IndexColorModel
     */
    private final static IndexColorModel createIndexColorModel(ColorMap colormap)
    {
        // Computes the color model
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        int nColors = colormap.size();
        for(int i = 0; i < 256; i++) 
        {
            net.sci.array.color.Color color = colormap.getColor(i % nColors);
            red[i]      = (byte) (color.red() * 255);
            green[i]    = (byte) (color.green() * 255);
            blue[i]     = (byte) (color.blue() * 255);
        }
        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
        return cm;
    }
    
    /**
     * Convert the colormap given as N-by-3 array into an IndexColorModel.
     * 
     * @param colormap the colormap instance
     * @param background the background color
     * @return the corresponding IndexColorModel, with 256 colors max.
     */
    private final static IndexColorModel createIndexColorModel(ColorMap colormap, Color background)
    {
        // allocate color components arrays
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];

        // first color corresponds to background
        red[0] = (byte) (background.red() * 255);
        green[0] = (byte) (background.green() * 255);
        blue[0] = (byte) (background.blue() * 255);

        // convert colormapcolors
        int nColors = Math.min(colormap.size(), 255);
        for(int i = 0; i < nColors; i++) 
        {
            net.sci.array.color.Color color = colormap.getColor(i);
            red[i+1]      = (byte) (color.red() * 255);
            green[i+1]    = (byte) (color.green() * 255);
            blue[i+1]     = (byte) (color.blue() * 255);
        }
        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
        return cm;
    }
    
    public static final java.awt.image.BufferedImage createAwtImageRGB8(
            UInt8Array array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        int type = java.awt.image.BufferedImage.TYPE_INT_RGB;
        
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type);
        WritableRaster raster = bufImg.getRaster();
        
        int[] pos = new int[3];
        for (int y = 0; y < sizeY; y++)
        {
            pos[1] = y;
            for (int x = 0; x < sizeX; x++)
            {
                pos[0] = x;
                for (int c = 0; c < 3; c++)
                {
                    pos[2] = c;
                    raster.setSample(x, y, c, array.getInt(pos));
                }
            }
        }
        
        return bufImg;
    }

    public static final java.awt.image.BufferedImage createAwtImageRGB8(RGB8Array array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        int type = java.awt.image.BufferedImage.TYPE_INT_RGB;
        
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type);
        WritableRaster raster = bufImg.getRaster();
        
        int[] pos = new int[3];
        for (int y = 0; y < sizeY; y++)
        {
            pos[1] = y;
            for (int x = 0; x < sizeX; x++)
            {
                pos[0] = x;
                RGB8 rgb = array.get(pos);
                for (int c = 0; c < 3; c++)
                {
                    pos[2] = c;
                    raster.setSample(x, y, c, rgb.getSample(c));
                }
            }
        }
        
        return bufImg;
    }

    public static final java.awt.image.BufferedImage createAwtImageRGB16(RGB16Array array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // determines max red, green and blue values
        int rMax = 0, gMax = 0, bMax = 0;
        for (RGB16 rgb : array)
        {
            rMax = Math.max(rMax, rgb.getSample(0));
            gMax = Math.max(gMax, rgb.getSample(1));
            bMax = Math.max(bMax, rgb.getSample(2));
        }
        double k = 255.0 / Math.max(Math.max(rMax,  gMax),  bMax);
 
        // create result AWT image
        int type = java.awt.image.BufferedImage.TYPE_INT_RGB;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type);
        WritableRaster raster = bufImg.getRaster();
        
        int[] pos = new int[3];
        for (int y = 0; y < sizeY; y++)
        {
            pos[1] = y;
            for (int x = 0; x < sizeX; x++)
            {
                pos[0] = x;
                RGB16 rgb = array.get(pos);
                for (int c = 0; c < 3; c++)
                {
                    pos[2] = c;
                    raster.setSample(x, y, c, (int) (rgb.getSample(c) * k));
                }
            }
        }
        
        return bufImg;
    }
    
    
    /**
     * Converts the RGB16 array into an instance of BufferedImage, by converting all
     * color components into 0 and 255, taking into account the specified display
     * range.
     * 
     * @param array        the array to convert, only the first two dimensions are
     *                     processed.
     * @param displayRange the values that will be mapped to 0 and 255 in each
     *                     channel.
     * @return an instance of BufferedImage that can be easily displayed.
     */
    public static final java.awt.image.BufferedImage createAwtImageRGB16(RGB16Array array, double displayRange[])
    {
        // get array dimensions
        if (array.dimensionality() < 2)
        {
            throw new IllegalArgumentException("Expect an array with two dimensions");
        }
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // compute coefficients of correction
        if (displayRange.length < 2)
        {
            throw new IllegalArgumentException("Display range must have two elements");
        }
        double v0 = displayRange[0];
        double k = 255.0 / (displayRange[1] - v0);
 
        // allocate memory for result AWT image
        int type = java.awt.image.BufferedImage.TYPE_INT_RGB;
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type);
        WritableRaster raster = bufImg.getRaster();
        
        // prepare for iteration
        int[] pos = new int[2];
        int[] samples = new int[3];
        
        // iterate over positions within array
        for (int y = 0; y < sizeY; y++)
        {
            pos[1] = y;
            for (int x = 0; x < sizeX; x++)
            {
                pos[0] = x;
                array.getSamples(pos, samples);

                // process each channel of current pixel
                for (int c = 0; c < 3; c++)
                {
                    int v = (int) Math.min(Math.max((samples[c] - v0) * k, 0), 255);
                    raster.setSample(x, y, c, v);
                }
            }
        }
        
        return bufImg;
    }
    
//	public static final java.awt.image.BufferedImage convertImageSlice(Image image, int sliceIndex)
//	{
//		// extract LUT from image, or create one otherwise
//		ColorMap lut = image.getDisplaySettings().getColorMap();
//		if (lut == null)
//		{
//			lut = createGrayLut(); 
//		}
//
//		// Ensure data is a planar image
//		Array<?> array = getImageSlice(image, sliceIndex);
//
//		// Process array depending on its data type
//		if (array instanceof BinaryArray2D)
//		{
//			// binary images are converted to bi-color images
//			return convertBooleanArray((BinaryArray2D) array, Color.RED, Color.WHITE);
//		}
// 		else if (array instanceof ScalarArray2D)
// 		{
// 			// scalar images use display range and current LUT
// 			double[] displayRange = image.getDisplaySettings().getDisplayRange();
// 			return convertScalarArray((ScalarArray2D<?>) array, displayRange, lut);
// 		}
//		else if (array instanceof RGB8Array)
//		{
//			// call the standard way for converting planar RGB images
//			return convertRGB8Array((RGB8Array) array);
//		} 
// 		else if (image.isColorImage())
// 		{
// 			// obsolete, and should be removed
// 			System.err.println("Color images should implement RGB8Array interface");
// 			return convertUInt8Array((UInt8Array) array);
// 		}
//		else if (array instanceof VectorArray)
//		{
//			// Compute the norm of the vector
//			ScalarArray<?> norm = VectorArray.norm((VectorArray<?>) array);
////			double vMax = 0;
////			for (Scalar item : norm)
////			{
////				vMax = Math.max(vMax, item.getValue());
////			}
//			
//			// convert image of the norm to AWT image
//			double[] displayRange = image.getDisplaySettings().getDisplayRange();
// 			return convertScalarArray((ScalarArray2D<?>) norm, displayRange, lut);
//		} 
//
// 		return null;
//	}
//	
//	private static final Array<?> getImageSlice(Image image, int sliceIndex)
//	{
//		Array<?> array = image.getData();
//		if (image.getDimension() == 2)
//		{
//			return array;
//		}
//
//		int nd = array.dimensionality();
//		array = createArraySlice(array, nd - 1, sliceIndex);
//		return array;
//	}
//	
//	private static <T> Array<T> createArraySlice(Array<T> array, int dim, int sliceIndex)
//	{
//		// check validity of dimension number 
//		if (array.dimensionality() < 3)
//		{
//			throw new IllegalArgumentException("Requires an array with at least three dimensions");
//		}
//		
//		// check validity of slice index
//		int sizeZ = array.size(dim);
//		if (sliceIndex < 0 || sliceIndex >= sizeZ)
//		{
//			throw new IllegalArgumentException(String.format("Slice index (%d) must be comprised between 0 and %d", sliceIndex, sizeZ-1));
//		}
//
//		// infos of initial array
//		int nd = array.dimensionality();
//		int[] dims = array.size();
//		
//		// create new array for slice
//		int nd2 = nd - 1;
//		int[] dims2 = new int[nd2];
//		for (int d = 0; d < dim; d++)
//		{
//			dims2[d] = dims[d];
//		}
//		for (int d = dim; d < nd2; d++)
//		{
//			dims2[d] = dims[d+1];
//		}
//		
//		Array<T> slice = array.newInstance(dims2);
//		
//		// create position cursors
//		int[] pos0 = new int[nd];
//		pos0[dim] = sliceIndex;
//		int[] pos2;
//		
//		// iterate over slice pixels
//        Array.PositionIterator iter = array.positionIterator();
//        while (iter.hasNext())
//        {
//            // iterate position cursor
//            pos2 = iter.next();
//			
//			// convert position on slice to position in original array
//			for (int d = 0; d < dim; d++)
//			{
//				pos0[d] = pos2[d];
//			}
//			for (int d = dim; d < nd2; d++)
//			{
//				pos0[d+1] = pos2[d];
//			}
//			
//			slice.set(pos2, array.get(pos0));
//		}
//
//		return slice;
//	}
//
//	/**
//	 * Converts an array to a buffered image, by inferring the type of image
//	 * from the type of array.
//	 * 
//	 * @param array
//	 *            the array to convert.
//	 * @return the resulting BufferedImage
//	 */
//	public static final java.awt.image.BufferedImage convertArray(Array<?> array)
//	{
//		if (array instanceof UInt8Array)
//		{
//			return convertUInt8Array((UInt8Array) array);
//		}
//		else if (array instanceof RGB8Array)
//		{
//			return convertRGB8Array((RGB8Array) array);
//		}
//		else if (array instanceof BinaryArray)
//		{
//			return convertBooleanArray((BinaryArray) array, Color.WHITE, Color.BLACK);
//		}
//		
//		throw new RuntimeException("Could not convert the array of class: " + array.getClass());
//	}
//	
//	public static final java.awt.image.BufferedImage convertUInt8Array(UInt8Array2D array, int[][] lut)
//	{
//		// get array size
//		int sizeX = array.size(0);
//		int sizeY = array.size(1);
//		
//		// Computes the color model
//		byte[] red = new byte[256];
//		byte[] green = new byte[256];
//		byte[] blue = new byte[256];
//		for(int i = 0; i < 256; i++) 
//		{
//			red[i] = (byte) lut[i][0];
//			green[i] = (byte) lut[i][1];
//			blue[i] = (byte) lut[i][2];
//		}
//		IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
//		
//		// Create the AWT image
//		int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
//		BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
//		
//		// Populate the raster
//		WritableRaster raster = bufImg.getRaster();
//		for (int y = 0; y < sizeY; y++)
//		{
//			for (int x = 0; x < sizeX; x++)
//			{
//				int value = array.getInt(x, y);
//				raster.setSample(x, y, 0, value); 
//			}
//		}
//
//		return bufImg;
//	}
//
//    private static final ColorMap createGrayLut()
//    {
//        return ColorMaps.GRAY.createColorMap(256);
//    }
//	
//	public static final java.awt.image.BufferedImage convertBooleanArray(
//			BinaryArray array, Color fgColor, Color bgColor)
//	{
//		// get array size
//		int sizeX = array.size(0);
//		int sizeY = array.size(1);
//		
//		// Computes the color model
//		byte[] red = new byte[256];
//		byte[] green = new byte[256];
//		byte[] blue = new byte[256];
//		red[0] 		= (byte) bgColor.getRed();
//		green[0] 	= (byte) bgColor.getGreen();
//		blue[0] 	= (byte) bgColor.getBlue();
//		red[255] 	= (byte) fgColor.getRed();
//		green[255] 	= (byte) fgColor.getGreen();
//		blue[255] 	= (byte) fgColor.getBlue();
//		IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
//		
//		// Create the AWT image
//		int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
//		BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
//		
//		// Populate the raster
//		WritableRaster raster = bufImg.getRaster();
//		int[] pos = new int[2];
//		for (int y = 0; y < sizeY; y++)
//		{
//			pos[1] = y;
//			for (int x = 0; x < sizeX; x++)
//			{
//				pos[0] = x;
//				int value = array.getBoolean(pos) ? 255 : 0;
//				raster.setSample(x, y, 0, value); 
//			}
//		}
//
//		return bufImg;
//	}
//
//
//    public static final java.awt.image.BufferedImage convertScalarArray(
//            ScalarArray2D<?> array, double[] displayRange, int[][] colormap)
//    {
//        // get array size
//        int sizeX = array.size(0);
//        int sizeY = array.size(1);
//        
//        // Computes the color model
//        IndexColorModel cm = createIndexColorModel(colormap);  
//        
//        // compute slope for intensity conversions
//        double extent = displayRange[1] - displayRange[0];
//        
//        // Create the AWT image
//        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
//        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
//        
//        // Populate the raster
//        WritableRaster raster = bufImg.getRaster();
//        for (int y = 0; y < sizeY; y++)
//        {
//            for (int x = 0; x < sizeX; x++)
//            {
//                double value = array.getValue(x, y);
//                int sample = (int) Math.min(Math.max(255 * (value - displayRange[0]) / extent, 0), 255);
//                raster.setSample(x, y, 0, sample); 
//            }
//        }
//
//        return bufImg;
//    }
//    
//    public static final java.awt.image.BufferedImage convertScalarArray(
//            ScalarArray2D<?> array, double[] displayRange, ColorMap colormap)
//    {
//        // get array size
//        int sizeX = array.size(0);
//        int sizeY = array.size(1);
//        
//        // Computes the color model
//        IndexColorModel cm = createIndexColorModel(colormap);  
//        
//        // compute slope for intensity conversions
//        double extent = displayRange[1] - displayRange[0];
//        
//        // Create the AWT image
//        int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
//        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
//        
//        // Populate the raster
//        WritableRaster raster = bufImg.getRaster();
//        for (int y = 0; y < sizeY; y++)
//        {
//            for (int x = 0; x < sizeX; x++)
//            {
//                double value = array.getValue(x, y);
//                int sample = (int) Math.min(Math.max(255 * (value - displayRange[0]) / extent, 0), 255);
//                raster.setSample(x, y, 0, sample); 
//            }
//        }
//
//        return bufImg;
//    }
//    
//    /**
//     * Convert the colormap given as N-by-3 array into an IndexColorModel.
//     * 
//     * @param colormap the colormap as 256 array of 3 components
//     * @return the corresponding IndexColorModel
//     */
//    private final static IndexColorModel createIndexColorModel(int[][] colormap)
//    {
//        // Computes the color model
//        byte[] red = new byte[256];
//        byte[] green = new byte[256];
//        byte[] blue = new byte[256];
//        for(int i = 0; i < 256; i++) 
//        {
//            red[i]      = (byte) colormap[i][0];
//            green[i]    = (byte) colormap[i][1];
//            blue[i]     = (byte) colormap[i][2];
//        }
//        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
//        return cm;
//    }
//    
//    /**
//     * Convert the colormap given as N-by-3 array into an IndexColorModel.
//     * 
//     * @param colormap the colormap as 256 array of 3 components
//     * @return the corresponding IndexColorModel
//     */
//    private final static IndexColorModel createIndexColorModel(ColorMap colormap)
//    {
//        // Computes the color model
//        byte[] red = new byte[256];
//        byte[] green = new byte[256];
//        byte[] blue = new byte[256];
//        for(int i = 0; i < 256; i++) 
//        {
//            net.sci.array.color.Color color = colormap.getColor(i);
//            red[i]      = (byte) (color.red() * 255);
//            green[i]    = (byte) (color.green() * 255);
//            blue[i]     = (byte) (color.blue() * 255);
//        }
//        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
//        return cm;
//    }
//    
//	public static final java.awt.image.BufferedImage convertUInt8Array(UInt8Array array)
//	{
//		// get array size
//		int sizeX = array.size(0);
//		int sizeY = array.size(1);
//		
//		// Create the AWT image
//		int type = java.awt.image.BufferedImage.TYPE_INT_RGB;		
//		BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type);
//		WritableRaster raster = bufImg.getRaster();
//		
//		int[] pos = new int[3];
//		for (int y = 0; y < sizeY; y++)
//		{
//			pos[1] = y;
//			for (int x = 0; x < sizeX; x++)
//			{
//				pos[0] = x;
//				for (int c = 0; c < 3; c++)
//				{
//					pos[2] = c;
//					raster.setSample(x, y, c, array.getInt(pos));
//				}
//			}
//		}
//		
//		return bufImg;
//	}
//
//	public static final java.awt.image.BufferedImage convertRGB8Array(RGB8Array array)
//	{
//		// get array size
//		int sizeX = array.size(0);
//		int sizeY = array.size(1);
//		
//		// Create the AWT image
//		int type = java.awt.image.BufferedImage.TYPE_INT_RGB;
//		BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type);
//		WritableRaster raster = bufImg.getRaster();
//		
//		int[] pos = new int[3];
//		for (int y = 0; y < sizeY; y++)
//		{
//			pos[1] = y;
//			for (int x = 0; x < sizeX; x++)
//			{
//				pos[0] = x;
//				RGB8 rgb = array.get(pos);
//				for (int c = 0; c < 3; c++)
//				{
//					pos[2] = c;
//					raster.setSample(x, y, c, rgb.getSample(c));
//				}
//			}
//		}
//		
//		return bufImg;
//	}
}
