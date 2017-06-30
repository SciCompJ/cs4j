/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import net.sci.array.Array;
import net.sci.array.data.Float32Array;
import net.sci.array.data.Float64Array;
import net.sci.array.data.Int16Array;
import net.sci.array.data.Int32Array;
import net.sci.array.data.UInt16Array;
import net.sci.array.data.UInt8Array;
import net.sci.image.Image;

/**
 * @author dlegland
 *
 */
public class RawImageReader implements ImageReader
{
    /**
     * The set of element types managed by this class.
     */
    public enum DataType 
    {
        UINT8(1),
        UINT16(2),
        INT16(2),
        INT32(4),
        FLOAT32(4),
        FLOAT64(8);
        
        int bytesPerElement;
        
        private DataType(int bytesPerElement)
        {
            this.bytesPerElement = bytesPerElement;
        }
        
        /**
         * Parses element type from its name. Should be case insensitive.
         * 
         * @param label
         *            the label of the type
         * @return the DataType instance corresponding to the label
         */
        public static final DataType fromLabel(String label) 
        {
            if (label != null)
            {
                label = label.toLowerCase();
            }
            for (DataType elementType : DataType.values())
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
    }

    // =============================================================
    // class variables

    File file;

    int[] size;
    
    DataType type;
    
    ByteOrder byteOrder;
    
    long offset = 0L;
    
    
    // =============================================================
    // Constructors

    public RawImageReader(File file, int[] size, DataType type) throws IOException 
    {
        this(file, size, type, ByteOrder.LITTLE_ENDIAN);
    }

    public RawImageReader(File file, int[] size, DataType type, ByteOrder byteOrder) throws IOException 
    {
        this.file = file;
        this.size = size;
        this.type = type;
        this.byteOrder= byteOrder;
    }

    
    // =============================================================
    // Accessors and mutators

    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    

    // =============================================================
    // Implementation of ImageReader interface

    /* (non-Javadoc)
     * @see net.sci.image.io.ImageReader#readImage()
     */
    @Override
    public Image readImage() throws IOException
    {
        return new Image(readImageData());
    }
    
    public Array<?> readImageData() throws IOException 
    {
        switch(this.type)
        {
        case UINT8:
            return UInt8Array.create(this.size, readByteData());
        
        case UINT16:
            return UInt16Array.create(this.size, readShortData());

        case INT16:
            return Int16Array.create(this.size, readShortData());
            
        case INT32:
            return Int32Array.create(this.size, readIntData());

        case FLOAT32:
            return Float32Array.create(this.size, readFloatData());

        case FLOAT64:
            return Float64Array.create(this.size, readDoubleData());

        default:
            throw new RuntimeException("Unable to process files with data type: " + type);
        }
    }
    
    private byte[] readByteData() throws IOException
    {
        BinaryDataReader reader = new BinaryDataReader(this.file, this.byteOrder);
        
        // Size of image and of data buffer
        int nPixels = computeElementNumber();
        int nBytes  = nPixels;

        // allocate memory for buffer
        byte[] buffer = new byte[nBytes];
        
        // Read the byte array
        reader.seek(this.offset);
        int nRead = reader.read(buffer, 0, nBytes);
        
        // closes file
        reader.close();
        
        // Check all data have been read
        if (nRead != nBytes) 
        {
            throw new IOException("Could read only " + nRead
                    + " bytes over the " + nBytes + " expected");
        }
        
        return buffer;
//        // Open binary stream on data
//        RandomAccessFile inputStream = new RandomAccessFile(this.file, "r");
//
//        // allocate memory for buffer
//        // Size of image and of data buffer
//        int nPixels = computeElementNumber();
//        int nBytes  = nPixels;
//        byte[] buffer = new byte[nBytes];
//
//        // Read the byte array
//        inputStream.seek(this.offset);
//        int nRead = inputStream.read(buffer, 0, nBytes);
//
//        // closes file
//        inputStream.close();
//
//        // Check all data have been read
//        if (nRead != nBytes) 
//        {
//            throw new IOException("Could read only " + nRead
//                    + " bytes over the " + nBytes + " expected");
//        }
//
//        return UInt8Array.create(this.size, buffer);
    }

    private short[] readShortData() throws IOException
    {
        BinaryDataReader reader = new BinaryDataReader(this.file, this.byteOrder);
        
        // Size of image and of data buffer
        int nPixels = computeElementNumber();
        
        // allocate memory for buffer
        short[] buffer = new short[nPixels];
        
        // Read the byte array
        reader.seek(this.offset);
        int nRead = reader.readShortArray(buffer, 0, nPixels);
        
        // closes file
        reader.close();
        
        // Check all data have been read
        if (nRead != nPixels) 
        {
            throw new IOException("Could read only " + nRead
                    + " shorts over the " + nPixels + " expected");
        }
        
        return buffer;
    }
    
    private int[] readIntData() throws IOException
    {
        BinaryDataReader reader = new BinaryDataReader(this.file, this.byteOrder);
        
        // Size of image and of data buffer
        int nPixels = computeElementNumber();
        
        // allocate memory for buffer
        int[] buffer = new int[nPixels];
        
        // Read the byte array
        reader.seek(this.offset);
        int nRead = reader.readIntArray(buffer, 0, nPixels);
        
        // closes file
        reader.close();
        
        // Check all data have been read
        if (nRead != nPixels) 
        {
            throw new IOException("Could read only " + nRead
                    + " shorts over the " + nPixels + " expected");
        }
        
        return buffer;
    }
    
    private float[] readFloatData() throws IOException
    {
        BinaryDataReader reader = new BinaryDataReader(this.file, this.byteOrder);
        
        // Size of image and of data buffer
        int nPixels = computeElementNumber();
        
        // allocate memory for buffer
        float[] buffer = new float[nPixels];
        
        // Read the byte array
        reader.seek(this.offset);
        int nRead = reader.readFloatArray(buffer, 0, nPixels);
        
        // closes file
        reader.close();
        
        // Check all data have been read
        if (nRead != nPixels) 
        {
            throw new IOException("Could read only " + nRead
                    + " shorts over the " + nPixels + " expected");
        }
        
        return buffer;
    }
    
    private double[] readDoubleData() throws IOException
    {
        BinaryDataReader reader = new BinaryDataReader(this.file, this.byteOrder);
        
        // Size of image and of data buffer
        int nPixels = computeElementNumber();
        
        // allocate memory for buffer
        double[] buffer = new double[nPixels];
        
        // Read the byte array
        reader.seek(this.offset);
        int nRead = reader.readDoubleArray(buffer, 0, nPixels);
        
        // closes file
        reader.close();
        
        // Check all data have been read
        if (nRead != nPixels) 
        {
            throw new IOException("Could read only " + nRead
                    + " shorts over the " + nPixels + " expected");
        }
        
        return buffer;
    }
    
    private int computeElementNumber()
    {
        int number = 1;
        for (int d : this.size)
        {
            number *= d;
        }
        return number;
    }
    
}
