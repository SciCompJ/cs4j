/**
 * 
 */
package net.sci.image.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.Float64Array3D;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int16Array2D;
import net.sci.array.numeric.Int16Array3D;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.Int32Array2D;
import net.sci.array.numeric.Int32Array3D;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.array.numeric.UInt16Array3D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.numeric.impl.BufferedFloat32Array2D;
import net.sci.array.numeric.impl.BufferedFloat64Array2D;
import net.sci.array.numeric.impl.BufferedInt16Array2D;
import net.sci.array.numeric.impl.BufferedInt32Array2D;
import net.sci.array.numeric.impl.BufferedUInt16Array2D;
import net.sci.array.numeric.impl.BufferedUInt8Array2D;
import net.sci.array.numeric.impl.SlicedFloat32Array3D;
import net.sci.array.numeric.impl.SlicedFloat64Array3D;
import net.sci.array.numeric.impl.SlicedInt16Array3D;
import net.sci.array.numeric.impl.SlicedInt32Array3D;
import net.sci.array.numeric.impl.SlicedUInt16Array3D;
import net.sci.array.numeric.impl.SlicedUInt8Array3D;

/**
 * Read image data with various formats taking into account endianness.
 * 
 * @see java.io.DataInput
 * 
 * @author dlegland
 *
 */
