/**
 * 
 */
package net.sci.image.io.tiff;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import net.sci.image.Image;

/**
 * The set of tags used by ImageJ to save overlays.
 * 
 * @author dlegland
 *
 */
public class ImageJTags implements TagSet
{
    /**
     * 50838 - ImageJ Metadata counts.
     */
    public static final class ImageJMetaDataCounts extends TiffTag
    {
        public static final int CODE = 50838;
        public ImageJMetaDataCounts()
        {
            super(CODE, Type.LONG, "ImageJMetaDataCounts", "ImageJ Metadata counts");
        }
    }

    /**
     * 50839 - ImageJ Metadata, as a pointer to the beginning of the meta data region within the file.
     */
    public static final class ImageJMetaData extends TiffTag
    {
        public static final int CODE = 50839;
        
        /**
         * Identifier for the beginning of ImageJ metadata. Corresponds to the
         * String "IJIJ".
         */
        static final int MAGIC_NUMBER = 0x494a494a;
        
        // The different types of meta data, as four characters strings.
        static final int INFO = 0x696e666f;  // "info" (Info image property)
        static final int LABELS = 0x6c61626c;  // "labl" (slice labels)
        static final int RANGES = 0x72616e67;  // "rang" (display ranges)
        static final int LUTS = 0x6c757473;    // "luts" (channel LUTs)
        static final int PLOT = 0x706c6f74;    // "plot" (serialized plot)
        static final int ROI = 0x726f6920;     // "roi " (ROI)
        static final int OVERLAY = 0x6f766572; // "over" (overlay)
        static final int PROPERTIES = 0x70726f70; // "prop" (properties)
        
        
        public ImageJMetaData()
        {
            super(CODE, Type.BYTE, "ImageJMetaData", "ImageJ Metadata");
        }
        
        @Override
        public void update(Image image, ImageFileDirectory ifd)
        {
            int[] metaDataCounts = ifd.getIntArrayValue(ImageJMetaDataCounts.CODE, null);
            if (metaDataCounts == null)
            {
                System.err.println("Could not read ImageJ meta-data offsets");
                return;
            }
            
//            int n = metaDataCounts.length;
//            System.out.println("number of meta data: " + (n-1));
//            System.out.println("tag count: " + this.count);
//            System.out.println("tag type: " + this.type);
//            System.out.print("metadata counts: [");
//            for (int c : metaDataCounts) System.out.print(" " + c);
//            System.out.println("]");
            
            // wrap content into a byte buffer with same byte order as IFD
            ByteBuffer buffer = ByteBuffer.wrap((byte[]) this.content).order(ifd.getByteOrder());
            
            // check validity of the beginning of the buffer
            int hdrSize = metaDataCounts[0];
            if (hdrSize < 12 || hdrSize > 804)
            {
                System.err.println("Wrong header size (must be comprised between 12 and 804)");
                return;
            }
            
            // check the next four bytes correspond to the constant "IJIJ"
            if (buffer.getInt() != MAGIC_NUMBER)  // "IJIJ"  
            {
                return;
            }

            // retrieve number of entries
            int nTypes = (hdrSize - 4) / 8;
            int[] types = new int[nTypes];
            int[] counts = new int[nTypes];
            int extraMetaDataEntries = 0;
            
            // determine type and count of each meta data
            for (int i = 0; i < nTypes; i++)
            {
                types[i] = buffer.getInt();
                counts[i] = buffer.getInt(); // number of meta data with the current type
                
                if (types[i] < 0xffffff) extraMetaDataEntries += counts[i];
            }
            
            // create ImageJ metadata structure
            ImagejMetadata metadata = new ImagejMetadata();
            
            // allocate memory for extra meta-data (with non classical types)
            metadata.extraMetadataTypes = new int[extraMetaDataEntries];
            metadata.extraMetadata = new byte[extraMetaDataEntries][];
            int eMDindex = 0;
            
            // iterate over the types
            // (each type may have several meta data)
            int index = 1;
            for (int i = 0; i < nTypes; i++)
            {
                if (types[i] == INFO)
                {
                    metadata.info = readString(buffer, metaDataCounts[index]);
                }
                else if (types[i] == LABELS)
                {
                    int[] counts2 = subArray(metaDataCounts, index, counts[i]);
                    metadata.sliceLabels = readStringArray(buffer, counts2);
                }
                else if (types[i] == RANGES)
                {
                    metadata.displayRanges = readDisplayRanges(buffer, metaDataCounts, index);
                }
                else if (types[i] == LUTS)
                {
                    int[] counts2 = subArray(metaDataCounts, index, counts[i]);
                    metadata.channelLuts = readByteArrayArray(buffer, counts2);
                }
                else if (types[i] == PLOT)
                {
                    metadata.plotData = readByteArray(buffer, metaDataCounts[index]);
                }
                else if (types[i] == ROI)
                {
                    metadata.roiData = readByteArray(buffer, metaDataCounts[index]);
                }
                else if (types[i] == OVERLAY)
                {
                    int[] counts2 = subArray(metaDataCounts, index, counts[i]);
                    metadata.overlayData = readByteArrayArray(buffer, counts2);
                }
                else if (types[i] == PROPERTIES)
                {
                    int[] counts2 = subArray(metaDataCounts, index, counts[i]);
                    metadata.properties = readStringArray(buffer, counts2);
                }
                else if (types[i] < 0xffffff)
                {
                    for (int j = index; j < index + counts[i]; j++)
                    {
                        metadata.extraMetadata[eMDindex] = readByteArray(buffer, metaDataCounts[j]);
                        metadata.extraMetadataTypes[eMDindex] = types[i];
                        eMDindex++;
                    }
                }
                else
                {
                    int[] counts2 = subArray(metaDataCounts, index, counts[i]);
                    readByteArrayArray(buffer, counts2);
                }
                index += counts[i];
            }
            
            image.metadata.put("imagej", metadata);
        }
        
