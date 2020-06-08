/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.ByteOrder;

import net.sci.array.Array;
import net.sci.image.Calibration;
import net.sci.image.Image;

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
        
        double[] resol = new double[3];
        String unitName = "";
        
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
                        
                        resol[0] = Double.parseDouble(tokens[0]);
                        resol[1] = Double.parseDouble(tokens[1]);
                        resol[2] = Double.parseDouble(tokens[2]);
                    }
                    else if ("unit".equalsIgnoreCase(key))
                    {
                        // read unit of spatial calibration
                        unitName = valueString;
                    }
                }
            }
            reader.close();
        };

        // assumes all necessary information have been read
        File dataFile = new File(dataFileName);
        dataFile = new File(file.getParentFile(), dataFile.getName());
        System.out.println("read data file: " + dataFile.getAbsolutePath());

        ByteOrder order = littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        ImageBinaryDataReader reader = new ImageBinaryDataReader(dataFile, order);
        Array<?> array = reader.readUInt16Array(new int[]{sizeX, sizeY, sizeZ});

        reader.close();
        
        // Create new image
        Image image = new Image(array);
		image.setNameFromFileName(file.getName());
		image.setFilePath(file.getPath());
        
        // setup spatial resolution
        image.setCalibration(new Calibration(resol, unitName));
                
        return image; 
    }

}
