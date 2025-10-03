/**
 * 
 */
package net.sci.image.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Read data with various formats from a binary file, by taking into account
 * byte-order. This is the low-level class for reading binary data.
 * 
 * Provides outputs in various formats:
 * <ul>
 * <li>primitive type,</li>
 * <li>array of primitive type,</li>
 * <li>String,</li>
 * <li>...</li>
 * </ul>
 * 
 * Access to data is performed through a {@code RandomAccessFile}. The position
 * within the file can be changed via the {@code seek(long)} method, that calls
 * the corresponding method of the {@RandomAccessFile}.
 * 
 * @see java.io.DataInput
 * 
 * @author dlegland
 *
 */
public class BinaryDataReader implements Closeable
{
    // =============================================================
    // Class variables

    /**
     * The random-access file used to read binary data, initialized at class
     * instantiation.
     */
    RandomAccessFile raf;

    /**
     * The byte order of the data stream. Default is Little-endian.
     */
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    
    // =============================================================
    // Constructors

    public BinaryDataReader(File file) throws IOException
    {
        this.raf = new RandomAccessFile(file, "r");
    }

    public BinaryDataReader(File file, ByteOrder order) throws IOException
    {
        this.raf = new RandomAccessFile(file, "r");
        setOrder(order);
    }

    public BinaryDataReader(RandomAccessFile raf) throws IOException
    {
        this.raf = raf;
    }

    public BinaryDataReader(RandomAccessFile raf, ByteOrder order) throws IOException
    {
        this.raf = raf;
        setOrder(order);
    }
    

    // =============================================================
    // Class methods

    /**
     * Retrieve the byte order associated to this data reader.
     * 
     * @return the byte order of this reader
     */
    public ByteOrder getOrder()
    {
        return byteOrder;
    }

    /**
     * Changes the byte order of this data reader
     * 
     * @param order
     *            the new value of byte order.
     */
    public void setOrder(ByteOrder order)
    {
        this.byteOrder = order;
    }
    

    // =============================================================
    // Read arrays

    /**
     * Reads up to <code>len</code> bytes of data from this reader into an array
     * of bytes.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset in array b at which the data is written.
     * @param len
     *            the start offset in array b at which the data is written.
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of the file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public int readByteArray(byte[] b, int off, int len) throws IOException
    {
        return this.raf.read(b, off, len);
    }

    /**
     * Reads up to <code>buffer.length</code> bytes of data from this reader
     * into an array of bytes.
     * 
     * @param buffer
     *            the buffer into which the data is read.
     * @return the total number of bytes read into the buffer, or -1 if there is
     *         no more data because the end of this file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public int readByteArray(byte[] buffer) throws IOException
    {
        return this.raf.read(buffer);
    }

    /**
     * Reads up to <code>n</code> short values from this reader, and populates
     * the specified array.
     * 
     * @param shortArray
     *            the array that will contain the values (must have length equal
     *            to at least offset+n).
     * @param offset
     *            starting position in the destination array
     * @param n
     *            the number of values to read
     * @return the total number of values read, or -1 if there is no more data
     *         because the end of this file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public int readShortArray(short[] shortArray, int offset, int n) throws IOException
    {
        // read byte array of adequate length
        byte[] byteArray = new byte[n * 2];
        int nRead = readByteArray(byteArray) / 2;

        ByteBuffer.wrap(byteArray).order(byteOrder).asShortBuffer().get(shortArray, offset, n);

        // return number of data read
        return nRead;
    }

    /**
     * Reads up to <code>n</code> integer values from this reader, and populates
     * the specified array.
     * 
     * @param intArray
     *            the array that will contain the values (must have length equal
     *            to at least offset+n).
     * @param offset
     *            starting position in the destination array
     * @param n
     *            the number of values to read
     * @return the total number of values read, or -1 if there is no more data
     *         because the end of this file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public int readIntArray(int[] intArray, int offset, int n) throws IOException
    {
        // read byte array of adequate length
        byte[] byteArray = new byte[n * 4];
        int nRead = readByteArray(byteArray) / 4;

        ByteBuffer.wrap(byteArray).order(byteOrder).asIntBuffer().get(intArray, offset, n);

        // return number of data read
        return nRead;
    }

    /**
     * Reads up to <code>n</code> float values from this reader, and populates
     * the specified array.
     * 
     * @param shortArray
     *            the array that will contain the values (must have length equal
     *            to at least offset+n).
     * @param offset
     *            starting position in the destination array
     * @param n
     *            the number of values to read
     * @return the total number of values read, or -1 if there is no more data
     *         because the end of this file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public int readFloatArray(float[] floatArray, int offset, int n) throws IOException
    {
        // read byte array of adequate length
        byte[] byteArray = new byte[n * 4];
        int nRead = readByteArray(byteArray) / 4;

        ByteBuffer.wrap(byteArray).order(byteOrder).asFloatBuffer().get(floatArray, offset, n);

        // return number of data read
        return nRead;
    }

    /**
     * Reads up to <code>n</code> double precision floating-point values
     * ("double") from this reader, and populates the specified array.
     * 
     * @param shortArray
     *            the array that will contain the values (must have length equal
     *            to at least offset+n).
     * @param offset
     *            starting position in the destination array
     * @param n
     *            the number of values to read
     * @return the total number of values read, or -1 if there is no more data
     *         because the end of this file has been reached.
     * @throws IOException
     *             If the first byte cannot be read for any reason other than
     *             end of file
     */
    public int readDoubleArray(double[] doubleArray, int offset, int n) throws IOException
    {
        // read byte array of adequate length
        byte[] byteArray = new byte[n * 8];
        int nRead = readByteArray(byteArray) / 8;

        ByteBuffer.wrap(byteArray).order(byteOrder).asDoubleBuffer().get(doubleArray, offset, n);

        // return number of data read
        return nRead;
    }

    
    // =============================================================
    // Read primitive types

