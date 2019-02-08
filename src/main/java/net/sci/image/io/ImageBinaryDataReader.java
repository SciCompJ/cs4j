/**
 * 
 */
package net.sci.image.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

/**
 * Read data with various formats taking into account endianness.
 * 
 * @see java.io.DataInput
 * 
 * @author dlegland
 *
 */
public class ImageBinaryDataReader implements Closeable
{
    // =============================================================
    // Class variables
    
    RandomAccessFile inputStream;

    ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    
    
    // =============================================================
    // Constructors
    
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
    
    /**
     * Reads up to <code>length</code> bytes of data from this file into an
     * array of bytes.
     * 
     * @param buffer
     *            the buffer into which the data is read.
     * @param offset
     *            the start offset in array b at which the data is written.
     * @param length
     *            the start offset in array b at which the data is written.
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public int readByteArray(byte[] buffer, int offset, int length) throws IOException
    {
        return this.inputStream.read(buffer, offset, length);
    }
    
    /**
     * Reads up to <code>length</code> short values from this file into an
     * array of short.
     * 
     * @param buffer
     *            the buffer into which the data is read.
     * @param offset
     *            the start offset in array b at which the data is written.
     * @param length
     *            the start offset in array b at which the data is written.
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public void readShortArray(short[] shortArray, int offset, int length) throws IOException
    {
        FileChannel fc = this.inputStream.getChannel();
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(length * 2);
        byteBuffer.order(this.byteOrder);
        
        fc.read(byteBuffer);
        byteBuffer.flip();
        
        ShortBuffer buffer = byteBuffer.asShortBuffer();
        buffer.get(shortArray);
    }
    
    
    // =============================================================
    // Implements Closeable
    
    public void close() throws IOException
    {
        this.inputStream.close();
    }
}
