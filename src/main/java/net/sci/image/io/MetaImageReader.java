/**
 * 
 */
package net.sci.image.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import net.sci.array.Array;
import net.sci.array.data.scalar2d.BufferedUInt8Array2D;
import net.sci.array.data.scalar3d.BufferedUInt8Array3D;
import net.sci.image.Image;
import net.sci.image.io.MetaImageInfo;

/**
 * An implementation of ImageReader for Meta-Image file format.
 *
 * @author dlegland
 */
public class MetaImageReader implements ImageReader
{
	File file;

	public MetaImageReader(File file) throws IOException 
	{
		this.file = file;
	}

	public MetaImageReader(String fileName) throws IOException
	{
		this.file = new File(fileName);
	}


	@Override
	public Image readImage() throws IOException
	{
		MetaImageInfo info = readInfo(this.file);

		Array<?> data = readImageData(info);

		return new Image(data);
	}

	public MetaImageInfo readInfo(File file) throws IOException 
	{
		MetaImageInfo info = new MetaImageInfo();

		// open a buffered text reader on the file
		BufferedReader textReader = new BufferedReader(new FileReader(file));
		Scanner scanner = new Scanner(textReader);

		// Read image information until we find the 'ElementDataFile' tag
		while (info.elementDataFile == null) 
		{
			String line = readNextLine(scanner);
			Scanner lineScanner = new Scanner(line);
			lineScanner.useDelimiter("=");
			String tag = lineScanner.next().trim();
			String valueString = lineScanner.next().trim();
			lineScanner.close();

			//				System.out.println("  Found tag: " + tag + " with value: " + valueString);

			// Start by mandatory tags
			if (tag.equalsIgnoreCase("ObjectType")) 
			{
				info.ObjectTypeName = valueString;
			}
			else if (tag.equalsIgnoreCase("NDims")) 
			{
				info.nDims = Integer.parseInt(valueString);
			}
			else if (tag.equalsIgnoreCase("DimSize")) 
			{
				info.dimSize = parseIntegerArray(valueString, info.nDims);
			}
			else if (tag.equalsIgnoreCase("ElementType")) 
			{
				info.elementTypeName = valueString;
			}
			else if (tag.equalsIgnoreCase("ElementDataFile"))
			{
				info.elementDataFile = valueString;
			}

			// Some tags are commonly used for spatial calibration

			else if (tag.equalsIgnoreCase("HeaderSize")) 
			{
				info.headerSize = Integer.parseInt(valueString);
			}
			else if (tag.equalsIgnoreCase("ElementSize")) 
			{
				info.elementSize = parseDoubleArray(valueString, info.nDims);
			}
			else if (tag.equalsIgnoreCase("ElementSpacing")) 
			{
				info.elementSpacing = parseDoubleArray(valueString, info.nDims);
			}
			else if (tag.equalsIgnoreCase("ElementNuberOfChannels")) 
			{
				info.elementNumberOfChannels = Integer.parseInt(valueString);
			}
			else if (tag.equalsIgnoreCase("ElementByteOrderMSB")) 
			{
				info.elementByteOrderMSB = Boolean.parseBoolean(valueString);
			}
			else if (tag.equalsIgnoreCase("Offset")) 
			{
				info.offset = parseDoubleArray(valueString, info.nDims);
			}
			else if (tag.equalsIgnoreCase("AnatomicalOrientation")) 
			{
				info.anatomicalOrientation = valueString;
			}
			else if (tag.equalsIgnoreCase("CenterOfRotation")) 
			{
				info.centerOfRotation = parseDoubleArray(valueString, info.nDims);
			}
			else if (tag.equalsIgnoreCase("ElementSpacing")) 
			{
				info.elementSpacing = parseDoubleArray(valueString, info.nDims);
			}
			else if (tag.equalsIgnoreCase("BinaryData")) 
			{
				info.binaryData = Boolean.parseBoolean(valueString);
			}
			else if (tag.equalsIgnoreCase("BinaryDataByteOrderMSB")) 
			{
				info.binaryDataByteOrderMSB = Boolean.parseBoolean(valueString);
			}
			else if (tag.equalsIgnoreCase("CompressedData")) 
			{
				info.compressedData = Boolean.parseBoolean(valueString);
			}
			else if (tag.equalsIgnoreCase("CompressedDataSize")) 
			{
				info.compressedDataSize = Integer.parseInt(valueString);
			}

			else 
			{
				System.out.println("Unprocessed tag: " + tag);
			}

		}
		textReader.close();

		// init element size if it was not read
		if (info.elementSize == null)
		{
			if (info.elementSpacing != null) 
			{
				info.elementSize = info.elementSpacing;
			}
			else 
			{
				info.elementSize = new double[info.nDims];
				for (int d = 0; d < info.nDims; d++)
					info.elementSize[d] = 1;
			}
		}

		// init element spacing if it was not read
		if (info.elementSpacing == null)
		{
			info.elementSpacing = info.elementSize;
		}

		return info;
	}

	private String readNextLine(Scanner scanner) 
	{
		while (true) 
		{
			String line = scanner.nextLine();
			if (line.trim().isEmpty())
				continue;
			if (line.trim().startsWith("#"))
				continue;

			return line;
		}
	}

	private int[] parseIntegerArray(String string, int expectedLength)
	{
		int[] res = new int[expectedLength];

		Scanner scanner = new Scanner(string);
		for (int i = 0; i < expectedLength; i++) {
			res[i] = scanner.nextInt();
		}
		scanner.close();

		return res;
	}

	private double[] parseDoubleArray(String string, int expectedLength)
	{
		double[] res = new double[expectedLength];

		Scanner scanner = new Scanner(string);
		for (int i = 0; i < expectedLength; i++) {
			res[i] = Double.parseDouble(scanner.next());
		}
		scanner.close();

		return res;
	}

	public Array<?> readImageData(MetaImageInfo info) throws IOException 
	{
		// Size of image and of data buffer
		int nPixels = 1;
		for (int d = 0; d < info.nDims; d++)
		{
			nPixels *= info.dimSize[d];
		}
		int nBytes  = nPixels * info.elementNumberOfChannels;

		// Open binary stream on data
		File dataFile = new File(this.file.getParent(), info.elementDataFile);
		//			System.out.println("data file: " + dataFile.getAbsolutePath());
		RandomAccessFile inputStream = new RandomAccessFile(dataFile, "r");

		// allocate memory for buffer
		byte[] buffer = new byte[nBytes];

		// Read the byte array
		inputStream.seek(info.headerSize);
		int nRead = inputStream.read(buffer, 0, nBytes);

		// closes file
		inputStream.close();

		// Check all data have been read
		if (nRead != nBytes) {
			throw new IOException("Could read only " + nRead
					+ " bytes over the " + nBytes + " expected");
		}

		// TODO: process other data types
		Array<?> array;
		if (info.nDims == 2)
		{
			array = new BufferedUInt8Array2D(info.dimSize[0], info.dimSize[1], buffer);
		} 
		else if (info.nDims == 3)
		{
			array = new BufferedUInt8Array3D(info.dimSize[0], info.dimSize[1], info.dimSize[2], buffer);
		}
		else 
		{
			throw new RuntimeException("Can not manage image dimension other than 2 or 3");
		}

		return array;
	}
}