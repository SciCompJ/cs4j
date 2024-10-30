/**
 * 
 */
package net.sci.image.io;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;

/**
 * MetaImage file format manager. Contains declaration of constants, and handles
 * file info.
 */
public final class MetaImageInfo
{
    /**
     * The set of element types managed by this class.
     */
    public enum ElementType
    {
        UINT8(1, "MET_UCHAR"), 
        UINT16(2, "MET_USHORT"), 
        INT16(2, "MET_SHORT"), 
        INT32(4, "MET_INT"), 
        FLOAT32(4, "MET_FLOAT"), 
        FLOAT64(8, "MET_DOUBLE");
        
        int bytesPerElement;
        
        String metString;
        
        private ElementType(int bytesPerElement, String metString)
        {
            this.bytesPerElement = bytesPerElement;
            this.metString = metString;
        }
        
        /**
         * Used to parse element type from metaImage files.
         * 
         * @param metString
         *            the value in the meta image header file
         * @return the MetaImage.ElementType corresponding to the string
         */
        public static final ElementType parseMET(String metString)
        {
            if (metString.equalsIgnoreCase("MET_UCHAR")) return ElementType.UINT8;
            if (metString.equalsIgnoreCase("MET_USHORT")) return ElementType.UINT16;
            if (metString.equalsIgnoreCase("MET_SHORT")) return ElementType.INT16;
            if (metString.equalsIgnoreCase("MET_INT")) return ElementType.INT32;
            if (metString.equalsIgnoreCase("MET_FLOAT")) return ElementType.FLOAT32;
            if (metString.equalsIgnoreCase("MET_DOUBLE")) return ElementType.FLOAT64;
            
            // unknown value
            throw new IllegalArgumentException("Unable to parse ElementType with label: " + metString);
        }
        
        /**
         * Determines the most appropriate element type for storing data of the
         * specified array, or throw an exception if none can be found.
         * 
         * @param array
         *            an array
         * @return the most appropriate element type for storing data of the
         *         array
         */
        public static final ElementType fromArrayClass(Array<?> array)
        {
            if (array instanceof UInt8Array || array instanceof BinaryArray)
            {
                return MetaImageInfo.ElementType.UINT8;
            }
            else if (array instanceof UInt16Array)
            {
                return MetaImageInfo.ElementType.UINT16;
            }
            else if (array instanceof Int16Array)
            {
                return MetaImageInfo.ElementType.INT16;
            }
            else if (array instanceof Int32Array)
            {
                return MetaImageInfo.ElementType.INT32;
            }
            else if (array instanceof Float32Array)
            {
                return MetaImageInfo.ElementType.FLOAT32;
            }
            else if (array instanceof Float64Array)
            {
                return MetaImageInfo.ElementType.FLOAT64;
            }
            else
            {
                throw new IllegalArgumentException(
                        "Unable to determine MetaImage ElementType for array with class: "
                                + array.getClass());
            }
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
            return this.bytesPerElement;
        }
        
        public String getMetString()
        {
            return this.metString;
        }
    }
    
    public String ObjectTypeName = "";
    
    /** Number of dimensions, should be greater than 0 when initialized */
    public int nDims = 0;
    
    /** The size in each dimension */
    public int[] dimSize = null;
    
    /** the type of element stored in this file */
    public ElementType elementType = ElementType.UINT8;
    public String elementTypeName = "";
    
    /** the name of the file containing the data */
    public String elementDataFile = null;
    
    /** the size of the header (determined only when reading files). */
    public long headerSize = 0L;
    
    // values for spatial calibration
    public double[] elementSpacing = null;
    public double[] elementSize = null;
    public double[] offset = null;
    /** A rotation matrix, given as nine coefficients */
    public String orientation = "";
    
    public boolean elementByteOrderMSB = false;
    public int elementNumberOfChannels = 1;
    
    public boolean binaryData = true;
    public boolean binaryDataByteOrderMSB = false;
    public boolean compressedData = false;
    public int compressedDataSize = 0;
    
    /** a three-letters idenifier of the anatomcal orientation */
    public String anatomicalOrientation = "";
    public double[] centerOfRotation = null;
}
