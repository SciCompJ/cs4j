/**
 * 
 */
package net.sci.image.process.segment;

import java.util.Arrays;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.process.Histograms;
import net.sci.array.scalar.Int32Array1D;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray1D;
import net.sci.array.scalar.UInt8Array;
import net.sci.image.ImageArrayOperator;
import net.sci.image.morphology.watershed.Watershed1D;

/**
 * @author dlegland
 *
 */
public class WatershedClustering extends AlgoStub implements ImageArrayOperator
{

    int medianFilterSize = 5;
    
    public void setMedianFilterSize(int newSize)
    {
        this.medianFilterSize = newSize;
    }
    
    @Override
    public <T> IntArray<?> process(Array<T> array)
    {
        if (array instanceof UInt8Array)
        {
            // type cast
            UInt8Array array2 = (UInt8Array) array;
            
            int[] histo = Histograms.histogramUInt8(array2);
            histo = medianFilter(histo, 5);
            
            Int32Array1D histArray = Int32Array1D.fromIntArray(histo);
//            System.out.println("histo:");
//            printArray(histArray);
            
            histArray = complement(histArray);
//            System.out.println("histo comp:");
//            printArray(histArray);
            
            Watershed1D watershed = new Watershed1D();
            IntArray1D<?> histClasses = watershed.process(histArray);
//            System.out.println("histo classes:");
//            printArray(histClasses);
            histClasses = fillBackgroundValues(histClasses);
//            System.out.println("histo classes 2:");
//            printArray(histClasses);
            
            IntArray<?> labelMap = UInt8Array.create(array.size());
            for (int[] pos : labelMap.positions())
            {
                labelMap.setInt(pos, histClasses.getInt(array2.getInt(pos)));
            }
            return labelMap;
        }
        else
        {
            throw new RuntimeException("Can not process non scalar arrays");
        }
    }
    
    private final static Int32Array1D complement(Int32Array1D array)
    {
        int n = array.size(0);
        
        int maxi = 0;
        for (int i = 0; i < n; i++)
        {
            maxi = Math.max(maxi, array.getInt(i));
        }
        
        Int32Array1D res = Int32Array1D.create(n);
        for (int i = 0; i < n; i++)
        {
            res.setInt(i, maxi - array.getInt(i));
        }
        return res;
    }
    
    private int[] medianFilter(int[] array, int filterSize)
    {
        int n = array.length;
        int[] res = new int[array.length];

        int s0 = (int) Math.floor(filterSize * 0.5);
        int s1 = (int) Math.ceil(filterSize * 0.5);
        int[] buffer = new int[filterSize];
        
        for (int i = 0; i < s0; i++)
        {
            res[i] = array[i];
        }
        for (int i = s0; i < n-s1; i++)
        {
            for (int k = 0; k < filterSize; k++)
            {
                buffer[k] = array[i - s0 + k];
            }
            Arrays.sort(buffer);
            res[i] = buffer[filterSize/2];
        }
        for (int i = n-s1; i < n; i++)
        {
            res[i] = array[i];
        }
        return res;
    }
    
    private IntArray1D<?> fillBackgroundValues(IntArray1D<?> values)
    {
        int n = values.size(0);
        IntArray1D<?> res = Int32Array1D.create(n);
        
        int lastValue = 0;
        for (int i = 0; i < n; i++)
        {
            int v = values.getInt(i); 
            if (v != 0)
            {
                lastValue = v;
                break;
            }
        }
        
        for (int i = 0; i < n; i++)
        {
            int v = values.getInt(i); 
            if (v != 0)
            {
                lastValue = v;
            }
            else
            {
                v = lastValue;
            }
            res.setInt(i, v);
        }
        
        return res;
    }
    
//    private void printArray(IntArray1D<?> array)
//    {
//        int n = array.size(0);
//        for (int i = 0; i < n; i++)
//        {
//            System.out.print(String.format("%2d ", array.getInt(i)));
//        }
//        System.out.println();
//    }
}

