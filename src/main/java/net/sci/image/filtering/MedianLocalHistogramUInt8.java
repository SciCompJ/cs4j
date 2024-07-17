/**
 * 
 */
package net.sci.image.filtering;

/**
 * <p>
 * Keeps an histogram of values within the neighborhood of a position by storing
 * the counts of values within an array of integers, with a focus on median
 * value computation. Local Histogram is used by sliding structuring element
 * implementations.
 * </p>
 * s
 * <p>
 * This implementation does not use any buffer, but requires updates to replace
 * old value by new value. Local histogram is stored in a 256 array, resulting
 * in constant access time for updating value counts.
 * </p>
 * 
 * @see net.sci.image.morphology.strel.LocalHistogramDoubleTreeMap
 * @see net.sci.image.morphology.strel.SlidingDiskStrel
 * @see net.sci.image.morphology.strel.SlidingBallStrel3D
 * 
 * @author dlegland
 *
 */
public class MedianLocalHistogramUInt8
{
    // ==================================================
    // Class variables
    
    final int totalCount;
    final int halfCount;

    /**
     * An array to store the count of each value between 0 and 255.
     */
    int[] valueCounts;    
    
    /**
     * The current median value, updated only when required.
     */
    int medianValue = 0;

//    /**
//     * The flag indicating that the maximum value needs to be recomputed.
//     */
//    boolean needUpdateMax = false;
    
    int lowerValueCount = 0;
    int upperValueCount = 0;
    
    
    // ==================================================
    // Constructors

    /**
     * Constructor from histogram size and filling value.
     * 
     * @param count
     *            the number of values within the histogram
     * @param value
     *            the value that fills the histogram.
     */
    public MedianLocalHistogramUInt8(int count, int value)
    {
        this.totalCount = count;
        this.halfCount = (count - 1) / 2;
        
        this.valueCounts = new int[256];
        this.valueCounts[value] = count;

        this.medianValue = value;
        this.lowerValueCount = 0;
        this.upperValueCount = 0;
    }
    
    
    // ==================================================
    // Class methods

    /**
     * Resets this local histogram by filling with the specified value, avoiding
     * to create a new instance.
     * 
     * @param count
     *            the number of values within the histogram
     * @param value
     *            the value that fills the histogram.
     */
    public void reset(int value)
    {
        for (int i = 0; i < 256; i++)
        {
            this.valueCounts[i] = 0;
        }
        this.valueCounts[value] = totalCount;
        
        this.medianValue = value;
        this.lowerValueCount = 0;
        this.upperValueCount = 0;
    }
    
    public int getMedianInt()
    {
        if (lowerValueCount > halfCount || upperValueCount > halfCount)
        {
            recomputeMedianValue();
        }
        return medianValue;
    }
    
    private void recomputeMedianValue()
    {
        int count = 0;
        for (medianValue = 0; medianValue < 256; medianValue++)
        {
            count += valueCounts[medianValue];
            if (count >= halfCount)
            {
                break;
            }
        }
        
        // update count of lower and upper values
        lowerValueCount = 0;
        for (int value = 0; value < medianValue; value++)
        {
            lowerValueCount += valueCounts[value];
        }
        upperValueCount = 0;
        for (int value = medianValue + 1; value <= 255; value++)
        {
            upperValueCount += valueCounts[value];
        }
    }

    public void replace(int oldValue, int newValue)
    {
        increaseCount(newValue);
        decreaseCount(oldValue);
    }
    
    private void decreaseCount(int value)
    {
        if (valueCounts[value] > 0)
        {
            // decrease current count
            int count = valueCounts[value] - 1;
            valueCounts[value] = count;
            if (value < medianValue)
            {
                lowerValueCount--;
            }
            if (value > medianValue)
            {
                upperValueCount--;
            }
        }
        else
        {
            throw new RuntimeException("Local histogram does not contain count for value " + value);
        }
    }

    private void increaseCount(int value)
    {
        valueCounts[value] = valueCounts[value] + 1;
        
        if (value < medianValue)
        {
            lowerValueCount++;
        }
        if (value > medianValue)
        {
            upperValueCount++;
        }
    }
}
