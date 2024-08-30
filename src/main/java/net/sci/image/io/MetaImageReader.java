/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Scanner;

import net.sci.array.Array;
import net.sci.array.numeric.impl.FileMappedFloat32Array3D;
import net.sci.array.numeric.impl.FileMappedInt16Array3D;
import net.sci.array.numeric.impl.FileMappedUInt16Array3D;
import net.sci.array.numeric.impl.FileMappedUInt8Array3D;
import net.sci.image.Calibration;
import net.sci.image.Image;

/**
 * An implementation of ImageReader for Meta-Image file format.
 *
 * @author dlegland
 */
public class MetaImageReader implements ImageReader
{
    /**
     * Opens a MetaImage file containing header, and returns the file info
     * required to read the binary data as an instance of MetaImageInfo.
     * 
     * A large number of tags is managed. In case an unknown tag is encountered,
     * a warning is displayed on current output stream.
     * 
     * @param file
     *            the file containing the header of the MetaImage file
     * @return the MetaImageInfo instance containing parsed info
     * @throws IOException
     *             if file was not found or was malformed
     */
    public static final MetaImageInfo readFileInfo(File file) throws IOException
    {
        // create new empty file info
        MetaImageInfo info = new MetaImageInfo();
        
        // open file for reading binary data, keeping possibility to store file offset
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        
        // Read image information until we find the 'ElementDataFile' tag
        while (info.elementDataFile == null) 
        {
            // read next line (parse binary data), and split into tag and value
            String line = readNextLine(raf);
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter("=");
            String tag = lineScanner.next().trim();
            String valueString = lineScanner.next().trim();
            lineScanner.close();

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
                info.elementType = MetaImageInfo.ElementType.parseMET(valueString);
            }
            else if (tag.equalsIgnoreCase("ElementDataFile"))
            {
                info.elementDataFile = valueString;
                if (valueString.compareToIgnoreCase("LOCAL") == 0)
                {
                    info.elementDataFile = file.getName();
                    info.headerSize = raf.getFilePointer();
                }
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
            else if (tag.equalsIgnoreCase("ElementNumberOfChannels")) 
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
            else if (tag.equalsIgnoreCase("Orientation")) 
            {
                info.orientation = valueString;
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
        raf.close();

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
    
    private final static String readNextLine(RandomAccessFile raf) throws IOException
    {
        StringBuffer buffer = new StringBuffer();
        while (true)
        {
            byte b = raf.readByte();
            if (b == (byte) 0x0A) break;
            buffer.append((char) b);
        }
        return buffer.toString();
    }
    
    private static final int[] parseIntegerArray(String string, int expectedLength)
    {
        int[] res = new int[expectedLength];
        
        Scanner scanner = new Scanner(string);
        for (int i = 0; i < expectedLength; i++)
        {
            res[i] = scanner.nextInt();
        }
        scanner.close();
        
        return res;
    }
    
    private static final double[] parseDoubleArray(String string, int expectedLength)
    {
        double[] res = new double[expectedLength];
        
        Scanner scanner = new Scanner(string);
        for (int i = 0; i < expectedLength; i++)
        {
            res[i] = Double.parseDouble(scanner.next());
        }
        scanner.close();
        
        return res;
    }
    
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
        MetaImageInfo info = readFileInfo(this.file);
        
        Array<?> data;
        
        boolean virtualType = 
                info.elementType == MetaImageInfo.ElementType.UINT8
                || info.elementType == MetaImageInfo.ElementType.UINT16
                || info.elementType == MetaImageInfo.ElementType.INT16
                || info.elementType == MetaImageInfo.ElementType.FLOAT32;
        if (info.nDims == 3 && virtualType)
        {
            data = readVirtualImageData(info);
        }
        else
        {
            data = readImageData(info);
        }
        
        Image image = new Image(data);
        image.setNameFromFileName(file.getName());
        image.setFilePath(file.getPath());
        
        // Update image spatial calibration
        double[] spacing = null;
        double[] origin = null;
        if (info.elementSize != null) spacing = info.elementSize;
        if (info.elementSpacing != null) spacing = info.elementSpacing;
        if (info.offset != null) origin = info.offset;
        if (spacing != null)
        {
            if (origin == null) origin = new double[spacing.length];
            Calibration calib = image.getCalibration();
            calib.setSpatialCalibration(spacing, origin, "");        
        }
        
        return image;
    }
    
    public Array<?> readImageData(MetaImageInfo info) throws IOException
    {
        File dataFile = new File(this.file.getParent(), info.elementDataFile);
        ByteOrder order = info.binaryDataByteOrderMSB ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
        ImageBinaryDataReader reader = new ImageBinaryDataReader(dataFile, order);
        reader.seek(info.headerSize);
        
        Array<?> array = switch (info.elementType)
        {
            case UINT8   -> reader.readUInt8Array(info.dimSize);
            case UINT16  -> reader.readUInt16Array(info.dimSize);
            case INT16   -> reader.readInt16Array(info.dimSize);
            case INT32   -> reader.readInt32Array(info.dimSize);
            case FLOAT32 -> reader.readFloat32Array(info.dimSize);
            case FLOAT64 -> reader.readFloat64Array(info.dimSize);
            
            // case BOOLEAN:
            default ->
            {
                reader.close();
                throw new RuntimeException("Unable to process files with data type: " + info.elementTypeName);
            }
        };
        
        reader.close();
        return array;
    }
    
    public Array<?> readVirtualImageData(MetaImageInfo info) throws IOException 
    {
        // check data validity
        if (info.dimSize.length != 3)
        {
            throw new RuntimeException("File-mapped arrays can be only 3D images");
        }
        
        // retrieve information
        File dataFile = new File(this.file.getParent(), info.elementDataFile);
        String path = dataFile.getAbsolutePath();
        long offset = info.headerSize;
        int size0 = info.dimSize[0];
        int size1 = info.dimSize[1];
        int size2 = info.dimSize[2];
        ByteOrder order = info.binaryDataByteOrderMSB ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;

        return switch(info.elementType)
        {
            case UINT8   -> new FileMappedUInt8Array3D(path, offset, size0, size1, size2);
            case UINT16  -> new FileMappedUInt16Array3D(path, offset, size0, size1, size2, order);
            case INT16   -> new FileMappedInt16Array3D(path, offset, size0, size1, size2, order);
            case FLOAT32 -> new FileMappedFloat32Array3D(path, offset, size0, size1, size2, order);
            
            default -> throw new RuntimeException("Unable to process files with data type: " + info.elementTypeName);
        };
    }
}