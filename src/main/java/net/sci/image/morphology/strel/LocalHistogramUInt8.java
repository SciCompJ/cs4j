/**
 * 
 */
package net.sci.image.morphology.strel;

/**
 * <p>
 * Keeps an histogram of values within the neighborhood of a position by storing
 * the counts of values within an array of integers. Local Histogram is used by 
 * sliding structuring element implementations.
 * </p>
 * s
 * <p>
 * This implementation does not use any buffer, but requires updates to replace
 * old value by new value. Local histogram is stored in a 256 array, resulting in
 * constant access time for updating value counts.
 * </p>
 * 
 * @see LocalHistogramDoubleTreeMap
 * @see SlidingDiskStrel
 * @see SlidingBallStrel3D
 * 
 * @author dlegland
 *
 */
public class LocalHistogramUInt8
{
    // ==================================================
    // Class variables

    /**
     * An array to store the count of each value between 0 and 255.
     */
    int[] valueCounts;    
    
    /**
     * The current maximum value, updated only when required.
     */
    int maxValue = 0;

    /**
     * The flag indicating that the maximum value needs to be recomputed.
     */
    boolean needUpdateMax = false;
    
    /**
     * The current minimum value, updated only when required.
     */
    int minValue = 255;
    
    /**
     * The flag indicating that the minimum value needs to be recomputed.
     */
    boolean needUpdateMin = false;
    
    
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
    public LocalHistogramUInt8(int count, int value)
    {
        this.valueCounts = new int[256];
        this.valueCounts[value] = count;

        this.maxValue = value;
        this.minValue = value;
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
    public void reset(int count, int value)
    {
        for (int i = 0; i < 256; i++)
        {
            this.valueCounts[i] = 0;
        }
        this.valueCounts[value] = count;
        
        this.maxValue = value;
        this.minValue = value;
        this.needUpdateMax = false;
        this.needUpdateMin = false;
    }
    
    public int getMaxInt()
    {
        if (needUpdateMax)
        {
            recomputeMaxValue();
        }
        return maxValue;
    }
    
    private void recomputeMaxValue()
    {
        for (maxValue = 255; maxValue > 0; maxValue--)
        {
            if (valueCounts[maxValue] > 0)
            {
                break;
            }
        }
        needUpdateMax = false;
    }

    public int getMinInt()
    {
        if (needUpdateMin)
        {
            recomputeMinValue();
        }
        return minValue;
    }
    
    private void recomputeMinValue()
    {
        for (minValue = 0; minValue < 256; minValue++)
        {
            if (valueCounts[minValue] > 0)
            {
                break;
            }
        }
        needUpdateMin = false;
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
            
            // when extreme bin count reaches zero, min or max value need to
            // be recomputed
            if (count == 0)
            {
                if (value == maxValue)
                {
                    needUpdateMax = true;
                }
                if (value == minValue)
                {
                    needUpdateMin = true;
                }
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
        
        // when new value is outside current bounds, it can be automatically
        // updated
        if (value > maxValue)
        {
            maxValue = value;
            needUpdateMax = false;
        }
        if (value < minValue)
        {
            minValue = value;
            needUpdateMin = false;
        }
    }
}
