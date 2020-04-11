/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that manages the different sources of Tag Sets, and can
 * return the set of all Tiff Tags that can be interpreted.
 * 
 * @author dlegland
 *
 */
public class TagSetManager
{
    /**
     * The inner collection of tag sets.
     */
    ArrayList<TagSet> tagSets = new ArrayList<TagSet>();
    
    /**
     * Adds a new tag set to the list of tag sets.
     * 
     * @param tagSet
     *            the tag set to add.
     * @return true if the add operation was successful
     */
    public boolean addTagSet(TagSet tagSet)
    {
        return tagSets.add(tagSet);
    }


    /**
     * Returns a map of all known tags, indexed by their code.
     * 
     * @return a map of known tags, using tag code as key and tag instance as values.
     */
    public Map<Integer, TiffTag> getAllTags()
    {
        HashMap<Integer, TiffTag> map = new HashMap<>(50);
        
        for (TagSet tagSet : tagSets)
        {
            for(TiffTag tag : tagSet.getTags().values())
            {
                if (!map.containsKey(tag.code))
                {
                    map.put(tag.code, tag);
                }
                else
                {
                    System.err.println("TagSetManager already contains a tag entry with code: "
                            + tag.code + "(" + tag.name + ")");
                }
            }
        }
        
        return map;
    }
    
    
    /**
     * Private constructor to prevent instantiation.
     */
    private TagSetManager()
    {
        // Baseline tags (image size and format)
        addTagSet(new BaselineTags());
        
        // Extension tags: less common formats
        addTagSet(new ExtensionTags());
        
        // TIFF/IT specification 
        addTagSet(new TiffITTags());
        
        // ImageJ Tags 
        addTagSet(new ImageJTags());
        
        // LSM tags
        addTagSet(new LsmTags());

        // Some other tag collections may be added in the future.
    }
    
    /** Singleton Holder, to be thread-safe. */
    private static class SingletonHolder
    {
        /** Unique instance. */
        private final static TagSetManager instance = new TagSetManager();
    }
    
    /** Returns the unique instance of TagSetManager */
    public static TagSetManager getInstance()
    {
        return SingletonHolder.instance;
    }
}
