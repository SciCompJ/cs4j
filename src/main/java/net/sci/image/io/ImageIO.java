/**
 * 
 */
package net.sci.image.io;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;

import net.sci.array.Array;
import net.sci.array.numeric.impl.FileMappedFloat32Array3D;
import net.sci.array.numeric.impl.FileMappedUInt16Array3D;
import net.sci.array.numeric.impl.FileMappedUInt8Array3D;

/**
 * A collection of utility methods for Image I/O.
 */
public class ImageIO
{
    public static final Array<?> createFileMappedArray(Path path, long offset, int[] dims, PixelType pixelType, ByteOrder byteOrder) throws IOException
    {
        String filePath = path.toString();
        if (pixelType == PixelType.UINT8)
        {
            return new FileMappedUInt8Array3D(filePath, offset, dims[0], dims[1], dims[2]);
        }
        else if (pixelType == PixelType.UINT12 || pixelType == PixelType.UINT16)
        {
            return new FileMappedUInt16Array3D(filePath, offset, dims[0], dims[1], dims[2], byteOrder);
        }
        else if (pixelType == PixelType.FLOAT32)
        {
            return new FileMappedFloat32Array3D(filePath, offset, dims[0], dims[1], dims[2], byteOrder);
        }
        else
        {
            throw new RuntimeException("Can not read stack with " + pixelType + " pixel type");
        }
    }
    

    /**
     * Private constructor to prevent instantiation. 
     */
    private ImageIO()
    {
    }
}
