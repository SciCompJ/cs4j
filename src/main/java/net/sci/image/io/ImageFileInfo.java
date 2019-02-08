/**
 * 
 */
package net.sci.image.io;

/**
 * Information required to read data from a binary data file.
 * 
 * @author dlegland
 *
 */
public class ImageFileInfo
{
    /**
     * The set of element types managed by this class.
     */
    public enum ElementType 
    {
        BINARY(1),
        UINT8(81),
        UINT16(16),
        INT16(16),
        INT32(32),
        FLOAT32(32),
        FLOAT64(64);
        
        int bitsPerElement;
        
        private ElementType(int bitsPerElement)
        {
            this.bitsPerElement = bitsPerElement;
        }
        
        /**
         * Parses element type from its name. Should be case insensitive.
         * 
         * @param label
         *            the label of the type
         * @return the type of the element specified by label
         */
        public static final ElementType fromLabel(String label) 
        {
            if (label != null)
            {
                label = label.toLowerCase();
            }
            for (ElementType elementType : ElementType.values())
            {
                String cmp = elementType.toString().toLowerCase();
                if (cmp.equals(label))
                {
                    return elementType;
                }
            }
            throw new IllegalArgumentException("Unable to parse ElementType with label: " + label);
        }
        
        public int getBytePerElement()
        {
            return (int) Math.ceil(this.bitsPerElement / 8.0);
        }
    }

    /** Number of dimensions, should be greater than 0 when initialized */
    public int nDims = 0;

    /** The size in each dimension */
    public int[] dimSize = null;

    /** The type of element stored in this file (default is UINT8) */
    public ElementType elementType = ElementType.UINT8;
    public String elementTypeName = "";

    /** the name of the file containing the data */
    public String dataFileName = null;

    /**
     * The number of bytes to skip before reading data
     */
    public int headerSize = 0;

    // values for spatial calibration
    public double[] elementSpacing = null;
    public double[] elementSize = null;

    public boolean elementByteOrderMSB = false;
    public int elementNumberOfChannels = 1;

    public boolean binaryData = true;
    public boolean binaryDataByteOrderMSB = false;
    
//    public boolean compressedData = false;
//    public int compressedDataSize = 0;

//    public String anatomicalOrientation = "";
//    public double[] centerOfRotation = null;
//    public double[] offset = null;

    /**
     * Empty constructor
     */
    public ImageFileInfo()
    {
    }

}
