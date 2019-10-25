/**
 * 
 */
package net.sci.image.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

/**
 * Read data with various formats from a binary file taking into account
 * endianness. This is the low-level class for reading binary data.
 * Outputs can be primitive types or arrays of primitive types. 
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
	
	RandomAccessFile inputStream;
	
	ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
	
	// =============================================================
	// Constructors

	public BinaryDataReader(File file) throws IOException
	{
		this.inputStream = new RandomAccessFile(file, "r");
	}

	public BinaryDataReader(File file, ByteOrder order) throws IOException
	{
		this.inputStream = new RandomAccessFile(file, "r");
		setOrder(order);
	}

	public BinaryDataReader(RandomAccessFile raf) throws IOException
	{
		this.inputStream = raf;
	}

	public BinaryDataReader(RandomAccessFile raf, ByteOrder order) throws IOException
	{
		this.inputStream = raf;
		setOrder(order);
	}

	
	// =============================================================
	// Class methods

	public ByteOrder getOrder()
	{
	    return byteOrder;
	}
	
	public void setOrder(ByteOrder order)
	{
	    this.byteOrder = order;
	}

	
	// =============================================================
	// Read arrays
    
	/**
	 * Reads up to len bytes of data from this file into an array of bytes.
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
		return this.inputStream.read(b, off, len);
	}

	/**
	 * Reads up to b.length bytes of data from this file into an array of bytes.
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
		return this.inputStream.read(buffer);
	}
	
    public int readShortArray(short[] buffer, int off, int len) throws IOException
    {
        // read byte array of adequate length
        byte[] byteBuffer = new byte[len * 2];
        int nRead = readByteArray(byteBuffer) / 2;

        // convert byte array to short array
        for (int i = 0; i < nRead; i++)
        {
            int b1 = byteBuffer[2 * i] & 0x00FF;
            int b2 = byteBuffer[2 * i + 1] & 0x00FF;
        
            if (byteOrder == ByteOrder.LITTLE_ENDIAN)
                buffer[i] = (short) ((b2 << 8) + b1);
            else
                buffer[i] = (short) ((b1 << 8) + b2);
        }

        // return number of data read
        return nRead;
    }

    public int readIntArray(int[] buffer, int off, int len) throws IOException
    {
        // fill up array
        int pos = off;
        for (int c = 0; c < len; c++)
        {
            buffer[pos++] = (int) readInt();
        }
        
        // return number of data read
        return len;
    }

    public int readFloatArray(float[] buffer, int off, int len) throws IOException
    {
        // fill up array
        int pos = off;
        for (int c = 0; c < len; c++)
        {
            buffer[pos++] = (float) readFloat();
        }
        
        // return number of data read
        return len;
    }

    public int readDoubleArray(double[] buffer, int off, int len) throws IOException
    {
        // fill up array
        int pos = off;
        for (int c = 0; c < len; c++)
        {
            buffer[pos++] = readDouble();
        }
        
        // return number of data read
        return len;
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
        return inputStream.readByte();
    }

    /**
     * Reads the next short state from the stream.
     * 
     * @return the next short value within this stream
     * @throws IOException
     *             if an error occurs
     */
    public int readShort() throws IOException
    {
        // read bytes
        int b1 = inputStream.read();
        int b2 = inputStream.read();

        // encode bytes to short
        if (byteOrder == ByteOrder.LITTLE_ENDIAN)
            return ((b2 << 8) + b1);
        else
            return ((b1 << 8) + b2);
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
		// read bytes
		int b1 = inputStream.read();
		int b2 = inputStream.read();
		int b3 = inputStream.read();
		int b4 = inputStream.read();

		// encode bytes to integer
		if (byteOrder == ByteOrder.LITTLE_ENDIAN)
			return ((b4 << 24) + (b3 << 16) + (b2 << 8) + b1);
		else
			return ((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
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
        // read bytes
        int b1 = inputStream.read();
        int b2 = inputStream.read();
        int b3 = inputStream.read();
        int b4 = inputStream.read();

        // encode bytes to integer
        if (byteOrder == ByteOrder.LITTLE_ENDIAN)
            return Float.intBitsToFloat((b4 << 24) + (b3 << 16) + (b2 << 8) + b1);
        else
            return Float.intBitsToFloat((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
    }

    /**
     * Reads the next floating point value from the stream.
     * 
     * @return the next double value within this stream
     * @throws IOException
     *             if an error occurs
     */
    public double readDouble() throws IOException
    {
        // read bytes
        long b1 = inputStream.read();
        long b2 = inputStream.read();
        long b3 = inputStream.read();
        long b4 = inputStream.read();
        long b5 = inputStream.read();
        long b6 = inputStream.read();
        long b7 = inputStream.read();
        long b8 = inputStream.read();

        // encode bytes to integer
        if (byteOrder == ByteOrder.LITTLE_ENDIAN)
            return Double.longBitsToDouble((b8 << 56) + (b7 << 48) + (b6 << 40) + (b5 << 32) + (b4 << 24) + (b3 << 16) + (b2 << 8) + b1);
        else
            return Double.longBitsToDouble((b1 << 56) + (b2 << 48) + (b3 << 40) + (b4 << 32) + (b5 << 24) + (b6 << 16) + (b7 << 8) + b8);
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

	public void close() throws IOException
	{
		this.inputStream.close();
	}
}
