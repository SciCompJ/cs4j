/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.TreeMap;

/**
 * <p>
 * Computes the maximum in a local buffer around current position.
 * </p>
 * 
 * <p>
 * This implementation does not use any buffer, but requires updates to replace
 * old value by new value.
 * </p>
 * 
 * @author dlegland
 *
 */
public class LocalHistogramDouble
{    
    TreeMap<Double, Integer> valueCounts = new TreeMap<>(); 
        
    /**
     * Constructor from size and type of extremum (minimum or maximum).
     *
     * @param n
     *            the size of the local histogram
     * @param type
     *            the type of extremum (maximum or minimum)
     */
    public LocalHistogramDouble(int n, double value)
    {
        valueCounts.put(value, n);
    }
    
    public void reset(int count, double value)
    {
        valueCounts.clear();
        valueCounts.put(value, count);
    }
    
    public double getMaxValue()
    {
        return valueCounts.lastKey();
    }

    public double getMinValue()
    {
        return valueCounts.firstKey();
    }
    
    public void replace(double oldValue, double newValue)
    {
        increaseCount(newValue);
        decreaseCount(oldValue);
    }
    
    private void decreaseCount(double value)
    {
        if (valueCounts.containsKey(value))
        {
            // decrease current count
            int count = valueCounts.get(value) - 1;
            if (count == 0)
            {
                valueCounts.remove(value);
            }
        }
        else
        {
            throw new RuntimeException("Local histogram does not contain count for value " + value);
        }
    }

    private void increaseCount(double value)
    {
        if (valueCounts.containsKey(value))
        {
            // increase current count
            valueCounts.put(value, valueCounts.get(value) + 1);
        }
        else
        {
            // create new count
            valueCounts.put(value, 1);
            // TODO: test update
        }
    }
}
