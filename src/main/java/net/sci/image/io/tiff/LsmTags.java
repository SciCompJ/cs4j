/**
 * 
 */
package net.sci.image.io.tiff;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sci.image.Image;

/**
 * Management of LSM Tags.
 *
 *
 * @see <a href="https://fr.mathworks.com/matlabcentral/fileexchange/8412-lsm-file-toolbox"> LSM File Toolbox</a> by Peter Li
 * @ee <a href="https://fr.mathworks.com/matlabcentral/fileexchange/46892-zeiss-laser-scanning-confocal-microscope-lsm-file-reader"> LSM File Reader</a> by Chao-Yuan Yeh
 * 
 * @author dlegland
 *
 */
public class LsmTags implements TagSet
{
    /**
     * 34412 - LSM Info.
     */
    public static final class LSMInfo extends TiffTag
    {
        public static final int CODE = 34412;
        
        public LSMInfo()
        {
            super(CODE, "LSMInfo", "LSM Info");
        }
        
        /**
         * Override default behavior to parse data structure specific to LSM file.
         * Result is a Map, stored in content fields. 
         */
        @Override
        public void update(Image image, ImageFileDirectory ifd)
        {
//            System.out.println("updating image info from LSM file");
            Entry entry = ifd.getEntry(CODE);
            ByteBuffer buffer = ByteBuffer.wrap((byte []) entry.content);
            buffer.order(ifd.byteOrder);
            
            Map<String, Object> map = new TreeMap<>();
            
            buffer.position(8);
            map.put("dimX", buffer.getInt());
            map.put("dimY", buffer.getInt());
            map.put("dimZ", buffer.getInt());
            map.put("dimC", buffer.getInt());
            map.put("dimT", buffer.getInt());
            buffer.position(buffer.position() + 12);
            map.put("voxelSizeX", buffer.getDouble());
            map.put("voxelSizeY", buffer.getDouble());
            map.put("voxelSizeZ", buffer.getDouble());
            map.put("specScan", buffer.getShort() & 0x00FFFF);
            
            image.metadata.put("lsm", map);
        }
    }


    /* (non-Javadoc)
     * @see net.sci.image.io.tiff.TagSet#getTags()
     */
    @Override
    public Map<Integer, TiffTag> getTags()
    {
        Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(1);

        add(tags, new LSMInfo());

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
        return "LSM";
    }
}
