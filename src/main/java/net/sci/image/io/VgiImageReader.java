/**
 * 
 */
package net.sci.image.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;

import net.sci.array.data.scalar3d.BufferedUInt16Array3D;
import net.sci.array.data.scalar3d.UInt16Array3D;
import net.sci.image.Image;
import net.sci.image.SpatialCalibration;

/**
 * @author dlegland
 *
 */
public class VgiImageReader implements ImageReader
{
    File file;

    public VgiImageReader(File file) throws IOException 
    {
        this.file = file;
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.io.ImageReader#readImage()
     */
    @Override
    public Image readImage() throws IOException
    {
        String dataFileName = null;
        int sizeX = 0;
        int sizeY = 0;
        int sizeZ = 0;
        int bitDepth = 0;
        boolean littleEndian = true;
        
        SpatialCalibration calib = new SpatialCalibration(3);
        
        try (LineNumberReader reader = new LineNumberReader(new FileReader(file))) 
        { 
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                //          System.out.println(reader.getLineNumber() + ": " + line);

                if (line.startsWith("{") && line.endsWith("}"))
                {
                    // Process new volume
                    //              String volumeName = line.substring(1, line.length()-1);
                    //              System.out.println("New volume: " + volumeName);
                }
                else if (line.startsWith("[") && line.endsWith("]"))
                {
                    // Process new information block
                    //              String blockName = line.substring(1, line.length()-1);
                    //              System.out.println("  New block: " + blockName);

                }
                else
                {
                    // process new key-value pair
                    String[] tokens = line.split("=");
                    if (tokens.length != 2)
                    {
                        System.err.println(String.format("Token count error at line %d: %s", reader.getLineNumber(), line));
                        continue;
                    }

                    String key = tokens[0].trim();
                    String valueString = tokens[1].trim();

                    if ("size".equalsIgnoreCase(key))
                    {
                        tokens = valueString.split(" ");
                        if (tokens.length != 3)
                        {
                            System.err.println(String.format("Assume three integer values at line %d: %s", reader.getLineNumber(), line));
                            continue;
                        }

                        sizeX = Integer.parseInt(tokens[0]);
                        sizeY = Integer.parseInt(tokens[1]);
                        sizeZ = Integer.parseInt(tokens[2]);
                    }
                    else if ("bitsperelement".equalsIgnoreCase(key))
                    {
                        bitDepth = Integer.parseInt(valueString);
                        if (bitDepth != 16)
                        {
                            throw new RuntimeException("Only 16 bits per elements are currently supported, not " + bitDepth);
                        }
                    }
                    else if ("name".equalsIgnoreCase(key))
                    {
                        if (dataFileName == null)
                        {
                            System.out.println("data file name: "  + valueString);
                            dataFileName = valueString;
                        }
                    }
                    else if ("resolution".equalsIgnoreCase(key))
                    {
                        // read spatial calibration of voxel
                        tokens = valueString.split(" ");
                        if (tokens.length != 3)
                        {
                            System.err.println("Could not parse spatial resolution from line:" + line);
                            continue;
                        }
                        
                        double[] resol = new double[3]; 
                        resol[0] = Double.parseDouble(tokens[0]);
                        resol[1] = Double.parseDouble(tokens[1]);
                        resol[2] = Double.parseDouble(tokens[2]);
                        calib.setResolutions(resol);
                    }
                    else if ("unit".equalsIgnoreCase(key))
                    {
                        // read unit of spatial calibration
                        calib.setUnit(valueString);
                    }
                }
            }
            reader.close();
        };

        // assumes all necessary information have been read
        File dataFile = new File(dataFileName);
        dataFile = new File(file.getParentFile(), dataFile.getName());
        System.out.println("read data file: " + dataFile.getAbsolutePath());

        int nPixels = sizeX * sizeY * sizeZ;
        short[] data = read16bitsData(dataFile, 0, nPixels, littleEndian);

        UInt16Array3D array = new BufferedUInt16Array3D(sizeX, sizeY, sizeZ, data);
        
        // Create new image
        Image image = new Image(array);
        image.setFilePath(file.getPath());
        image.setSpatialCalibration(calib);
                
        return image; 
    }
    
    private short[] read16bitsData(File file, int offset, int size, boolean littleEndian) throws IOException 
    {
        if (!file.exists())
        {
            throw new RuntimeException("Could not find data file: " + file.getName());
        }

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file)))
        {
            short[] data = new short[size];
            int nBytes = size * 2;
            byte[] byteData = new byte[nBytes];
            
//            inputStream.seek(0);
            int nRead = inputStream.read(byteData, 0, nBytes);
            inputStream.close();
            
            if (nRead != nBytes)
            {
                throw new RuntimeException("Could read only " + nRead + " over the " + nBytes + " expected");
            }

            if (littleEndian)
            {
                for (int i = 0; i < size; i++)
                {
                    byte b1 = byteData[2*i];
                    byte b2 = byteData[2*i+1];

                    int v = ((b2 & 0xFF) << 8 | (b1 & 0x00FF));
                    data[i] = (short) v;
                }
            }
            else
            {
                for (int i = 0; i < size; i++)
                {
                    byte b1 = byteData[2*i];
                    byte b2 = byteData[2*i+1];
                    data[i] = (short) ((b1 & 0xFF) << 8 | (b2 & 0xFF));
                }
            }

            return data;
        }
    }
    
}
