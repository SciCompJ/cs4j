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
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt8;
import net.sci.array.vector.VectorArray;
import net.sci.axis.Axis;
import net.sci.axis.CategoricalAxis;

/**
 * Interface for the "Type" of image, that provide a method for converting an
 * instance of Image into an AWT Buffered image.
 * 
 * @author dlegland
 */
public interface ImageType
{
    // =============================================================
    // Implementation of global constants

    public final static ImageType GRAYSCALE = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            
            // Check if the array contains UInt8 or UInt16 data
            Class<?> dataClass = array.elementClass();
            if (dataClass != UInt8.class && dataClass != UInt16.class)
            {
                throw new RuntimeException("Grayscale or intensity images must refer to an array of UInt8 or UInt16");
            }
            
            // convert to ScalarArray2D either by class cast or by wrapping
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray2D<?> array2d = ScalarArray2D.wrapScalar2d(ScalarArray.wrap((Array<Scalar>) array));

            // scalar images use display range and current LUT
            DisplaySettings settings = image.getDisplaySettings();
            ColorMap lut = settings.getColorMap();
            if (lut == null)
            {
                lut = ColorMaps.GRAY.createColorMap(256); 
            }

            // Computes the color model
            IndexColorModel cm = createIndexColorModel(lut);  

            // scalar images use display range and current LUT
            // compute slope for intensity conversions
            double[] displayRange = settings.getDisplayRange();
            double extent = displayRange[1] - displayRange[0];
            double val0 = displayRange[0];
            
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            
            // Create the AWT image
            BufferedImage bufImg = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_BYTE_INDEXED, cm);
            
            // Populate the raster
            WritableRaster raster = bufImg.getRaster();
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    double value = array2d.getValue(x, y);
                    int sample = (int) Math.min(Math.max(255 * (value - val0) / extent, 0), 255);
                    raster.setSample(x, y, 0, sample); 
                }
            }

            return bufImg;
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            Class<?> dataClass = array.elementClass();
            return dataClass == UInt8.class || dataClass == UInt16.class;
        }
        
        @Override
        public void setupCalibration(Image image)
        {
            image.calibration.channelAxis = new CategoricalAxis("Grayscale", Axis.Type.CHANNEL, new String[] { "Value" });
        }

        @Override
        public void setupDisplaySettings(Image image)
        {
            Class<?> dataClass = image.data.elementClass();
            if (dataClass == UInt8.class)
            {
                image.displaySettings.displayRange = new double[] { 0, 255 };
            }
            else if (dataClass == UInt16.class)
            {
                image.displaySettings.displayRange = new double[] { 0, 65535 };
            }
            else if (ScalarArray.class.isAssignableFrom(dataClass))
            {
                image.displaySettings.displayRange = new double[] { 0, 1.0 };
            }
            else
            {
                throw new RuntimeException("Grayscale or intensity images require scalar array for data");
            }        
        }

        @Override
        public String toString()
        {
            return "Grayscale";
        }
    };

    public final static ImageType INTENSITY = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            // Check if the array contains RGB8 data
            if (!(array instanceof ScalarArray))
            {
                throw new RuntimeException("Grayscale or intensity images must refer to an array of UInt8");
            }
            
            // convert to ScalarArray2D either by class cast or by wrapping
            ScalarArray2D<?> array2d = ScalarArray2D.wrapScalar2d((ScalarArray<?>) array);
            
            return createAwtImage_scalar(array2d, image.getDisplaySettings());
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return Scalar.class.isAssignableFrom(array.elementClass());
        }

        @Override
        public void setupCalibration(Image image)
        {
            image.calibration.channelAxis = new CategoricalAxis("Intensity", Axis.Type.CHANNEL, new String[] { "Value" });
        }

        @Override
        public void setupDisplaySettings(Image image)
        {
            image.displaySettings.displayRange = new double[] { 0.0, 1.0};
        }

        @Override
        public String toString()
        {
            return "Intensity";
        }
    };


    public final static ImageType DISTANCE = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            if (array.dimensionality() != 2)
            {
                throw new RuntimeException("Requires an image with array dimensionality equal to 2");
            }
            if (!(array instanceof ScalarArray))
            {
                throw new RuntimeException("Distance maps requires images containing an array of Scalars");
            }
            ScalarArray2D<?> array2d = ScalarArray2D.wrapScalar2d((ScalarArray<?>) array);
            DisplaySettings settings = image.getDisplaySettings();
            
            // extract LUT from image, or create one otherwise
            ColorMap lut = settings.getColorMap();
            if (lut == null)
            {
                lut = ColorMaps.GRAY.createColorMap(256); 
            }
    
            // Computes the color model
            IndexColorModel cm = createIndexColorModel(lut, settings.getBackgroundColor());  
            
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            
            // Create the AWT image
            BufferedImage bufImg = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_BYTE_INDEXED, cm);
           
            // Populate the raster
            WritableRaster raster = bufImg.getRaster();
            double distMax = settings.displayRange[1];
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    double value = array2d.getValue(x, y);
                    int index = (value == 0) ? 0 : (int) Math.floor((value * 254 / (distMax+0.001)) + 1);
                    raster.setSample(x, y, 0, index);
                }
            }
    
            return bufImg;
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return Scalar.class.isAssignableFrom(array.elementClass());
        }

        @Override
        public void setupCalibration(Image image)
        {
            image.calibration.channelAxis = new CategoricalAxis("Distance", Axis.Type.CHANNEL, new String[] { "Distance" });
        }
        
        @Override
        public void setupDisplaySettings(Image image)
        {
            DisplaySettings settings = image.getDisplaySettings();
            
            // updates display range
            settings.displayRange = new double[] {0.0, 255.0};
            
            // compute JET lut by default
            settings.setColorMap(ColorMaps.JET.createColorMap(255));
            settings.setBackgroundColor(RGB8.WHITE);
        }

        @Override
        public String toString()
        {
            return "Distance";
        }
    };

    public final static ImageType BINARY = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            if (array.elementClass() != Binary.class)
            {
                throw new RuntimeException("Binary images must refer to an array of Binary");
            }

            // ensure array is binary class
            BinaryArray2D binaryArray = BinaryArray2D.wrap(BinaryArray.wrap(array)); 

            RGB8 bgColor = RGB8.WHITE;
            RGB8 fgColor = RGB8.RED;

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

            int sizeX = array.size(0);
            int sizeY = array.size(1);

            // Create the AWT image
            int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
            BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);

            // Populate the raster
            WritableRaster raster = bufImg.getRaster();
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int value = binaryArray.getBoolean(x, y) ? 255 : 0;
                    raster.setSample(x, y, 0, value); 
                }
            }

            return bufImg;
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return array.elementClass() == Binary.class;
        }

        @Override
        public void setupCalibration(Image image)
        {
            image.calibration.channelAxis = new CategoricalAxis("Value", Axis.Type.CHANNEL, new String[] { "Value" });
        }

        @Override
        public void setupDisplaySettings(Image image)
        {
            image.displaySettings.displayRange = new double[]{0, 1};
        }

        @Override
        public String toString()
        {
            return "Binary";
        }
    };
    
    public final static ImageType LABEL = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            if (!(array instanceof IntArray))
            {
                throw new RuntimeException("Label images assume inner array implements IntArray");
            }
            IntArray2D<?> intArray = IntArray2D.wrap((IntArray<?>) array);

            // extract LUT from image, or create one otherwise
            ColorMap lut = image.getDisplaySettings().getColorMap();
            if (lut == null)
            {
                lut = ColorMaps.GRAY.createColorMap(256); 
            }

            // Computes the color model
            IndexColorModel cm = createIndexColorModel(lut, image.getDisplaySettings().getBackgroundColor());  
            
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            
            // Create the AWT image
            int type = java.awt.image.BufferedImage.TYPE_BYTE_INDEXED ;
            BufferedImage bufImg = new BufferedImage(sizeX, sizeY, type, cm);
            
            // Populate the raster
            WritableRaster raster = bufImg.getRaster();
            int nLabels = lut.size() - 1;
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int index = intArray.getInt(x, y);
                    if (index > 0)
                    {
                        index = ((index - 1) % nLabels) + 1;
                    }
                    raster.setSample(x, y, 0, index); 
                }
            }

            return bufImg;
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return array instanceof IntArray;
        }

        @Override
        public void setupCalibration(Image image)
        {
            image.calibration.channelAxis = new CategoricalAxis("Label", Axis.Type.CHANNEL, new String[] { "Label" });
        }

        @Override
        public void setupDisplaySettings(Image image)
        {
            // check array type
            if (!(image.data instanceof IntArray))
            {
                throw new RuntimeException("Label images require int array for data");
            }
        
            image.displaySettings.displayRange = new double[]{0, 255};
            
            // default display of label maps: Glasbey LUT and white background
            image.displaySettings.backgroundColor = RGB8.WHITE;
            ColorMap colorMap = ColorMaps.GLASBEY.createColorMap(255);
            image.displaySettings.colorMap = colorMap;
        }

        @Override
        public String toString()
        {
            return "Label";
        }
    };

    public final static ImageType COLOR = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
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
        
        public static final java.awt.image.BufferedImage createAwtImageRGB8(RGB8Array array)
        {
            // retrieve array size
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            
            BufferedImage bufImg = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
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
        private static final java.awt.image.BufferedImage createAwtImageRGB16(RGB16Array array, double displayRange[])
        {
            // compute color adjustment factor
            if (displayRange.length < 2)
            {
                throw new IllegalArgumentException("Display range must have two elements");
            }
            double v0 = displayRange[0];
            double k = 255.0 / (displayRange[1] - v0);
     
            // retrieve array size
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            
            // allocate memory for result AWT image
            BufferedImage bufImg = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
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
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            Class<?> dataClass = array.elementClass();
            return dataClass == RGB8.class || dataClass == RGB16.class;
        }

        @Override
        public void setupCalibration(Image image)
        {
            // update calibration
            String[] channelNames = new String[]{"Red", "Green", "Blue"};
            image.calibration.channelAxis = new CategoricalAxis("Channels", Axis.Type.CHANNEL, channelNames);
        }
        
        @Override
        public String toString()
        {
            return "Color";
        }

        @Override
        public void setupDisplaySettings(Image image)
        {
            // For color images, display range is applied to each channel identically.
            if (image.data.elementClass() == RGB8.class)
            {
                // (in theory not used)
                image.displaySettings.displayRange = new double[] { 0, 255 };
            }
            else if (image.data.elementClass() == RGB16.class)
            {
                // can be later adjusted
                image.displaySettings.displayRange = new double[] { 0, 65535 };
            }
        }
    };
    
    public final static ImageType COMPLEX = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            // Check if the array contains Vector data
            if (!(array instanceof VectorArray))
            {
                throw new RuntimeException("Vector images must refer to a VectorArray");
            }
            ScalarArray2D<?> norm = ScalarArray2D.wrapScalar2d(VectorArray.norm((VectorArray<?,?>) array));
            
            return createAwtImage_scalar(norm, image.getDisplaySettings());
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return array instanceof VectorArray; 
        }

        @Override
        public void setupCalibration(Image image)
        {
            // update calibration
            String[] channelNames = new String[]{"Real", "Imag"};
            image.calibration.channelAxis = new CategoricalAxis("Parts", Axis.Type.CHANNEL, channelNames);
        }
        
        @Override
        public void setupDisplaySettings(Image image)
        {
            image.displaySettings.displayRange = new double[] { 0.0, 1.0 };
        }
        
        @Override
        public String toString()
        {
            return "Complex";
        }
    };

    public final static ImageType GRADIENT = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            // Check if the array contains Vector data
            if (!(array instanceof VectorArray))
            {
                throw new RuntimeException("Vector images must refer to a VectorArray");
            }
            ScalarArray2D<?> norm = ScalarArray2D.wrapScalar2d(VectorArray.norm((VectorArray<?,?>) array));
            
            return createAwtImage_scalar(norm, image.getDisplaySettings());
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return array instanceof VectorArray; 
        }

        @Override
        public void setupCalibration(Image image)
        {
            // check image data type
            Array<?> array = image.getData();
            if (!(array instanceof VectorArray))
            {
                throw new RuntimeException("Image to calibrate must refer to an array of Vector");
            }
            
            // compute name of channels
            int nChannels = ((VectorArray<?,?>) array).channelCount();
            String[] channelNames = new String[nChannels];
            int nDigits = (int) Math.ceil(Math.log10(nChannels));
            String pattern = "G%0" + nDigits + "d";
            for (int c = 0; c < nChannels; c++)
            {
                channelNames[c] = String.format(pattern, c);
            }
            
            // update calibration
            image.calibration.channelAxis = new CategoricalAxis("Dimensions", Axis.Type.CHANNEL, channelNames);
        }
        
        @Override
        public void setupDisplaySettings(Image image)
        {
            image.displaySettings.displayRange = new double[] { 0.0, 1.0 };
        }
        
        @Override
        public String toString()
        {
            return "Gradient";
        }
    };

    public final static ImageType VECTOR = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            // Check if the array contains Vector data
            if (!(array instanceof VectorArray))
            {
                throw new RuntimeException("Vector images must refer to a VectorArray");
            }
            ScalarArray2D<?> norm = ScalarArray2D.wrapScalar2d(VectorArray.norm((VectorArray<?,?>) array));
            
            return createAwtImage_scalar(norm, image.getDisplaySettings());
        }
        
        @Override
        public void setupDisplaySettings(Image image)
        {
            image.displaySettings.displayRange = new double[] { 0.0, 1.0 };
        }
        
        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return array instanceof VectorArray; 
        }

        @Override
        public void setupCalibration(Image image)
        {
            // check image data type
            Array<?> array = image.getData();
            if (!(array instanceof VectorArray))
            {
                throw new RuntimeException("Image to calibrate must refer to an array of Vector");
            }
            
            // compute name of channels
            int nChannels = ((VectorArray<?,?>) array).channelCount();
            String[] channelNames = new String[nChannels];
            int nDigits = (int) Math.ceil(Math.log10(nChannels));
            String pattern = "C%0" + nDigits + "d";
            for (int c = 0; c < nChannels; c++)
            {
                channelNames[c] = String.format(pattern, c);
            }
            
            // update calibration
            image.calibration.channelAxis = new CategoricalAxis("Channels", Axis.Type.CHANNEL, channelNames);
        }
        
        @Override
        public String toString()
        {
            return "Vector";
        }
    };

    public final static ImageType UNKNOWN = new ImageType()
    {
        @Override
        public BufferedImage createAwtImage(Image image)
        {
            // Check adequacy of array type with image type
            Array<?> array = image.getData();
            checkDimensionalityIs2(array);
            
            // Computes 'empty' color model
            byte[] red = new byte[256];
            byte[] green = new byte[256];
            byte[] blue = new byte[256];
            IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  

            int sizeX = array.size(0);
            int sizeY = array.size(1);

            // Create the AWT image
            BufferedImage bufImg = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_BYTE_INDEXED, cm);

            // Populate the raster with value 0
            WritableRaster raster = bufImg.getRaster();
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    raster.setSample(x, y, 0, 0); 
                }
            }

            return bufImg;
        }

        @Override
        public boolean isCompatibleWith(Array<?> array)
        {
            return true;
        }

        @Override
        public void setupCalibration(Image image)
        {
        }
        
        @Override
        public void setupDisplaySettings(Image image)
        {
        }
        
        @Override
        public String toString()
        {
            return "Unknown";
        }
    };
    
        
    // =============================================================
    // Declaration of interface methods

    /**
     * Converts the input (2D) image into an instance of AWT BufferedImage that
     * can be easily displayed. The type and representation of the raster are
     * managed by ImageType specializations.
     * 
     * @param image
     *            the image to convert (must have dimensionality 2).
     * @return an instance of BufferedImage the same size as the input image.
     */
    public java.awt.image.BufferedImage createAwtImage(Image image);
    
    public boolean isCompatibleWith(Array<?> array);
    
    /**
     * Updates the image calibration from array size and data type. This
     * method is intended to be called during initialization of Image class.
     * Updates some settings such as channel names.
     * 
     * @param array
     *            the image data
     */
    public void setupCalibration(Image image);
    
    /**
     * Setup some default settings for the display of the image. Could iterate
     * over image values to determine value range.
     * 
     * @param image the image whose display settings need update
     */
    public void setupDisplaySettings(Image image);
    
    
    // =============================================================
    // Static utility methods

    private static void checkDimensionalityIs2(Array<?> array)
    {
        if (array.dimensionality() != 2)
        {
            throw new RuntimeException("Requires an image with array dimensionality equal to 2");
        }
    }
    
    
    /**
     * Factorization of the method used by grayscale and intensity images. Also used by vector image after co
     * 
     * @param image
     *            the image to convert
     * @return and instance of awt BufferedImage that can be displayed
     */
    private static BufferedImage createAwtImage_scalar(ScalarArray2D<?> array, DisplaySettings settings)
    {
        // extract LUT from image, or create one otherwise
        ColorMap lut = settings.getColorMap();
        if (lut == null)
        {
            lut = ColorMaps.GRAY.createColorMap(256); 
        }

        // Computes the color model
        IndexColorModel cm = createIndexColorModel(lut);  

        // scalar images use display range and current LUT
        double[] displayRange = settings.getDisplayRange();

        // compute slope for intensity conversions
        double extent = displayRange[1] - displayRange[0];

        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // Create the AWT image
        BufferedImage bufImg = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_BYTE_INDEXED, cm);

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
                int index = (int) Math.min(Math.max(255 * (value - displayRange[0]) / extent, 0), 255);

                raster.setSample(x, y, 0, index); 
            }
        }

        return bufImg;
    }

    /**
     * Convert the specified colormap into an IndexColorModel.
     * 
     * @param colormap the colormap as 256 array of 3 components
     * @return the corresponding IndexColorModel
     */
    private static IndexColorModel createIndexColorModel(ColorMap colormap)
    {
        // Computes the color model
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];
        int nColors = colormap.size();
        for(int i = 0; i < 256; i++) 
        {
            Color color = colormap.getColor(i % nColors);
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
    private static IndexColorModel createIndexColorModel(ColorMap colormap, Color background)
    {
        // allocate color components arrays
        byte[] red = new byte[256];
        byte[] green = new byte[256];
        byte[] blue = new byte[256];

        // first color corresponds to background
        red[0] = (byte) (background.red() * 255);
        green[0] = (byte) (background.green() * 255);
        blue[0] = (byte) (background.blue() * 255);

        // convert colormap colors
        int nColors = Math.min(colormap.size(), 255);
        for(int i = 0; i < nColors; i++) 
        {
            Color color = colormap.getColor(i);
            red[i+1]    = (byte) (color.red() * 255);
            green[i+1]  = (byte) (color.green() * 255);
            blue[i+1]   = (byte) (color.blue() * 255);
        }
        IndexColorModel cm = new IndexColorModel(8, 256, red, green, blue);  
        return cm;
    }
}
