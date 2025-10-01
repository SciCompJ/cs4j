/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import net.sci.array.Array;
import net.sci.image.Image;

/**
 * Read "raw" image data from a binary file, by choosing array size, data type,
 * and byte order.
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
		Image image = new Image(readImageData());
		image.setNameFromFileName(file.getName());
		image.setFilePath(file.getPath());
		return image;
    }
    
    public Array<?> readImageData() throws IOException 
    {
        try(ImageBinaryDataReader reader = new ImageBinaryDataReader(this.file, this.byteOrder))
        {
            reader.seek(this.offset);
            
            return switch (this.type)
            {
                case UINT8 -> reader.readUInt8Array(this.size);
                case UINT16 -> reader.readUInt16Array(this.size);
                case INT16 -> reader.readInt16Array(this.size);
                case INT32 -> reader.readInt32Array(this.size);
                case FLOAT32 -> reader.readFloat32Array(this.size);
                case FLOAT64 -> reader.readFloat64Array(this.size);
                default -> throw new RuntimeException(
                        "Unable to process files with data type: " + type);
            };
        }
    }
}
