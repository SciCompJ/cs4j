/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;

/**
 * Downsamples a scalar array by filtering and subsampling along each dimension.
 * 
 * @author dlegland
 *
 */
public class DownSample extends AlgoStub implements ScalarArrayOperator
{
    int factor;
    int kernelSize;
    double[] coeffs;
    
    public DownSample()
    {
        this.factor = 2;
        init();
    }
    
    public DownSample(int factor)
    {
        this.factor = factor;
        init();
    }
    
    private void init()
    {
        kernelSize = 2 * factor + 1;
        double sigma = factor * 0.5;
        
        // compute normalized kernel
        double sigma2 = 2 * sigma * sigma;
        this.coeffs = new double[kernelSize];
        double sum = 0.0;
        for (int i = 0; i < kernelSize; i++)
        {
            double x = i - factor;
            this.coeffs[i] = Math.exp(-((x * x) / sigma2));
            sum += this.coeffs[i];
        }
        
        // normalizes kernel
        for (int i = 0; i < kernelSize; i++)
        {
            this.coeffs[i] /= sum;
        }
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        if (array.dimensionality() == 2)
        {
            return processScalar2d(ScalarArray2D.wrapScalar2d(array));
        }
        else if (array.dimensionality() == 3)
        {
            return processScalar3d(ScalarArray3D.wrapScalar3d(array));
        }
        else
        {
            throw new RuntimeException("Input dimension must be 2 or 3");
        }
    }
    
    public ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array)
    {
        int sizeZ = array.size(2);
        int[] dims2 = outputArraySize(array);
        
        ScalarArray3D<?> res = ScalarArray3D.wrapScalar3d(array.newInstance(dims2));
        
        // create buffer of XY-slices
        int bufferSize = (int) Math.ceil(2 * factor) + 1;
        double[][][] smoothedSlices = new double[bufferSize][dims2[1]][dims2[0]];
        
        // initialize with filtered first row
        double[][] filteredSlice0 = smoothAndResampleSlice(array.slice(0));
        for (int f = 0; f < bufferSize; f++)
        {
            smoothedSlices[f] = filteredSlice0;
        }
        
        // iterate over rows of output array
        for (int z = 0; z < dims2[2]; z++)
        {
            this.fireProgressChanged(this, z, dims2[2]);
            for (int f = 0; f <= factor; f++)
            {
                smoothedSlices[f] = smoothedSlices[f+factor];
            }
            for (int f = factor + 1; f < bufferSize; f++)
            {
                smoothedSlices[f] = smoothAndResampleSlice(array.slice(Math.min(z * factor + f - factor, sizeZ - 1)));
            }
            
            for (int y = 0; y < dims2[1]; y++)
            {
                for (int x = 0; x < dims2[0]; x++)
                {
                    double tmp = 0.0;
                    for (int yBuf = 0; yBuf < bufferSize; yBuf++)
                    {
                        tmp += smoothedSlices[yBuf][y][x] * coeffs[yBuf];
                    }
                    res.setValue(x, y, z, tmp);
                }
            }
        }
        
        return res;
    }
    
    private double[][] smoothAndResampleSlice(ScalarArray2D<?> array)
    {
        int[] dims2 = outputArraySize(array);
        
        double[][] res = new double[dims2[1]][dims2[0]];

        int sizeY = array.size(1);
        
        // create buffer of rows
        double[][] smoothedRows = new double[kernelSize][dims2[0]];
        
        // initialize with filtered first row
        double[] filteredRow0 = smoothAndResampleRow(array, 0);
        for (int f = 0; f < kernelSize; f++)
        {
            smoothedRows[f] = filteredRow0;
        }
        
        // iterate over rows of output array
        for (int y = 0; y < dims2[1]; y++)
        {
            for (int f = 0; f <= factor; f++)
            {
                smoothedRows[f] = smoothedRows[f+factor];
            }
            for (int f = factor + 1; f < kernelSize; f++)
            {
                smoothedRows[f] = smoothAndResampleRow(array, Math.min(y * factor + f - factor, sizeY - 1));
            }
            
            for (int x = 0; x < dims2[0]; x++)
            {
                double tmp = 0.0;
                for (int yBuf = 0; yBuf < kernelSize; yBuf++)
                {
                    tmp += smoothedRows[yBuf][x] * coeffs[yBuf];
                }
                res[y][x] = tmp;
            }
        }
        
        return res;
    }

    public ScalarArray2D<?> processScalar2d(ScalarArray2D<?> array)
    {
        int sizeY = array.size(1);
        int[] dims2 = outputArraySize(array);
        
        ScalarArray2D<?> res = ScalarArray2D.wrapScalar2d(array.newInstance(dims2));
        
        // create buffer of rows
        double[][] smoothedRows = new double[kernelSize][dims2[0]];
        
        // initialize with filtered first row
        double[] filteredRow0 = smoothAndResampleRow(array, 0);
        for (int f = 0; f < kernelSize; f++)
        {
            smoothedRows[f] = filteredRow0;
        }
        
        // iterate over rows of output array
        for (int y = 0; y < dims2[1]; y++)
        {
            this.fireProgressChanged(this, y, dims2[1]);
            
            for (int f = 0; f <= factor; f++)
            {
                smoothedRows[f] = smoothedRows[f+factor];
            }
            for (int f = factor + 1; f < kernelSize; f++)
            {
                smoothedRows[f] = smoothAndResampleRow(array, Math.min(y * factor + f - factor, sizeY - 1));
            }
            
            for (int x = 0; x < dims2[0]; x++)
            {
                double tmp = 0.0;
                for (int yBuf = 0; yBuf < kernelSize; yBuf++)
                {
                    tmp += smoothedRows[yBuf][x] * coeffs[yBuf];
                }
                res.setValue(x, y, tmp);
            }
        }
        
        return res;
    }
    
    private double[] smoothAndResampleRow(ScalarArray2D<?> array, int y)
    {
        int sizeX = array.size(0);
        double[] buffer = new double[sizeX];
        copyRowValues(array, y, buffer);
        
        return smoothAndSubsample(buffer, factor);
    }

    private void copyRowValues(ScalarArray2D<?> array, int y, double[] buffer)
    {
        int sizeX = array.size(0);
        for (int x = 0; x < sizeX; x++)
        {
            buffer[x] = array.getValue(x, y);
        }
    }
    
    private double[] smoothAndSubsample(double[] row, int factor)
    {
        int sizeX2 = row.length / factor;
        double[] res = new double[sizeX2];
        
        for (int x = 0; x < sizeX2; x++)
        {
            // iterate over kernel elements
            double acc = 0.0;
            for (int i = 0; i < kernelSize; i++)
            {
                int i2 = Math.min(Math.max(x * factor - factor + i, 0), row.length-1);
                acc += row[i2] * this.coeffs[i];
            }
            
            res[x] = acc;
        }
        return res;
    }
    
    /**
     * Computes the dimensions of the result array based on the array of
     * sub-sampling factors.
     * 
     * @param array
     *            the array to sub sample
     * @return the dimensions of the sub-sampled array
     */
    public int[] outputArraySize(Array<?> array)
    {
        // input array dimensions
        int[] dims = array.size();
        int nd = dims.length;
        
        // compute output array dimensions
        int[] dims2 = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            dims2[d] = (int) Math.floor(dims[d] / factor);
        }
        
        return dims2;
    }
}