    /**
     * Reads the next byte from the stream.
     * 
     * @return the next byte value within this stream
     * @throws IOException
     *             if an error occurs
     */
    public byte readByte() throws IOException
    {
        return raf.readByte();
    }

    /**
     * Reads the next short state from the stream.
     * 
     * @return the next short value within this stream
     * @throws IOException
     *             if an error occurs
     */
    public short readShort() throws IOException
    {
        // read byte array of adequate length
        byte[] byteArray = new byte[2];
        readByteArray(byteArray);

        // convert to short by wrapping to a short buffer 
        return ByteBuffer.wrap(byteArray).order(byteOrder).asShortBuffer().get();
    }

   /**
    * Reads the next integer from the stream.
    * 
    * @return the next int value within this stream
    * @throws IOException
    *             if an error occurs
    */
   public int readInt() throws IOException
   {
       // read byte array of adequate length
       byte[] byteArray = new byte[4];
       readByteArray(byteArray);

       // convert to int by wrapping to a int buffer 
       return ByteBuffer.wrap(byteArray).order(byteOrder).asIntBuffer().get();
   }

   /**
     * Reads the next floating point value from the stream.
     * 
     * @return the next float value within this stream
     * @throws IOException
     *             if an error occurs
     */
    public float readFloat() throws IOException
    {
        // read byte array of adequate length
        byte[] byteArray = new byte[4];
        readByteArray(byteArray);

        // convert to float by wrapping to a float buffer 
        return ByteBuffer.wrap(byteArray).order(byteOrder).asFloatBuffer().get();
    }

    /**
     * Reads the next double preision floating point value from the stream.
     * 
     * @return the next double value within this stream
     * @throws IOException
     *             if an error occurs
     */
    public double readDouble() throws IOException
    {
        // read byte array of adequate length
        byte[] byteArray = new byte[8];
        readByteArray(byteArray);

        // convert to double by wrapping to a double buffer 
        return ByteBuffer.wrap(byteArray).order(byteOrder).asDoubleBuffer().get();
    }
    
    /**
     * Reads a string with the specified number of characters. The number of
     * bytes read is twice the number of characters.
     * 
     * @return the string obtained by reading the next {@code nChars}
     *         characters.
     * @throws IOException
     *             if an error occurs
     */
    public String readString(int nChars) throws IOException
    {
        int n = nChars * 2;
        byte[] buffer = new byte[n];
        this.raf.read(buffer, 0, n);
        return ByteBuffer.wrap(buffer).order(this.byteOrder).asCharBuffer().toString();
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
        this.raf.seek(pos);
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
        return this.raf.getFilePointer();
    }

    /**
     * Closes this reader, and the underlying file.
     */
    public void close() throws IOException
    {
        this.raf.close();
    }
}
