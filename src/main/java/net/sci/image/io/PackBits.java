/**
 * 
 */
package net.sci.image.io;

/**
 * Utility for PackBits decompression. Used in TiffImageReader, for instance.
 * 
 * @author dlegland
 *
 */
public class PackBits
{
    /**
     * Private constructor to prevent instantiation.
     */
    private PackBits()
    {
    }
    
    /**
     * Uncompress byte array into a pre-allocated result byte array, using
     * Packbits compression.
     * 
     * Based on the ImageJ code, which is based on Bio-Formats PackbitsCodec
     * written by Melissa Linkert.
     * 
     * @param input
     *            the input array
     * @param output
     *            the pre-allocated output array
     * @returns the length of the buffer after decompression
     */
    public static int uncompressPackBits(byte[] input, byte[] output)
    {
        return uncompressPackBits(input, output, 0);
    }

    /**
     * Uncompress byte array into a pre-allocated result byte array, using
     * PackBits compression.
     * 
     * Based on the ImageJ code, which is based on Bio-Formats PackbitsCodec
     * written by Melissa Linkert.
     * 
     * @param input
     *            the input array
     * @param output
     *            the pre-allocated output array
     * @param offset
     *            the position of the first byte to write in the output array
     * 
     * @returns the length of the buffer after decompression
     */
    public static int uncompressPackBits(byte[] input, byte[] output, int offset)
    {
        int index = 0;
        int index2 = offset;
        while (index < input.length && index2 < output.length)
        {
            // read the compression code
            byte n = input[index++];
            if (n >= 0)
            {
                // copy the next n+1 bytes literally
                for (int i = 0; i < n + 1; i++)
                {
                    output[index2++] = input[index++];
                }
            }
            else if (n != -128)
            {
                // copy the next byte state -n+1 times
                int count = -n + 1;
                byte value = input[index++];
                for (int i = 0; i < count; i++)
                {
                    output[index2++] = value;
                }
            }
        }

        return index2 - offset;
    }
}
