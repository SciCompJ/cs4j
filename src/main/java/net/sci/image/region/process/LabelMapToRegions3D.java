package net.sci.image.region.process;

import java.util.HashMap;
import java.util.Map;

import net.sci.array.Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray3D;
import net.sci.image.region.BinaryRegion3D;

/**
 * Converts a 3D label map (an array of integers) into a map of region labels to
 * instances of {@code Region3D}.
 */
public class LabelMapToRegions3D
{
    /**
     * Default empty constructor.
     */
    public LabelMapToRegions3D()
    {
    }
    
    public Map<Integer, BinaryRegion3D> process(Array<?> array)
    {
        if (!array.elementInstanceOf(Int.class))
        {
            throw new IllegalArgumentException("Requires an array containing element of class <Int>");
        }
        if (array.dimensionality() != 3)
        {
            throw new IllegalArgumentException("Requires an array with three dimensions, not " + array.dimensionality());
        }
        
        // cast to IntArray3D
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray3D<?> labelMap = IntArray3D.wrap(IntArray.wrap((Array<? extends Int>) array));
        
        // retrieve image size
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);
        int sizeZ = labelMap.size(2);
        
        // create result data structure
        Map<Integer, BinaryRegion3D> map = new HashMap<Integer, BinaryRegion3D>(); 
        
        // iterate over image voxels
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = labelMap.getInt(x, y, z);
                    if (label == 0) continue;
                    
                    BinaryRegion3D region = map.get(label);
                    if (region == null)
                    {
                        region = new BinaryRegion3D();
                        map.put(label, region);
                    }
                    
                    region.set(x, y, z, true);
                }
            }
        }
        
        return map;
    }
}
