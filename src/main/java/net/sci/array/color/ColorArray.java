/**
 * 
 */
package net.sci.array.color;

import net.sci.array.Array;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.ScalarArray;

/**
 * An array that contains elements that implements the Color interface.
 *
 * Provides convenience methods for accessing channels as arrays.
 * 
 * @param <T> the type of color of array elements
 * 
 * @author dlegland
 *
 */
public interface ColorArray<T extends Color>  extends Array<T>
{
    // =============================================================
    // New methods specific to ColorArray

    /**
     * Returns the red channel of this array, with elements between 0 and 1.
     * 
     * @return the red channel of this array, with elements between 0 and 1.
     */
    public default ScalarArray<?> redChannel()
    {
        // allocate array for storing result
        Float32Array channel = Float32Array.create(this.size());
        
        // create iterators
        Iterator<T> colorIter = iterator();
        Float32Array.Iterator channelIter = channel.iterator();
        
        // iterate over both arrays
        while (colorIter.hasNext())
        {
            channelIter.setNextValue(colorIter.next().red());
        }
        
        // return the channel
        return channel;
    }
    
    /**
     * Returns the green channel of this array, with elements between 0 and 1.
     * 
     * @return the green channel of this array, with elements between 0 and 1.
     */
    public default ScalarArray<?> greenChannel()
    {
        // allocate array for storing result
        Float32Array channel = Float32Array.create(this.size());
        
        // create iterators
        Iterator<T> colorIter = iterator();
        Float32Array.Iterator channelIter = channel.iterator();
        
        // iterate over both arrays
        while (colorIter.hasNext())
        {
            channelIter.setNextValue(colorIter.next().green());
        }
        
        // return the channel
        return channel;
    }
    
    /**
     * Returns the blue channel of this array, with elements between 0 and 1.
     * 
     * @return the blue channel of this array, with elements between 0 and 1.
     */
    public default ScalarArray<?> blueChannel()
    {
        // allocate array for storing result
        Float32Array channel = Float32Array.create(this.size());
        
        // create iterators
        Iterator<T> colorIter = iterator();
        Float32Array.Iterator channelIter = channel.iterator();
        
        // iterate over both arrays
        while (colorIter.hasNext())
        {
            channelIter.setNextValue(colorIter.next().blue());
        }
        
        // return the channel
        return channel;
    }
    
    /**
     * Returns the hue channel of this array, with elements between 0 and 1.
     * 
     * @return the hue channel of this array, with elements between 0 and 1.
     */
    public default ScalarArray<?> hueChannel()
    {
        // allocate array for storing result
        Float32Array channel = Float32Array.create(this.size());
        
        // create iterators
        Iterator<T> colorIter = iterator();
        Float32Array.Iterator channelIter = channel.iterator();
        
        // iterate over both arrays
        while (colorIter.hasNext())
        {
            channelIter.setNextValue(colorIter.next().hue());
        }
        
        // return the channel
        return channel;
    }
    
    /**
     * Returns the saturation channel of this array, with elements between 0 and 1.
     * 
     * @return the saturation channel of this array, with elements between 0 and 1.
     */
    public default ScalarArray<?> saturationChannel()
    {
        // allocate array for storing result
        Float32Array channel = Float32Array.create(this.size());
        
        // create iterators
        Iterator<T> colorIter = iterator();
        Float32Array.Iterator channelIter = channel.iterator();
        
        // iterate over both arrays
        while (colorIter.hasNext())
        {
            channelIter.setNextValue(colorIter.next().saturation());
        }
        
        // return the channel
        return channel;
    }
    
    /**
     * Returns the luminance channel of this array, with elements between 0 and
     * 1.
     * 
     * @return the luminance channel of this array, with elements between 0 and
     *         1.
     */
    public default ScalarArray<?> luminanceChannel()
    {
        // allocate array for storing result
        Float32Array channel = Float32Array.create(this.size());
        
        // create iterators
        Iterator<T> colorIter = iterator();
        Float32Array.Iterator channelIter = channel.iterator();
        
        // iterate over both arrays
        while (colorIter.hasNext())
        {
            channelIter.setNextValue(colorIter.next().luminance());
        }
        
        // return the channel
        return channel;
    }
    
    public Array.Iterator<T> iterator();
}