public class ImageBinaryDataReader extends AlgoStub implements Closeable
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
    // Class variables
    
    RandomAccessFile inputStream;

    ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    
    
    // =============================================================
    // Constructors
    
    public ImageBinaryDataReader(File file) throws IOException
    {
        this.inputStream = new RandomAccessFile(file, "r");
    }

    public ImageBinaryDataReader(File file, ByteOrder order) throws IOException
    {
        this.inputStream = new RandomAccessFile(file, "r");
        this.byteOrder = order;
    }


    public ImageBinaryDataReader(RandomAccessFile raf) throws IOException
    {
        this.inputStream = raf;
    }

    public ImageBinaryDataReader(RandomAccessFile raf, ByteOrder order) throws IOException
    {
        this.inputStream = raf;
        this.byteOrder = order;
    }


    
    // =============================================================
    // Methods
    
    public Array<?> readArray(int[] dims, DataType type) throws IOException 
    {
        if (type == DataType.UINT8) return readUInt8Array(dims);

        throw new RuntimeException("reading of such data type not implemented: " + type);
    }
    
    public UInt8Array readUInt8Array(int[] dims) throws IOException 
    {
        switch(dims.length)
        {
        case 2:
            return readUInt8Array2D(dims[0], dims[1]);
        case 3:
            return readUInt8Array3D(dims[0], dims[1], dims[2]);
        default:
            throw new RuntimeException(String.format("Not yet implemented for array with dimensions %d", dims.length));
        }
    }
    
    public UInt8Array2D readUInt8Array2D(int sizeX, int sizeY) throws IOException
    {
        int numel = sizeX * sizeY;
        byte[] buffer = new byte[numel];
        
        @SuppressWarnings("resource")
        BinaryDataReader reader = new BinaryDataReader(inputStream, byteOrder);
        reader.readByteArray(buffer);
        
        return new BufferedUInt8Array2D(sizeX, sizeY, buffer);
    }
    
    public UInt8Array3D readUInt8Array3D(int sizeX, int sizeY, int sizeZ) throws IOException
    {
        ArrayList<UInt8Array2D> slices = new ArrayList<>(sizeZ);
        
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            slices.add(readUInt8Array2D(sizeX, sizeY));
        }
        this.fireProgressChanged(this, sizeZ, sizeZ);
        
        return new SlicedUInt8Array3D(slices);
    }
    
    
    public UInt16Array readUInt16Array(int[] dims) throws IOException 
    {
        switch(dims.length)
        {
        case 2:
            return readUInt16Array2D(dims[0], dims[1]);
        case 3:
            return readUInt16Array3D(dims[0], dims[1], dims[2]);
        default:
            throw new RuntimeException(String.format("Not yet implemented for array with dimensions %d", dims.length));
        }
    }
    
    public UInt16Array2D readUInt16Array2D(int sizeX, int sizeY) throws IOException
    {
        int numel = sizeX * sizeY;
        short[] buffer = new short[numel];
        
        @SuppressWarnings("resource")
        BinaryDataReader reader = new BinaryDataReader(inputStream, byteOrder);
        reader.readShortArray(buffer, 0, numel);
        
        return new BufferedUInt16Array2D(sizeX, sizeY, buffer);
    }
    
    public UInt16Array3D readUInt16Array3D(int sizeX, int sizeY, int sizeZ) throws IOException
    {
        ArrayList<UInt16Array2D> slices = new ArrayList<>(sizeZ);
        
        for (int z = 0; z < sizeZ; z++)
        {
            slices.add(readUInt16Array2D(sizeX, sizeY));
        }
        
        return new SlicedUInt16Array3D(slices);
    }
    
    
    public Int16Array readInt16Array(int[] dims) throws IOException 
    {
        switch(dims.length)
        {
        case 2:
            return readInt16Array2D(dims[0], dims[1]);
        case 3:
            return readInt16Array3D(dims[0], dims[1], dims[2]);
        default:
            throw new RuntimeException(String.format("Not yet implemented for array with dimensions %d", dims.length));
        }
    }
    
    public Int16Array2D readInt16Array2D(int sizeX, int sizeY) throws IOException
    {
        int numel = sizeX * sizeY;
        short[] buffer = new short[numel];
        
        @SuppressWarnings("resource")
        BinaryDataReader reader = new BinaryDataReader(inputStream, byteOrder);
        reader.readShortArray(buffer, 0, numel);
        
        return new BufferedInt16Array2D(sizeX, sizeY, buffer);
    }
    
    public Int16Array3D readInt16Array3D(int sizeX, int sizeY, int sizeZ) throws IOException
    {
        ArrayList<Int16Array2D> slices = new ArrayList<>(sizeZ);
        
        for (int z = 0; z < sizeZ; z++)
        {
            slices.add(readInt16Array2D(sizeX, sizeY));
        }
        
        return new SlicedInt16Array3D(slices);
    }
    
    
    
    public Int32Array readInt32Array(int[] dims) throws IOException 
    {
        switch(dims.length)
        {
        case 2:
            return readInt32Array2D(dims[0], dims[1]);
        case 3:
            return readInt32Array3D(dims[0], dims[1], dims[2]);
        default:
            throw new RuntimeException(String.format("Not yet implemented for array with dimensions %d", dims.length));
        }
    }
    
    public Int32Array2D readInt32Array2D(int sizeX, int sizeY) throws IOException
    {
        int numel = sizeX * sizeY;
        int[] buffer = new int[numel];
        
        @SuppressWarnings("resource")
        BinaryDataReader reader = new BinaryDataReader(inputStream, byteOrder);
        reader.readIntArray(buffer, 0, numel);
        
        return new BufferedInt32Array2D(sizeX, sizeY, buffer);
    }
    
    public Int32Array3D readInt32Array3D(int sizeX, int sizeY, int sizeZ) throws IOException
    {
        ArrayList<Int32Array2D> slices = new ArrayList<>(sizeZ);
        
        for (int z = 0; z < sizeZ; z++)
        {
            slices.add(readInt32Array2D(sizeX, sizeY));
        }
        
        return new SlicedInt32Array3D(slices);
    }

    
    public Float32Array readFloat32Array(int[] dims) throws IOException 
    {
        switch(dims.length)
        {
        case 2:
            return readFloat32Array2D(dims[0], dims[1]);
        case 3:
            return readFloat32Array3D(dims[0], dims[1], dims[2]);
        default:
            throw new RuntimeException(String.format("Not yet implemented for array with dimensions %d", dims.length));
        }
    }
    
    public Float32Array2D readFloat32Array2D(int sizeX, int sizeY) throws IOException
    {
        int numel = sizeX * sizeY;
        float[] buffer = new float[numel];
        
        @SuppressWarnings("resource")
        BinaryDataReader reader = new BinaryDataReader(inputStream, byteOrder);
        reader.readFloatArray(buffer, 0, numel);
        
        return new BufferedFloat32Array2D(sizeX, sizeY, buffer);
    }
    
    public Float32Array3D readFloat32Array3D(int sizeX, int sizeY, int sizeZ) throws IOException
    {
        ArrayList<Float32Array2D> slices = new ArrayList<>(sizeZ);
        
        for (int z = 0; z < sizeZ; z++)
        {
            slices.add(readFloat32Array2D(sizeX, sizeY));
        }
        
        return new SlicedFloat32Array3D(slices);
    }

    
    public Float64Array readFloat64Array(int[] dims) throws IOException 
    {
        switch(dims.length)
        {
        case 2:
            return readFloat64Array2D(dims[0], dims[1]);
        case 3:
            return readFloat64Array3D(dims[0], dims[1], dims[2]);
        default:
            throw new RuntimeException(String.format("Not yet implemented for array with dimensions %d", dims.length));
        }
    }
    
    public Float64Array2D readFloat64Array2D(int sizeX, int sizeY) throws IOException
    {
        int numel = sizeX * sizeY;
        double[] buffer = new double[numel];
        
        @SuppressWarnings("resource")
        BinaryDataReader reader = new BinaryDataReader(inputStream, byteOrder);
        reader.readDoubleArray(buffer, 0, numel);
        
        return new BufferedFloat64Array2D(sizeX, sizeY, buffer);
    }
    
    public Float64Array3D readFloat64Array3D(int sizeX, int sizeY, int sizeZ) throws IOException
    {
        ArrayList<Float64Array2D> slices = new ArrayList<>(sizeZ);
        
        for (int z = 0; z < sizeZ; z++)
        {
            slices.add(readFloat64Array2D(sizeX, sizeY));
        }
        
        return new SlicedFloat64Array3D(slices);
    }

    

    /**
     * Sets the file-pointer offset, measured from the beginning of this file,
     * at which the next read or write occurs.
     * 
     * @param pos
     *            the position within the file
     * 
     * @throws IOException
     *             if pos is less than 0 or if an I/O error occurs.
     */
    public void seek(long pos) throws IOException
    {
        this.inputStream.seek(pos);
    }

    /**
     * Returns the current offset in this file.
     * 
     * @return the offset from the beginning of the file, in bytes, at which the
     *         next read or write occurs.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public long getFilePointer() throws IOException
    {
        return this.inputStream.getFilePointer();
    }
    
    
    // =============================================================
    // Implements Closeable
    
    public void close() throws IOException
    {
        this.inputStream.close();
    }
}
