/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sci.image.io.BinaryDataReader;

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
        
        public void init(BinaryDataReader dataReader) throws IOException
        {
            Map<String, Object> map = new TreeMap<>();
            
            // keep reader pointer
            long pos0 = dataReader.getFilePointer();

            // convert tag value to long offset for reading large buffer
            long offset = ((long) this.value) & 0xffffffffL;
            dataReader.seek(offset+8);

            map.put("dimX", dataReader.readInt());
            map.put("dimY", dataReader.readInt());
            map.put("dimZ", dataReader.readInt());
            map.put("dimC", dataReader.readInt());
            map.put("dimT", dataReader.readInt());
            dataReader.seek(dataReader.getFilePointer() + 12);
            map.put("voxelSizeX", dataReader.readDouble());
            map.put("voxelSizeY", dataReader.readDouble());
            map.put("voxelSizeZ", dataReader.readDouble());
            map.put("specScan", dataReader.readShort() & 0x00FFFF);
            
            // revert reader to initial position
            dataReader.seek(pos0);
            
            this.content = map;
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