        private static double[] readDisplayRanges(ByteBuffer buffer, int[] metaDataCounts, int first)
        {
            int n = metaDataCounts[first] / 8;
            double[] displayRanges = new double[n];
            for (int i = 0; i < n; i++)
                displayRanges[i] = buffer.getDouble();
            return displayRanges;
        }
        
        private static final byte[] readByteArray(ByteBuffer buffer, int length)
        {
            byte[] array = new byte[length];
            buffer.get(array, 0, length);
            return array;
        }
        
        private static final byte[][] readByteArrayArray(ByteBuffer buffer, int[] lengths)
        {
            byte[][] res = new byte[lengths.length][];
            for (int i = 0; i < lengths.length; i++)
            {
                res[i] = readByteArray(buffer, lengths[i]);
            }
            return res;
        }
        
        private static final String readString(ByteBuffer buffer, int nBytes)
        {
            ByteBuffer buffer2 = ByteBuffer.allocate(nBytes);
            buffer.get(buffer2.array(), 0, nBytes);
            return buffer2.order(buffer.order()).asCharBuffer().toString();
        }
        
        private static final String[] readStringArray(ByteBuffer buffer, int[] lengths)
        {
            String[] res = new String[lengths.length];
            for (int i = 0; i< lengths.length; i++)
            {
                res[i] = readString(buffer, lengths[i]);
            }
            return res;
        }
        
        /**
         * Returns a new array containing elements of input array comprised
         * between {@code first} (included) and {@code last} (excluded).
         * 
         * @param array
         *            the array to extract data from
         * @param start
         *            the index of the first element to keep in source array
         * @param count
         *            the number of elements to keep
         * @return a new array with {@code count} elements
         */
        private static final int[] subArray(int[] array, int start, int count)
        {
            int[] res = new int[count];
            System.arraycopy(array, start, res, 0, count);
            return res;
        }
    }

    /* (non-Javadoc)
	 * @see net.sci.image.io.TiffTagSet#getTags()
	 */
	@Override
	public Map<Integer, TiffTag> getTags()
	{
	    Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(2);
	    add(tags, new ImageJMetaDataCounts());
	    add(tags, new ImageJMetaData());
		return tags;
	}

    /**
     * Adds a tag into a map by indexing it with its key.
     * 
     * @param map
     *            the map to populate.
     * @param tag
     *            the tag to add.
     */
    private void add(Map<Integer, TiffTag> map, TiffTag tag)
    {
        tag.tagSet = this;
        map.put(tag.code, tag);
    }

    @Override
    public String getName()
    {
        return "ImageJ";
    }

}
