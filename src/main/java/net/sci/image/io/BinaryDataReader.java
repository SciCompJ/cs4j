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
 * Read data with various formats taking into account endianness.
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
	
	boolean littleEndian = false;
	
	
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
		return this.littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
	}
	
	public void setOrder(ByteOrder order)
	{
		this.littleEndian = order == ByteOrder.LITTLE_ENDIAN;
	}
	
	
	
//	public String readAscii(int count, int value) throws IOException
//	{
//		// Allocate memory for string buffer
//		byte[] data = new byte[count];
//
//		// read string buffer
//		if (count <= 4)
//		{
//			// unpack integer
//			for (int i = 0; i < count; i++)
//			{
//				data[i] = (byte) (value & 0x00FF);
//				value = value >> 8;
//			}
//		}
//		else
//		{
//			// convert state to long offset for reading large buffer
//			long offset = ((long) value) & 0xffffffffL;
//
//			long pos0 = inputStream.getFilePointer();
//			inputStream.seek(offset);
//			inputStream.read(data);
//			inputStream.seek(pos0);
//		}
//
//		return new String(data);
//	}
//
//	public int[] readArray(TiffTag.Type type, int count, int value) throws IOException
//	{
//		if (count == 1)
//		{
//			return new int[] { value };
//		}
//
//		// convert to long offset for reading large buffer
//		long offset = ((long) value) & 0xffffffffL;
//
//		if (type == TiffTag.Type.SHORT)
//		{
//			return readShortArray(count, offset);
//		}
//		else
//		{
//			return readIntArray(count, offset);
//		}
//	}
//
//	public int[] readShortArray(int count, long offset) throws IOException
//	{
//		// allocate memory for result
//		int[] res = new int[count];
//
//		// save pointer location
//		long saveLoc = inputStream.getFilePointer();
//
//		// fill up array
//		inputStream.seek(offset);
//		for (int c = 0; c < count; c++)
//			res[c] = readShort();
//
//		// restore pointer and return result
//		inputStream.seek(saveLoc);
//		return res;
//	}
//
//	public int[] readIntArray(int count, long offset) throws IOException
//	{
//		// allocate memory for result
//		int[] res = new int[count];
//
//		// save pointer location
//		long saveLoc = inputStream.getFilePointer();
//
//		// fill up array
//		inputStream.seek(offset);
//		for (int c = 0; c < count; c++)
//			res[c] = readInt();
//
//		// restore pointer and return result
//		inputStream.seek(saveLoc);
//		return res;
//	}

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
	public int read(byte[] b, int off, int len) throws IOException
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
	public int read(byte[] buffer) throws IOException
	{
		return this.inputStream.read(buffer);
	}
	
	/**
	 * Reads the next integer from the stream.
	 */
	public int readInt() throws IOException
	{
		// read bytes
		int b1 = inputStream.read();
		int b2 = inputStream.read();
		int b3 = inputStream.read();
		int b4 = inputStream.read();

		// encode bytes to integer
		if (littleEndian)
			return ((b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0));
		else
			return ((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
	}

	/**
	 * Reads the next short state from the stream.
	 */
	public int readShort() throws IOException
	{
		// read bytes
		int b1 = inputStream.read();
		int b2 = inputStream.read();

		// encode bytes to short
		if (littleEndian)
			return ((b2 << 8) + b1);
		else
			return ((b1 << 8) + b2);
	}

//	/**
//	 * Read the short state stored at the specified position
//	 */
//	public int readShort(long pos) throws IOException
//	{
//		long pos0 = inputStream.getFilePointer();
//		inputStream.seek(pos);
//		int result = readShort();
//		inputStream.seek(pos0);
//		return result;
//	}
//
//	/**
//	 * Reads the rationale at the given position, as the ratio of two integers.
//	 */
//	public double readRational(long loc) throws IOException
//	{
//		long saveLoc = inputStream.getFilePointer();
//		inputStream.seek(loc);
//		int numerator = readInt();
//		int denominator = readInt();
//		inputStream.seek(saveLoc);
//
//		if (denominator != 0)
//			return (double) numerator / denominator;
//		else
//			return 0.0;
//	}

	/**
	 * Sets the file-pointer offset, measured from the beginning of this file,
	 * at which the next read or write occurs.
	 * 
	 * @throws IOException if pos is less than 0 or if an I/O error occurs.
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
