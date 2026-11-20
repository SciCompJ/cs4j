/**
 * 
 */
package net.sci.image.filtering;

import java.util.Arrays;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.process.ScalarArrayOperator;
import net.sci.image.ImageArrayOperator;

/**
 * Gaussian filter that can be applied to images of any dimension.
 * 
 * Uses separability property of the Gaussian kernel to performs @{code n}
 * one-dimensional filters on the array.
 * 
 * @see <a href=
 *      "https://docs.scipy.org/doc/scipy/reference/generated/scipy.ndimage.gaussian_filter.html">
 *      https://docs.scipy.org/doc/scipy/reference/generated/scipy.ndimage.gaussian_filter.html</a>
 */
public class GaussianFilter extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator
{
    // =============================================================
    // Class variables

    /**
     * The factory used to create output array. If set to null (the default), use the factory
     * of the input array.
     */
    ScalarArray.Factory<?> factory = null;
    
    /**
     * The size (diameter) of the kernel (in pixels) along each dimension. May
     * also be a one-element array, in that case the same value is used for each
     * dimension. Each value must be an odd integer, in the form:
     * {@code diam = 2 * radius + 1}.
     */
    int[] diameters = null;
    
    /**
     * The width of the kernel along each dimension. May also be a one-element
     * array, in that case the same value is used for each dimension.
     */
    double[] sigmas;


    // =============================================================
    // Constructors

    /**
     * Empty constructor initialized with default options.
     */
    public GaussianFilter()
    {
        this(5, 2);
    }
    
    /**
     * Creates a new Gaussian filter initialized with the width of the Gaussian
     * curve. The same values will be used to any dimension of the arrays to
     * process. The size of the kernel is computed from the width of the curve.
     * 
     * @param sigma
     *            the width of the Gaussian curve in all dimensions
     */
    public GaussianFilter(double sigma)
    {
        this(new double[] { sigma });
    }
    
    /**
     * Creates a new Gaussian filter initialized with the width of the Gaussian
     * curve along each dimension. The size of the filter is computed from the
     * width of the curve. The {@code GaussianFilter} operator must be applied
     * on array with same number of dimensions.
     * 
     * @param sigmaList
     *            the width of the Gaussian curve along each dimension
     */
    public GaussianFilter(double[] sigmaList)
    {
        this.sigmas = sigmaList;
    }
    
    /**
     * Creates a new Gaussian filter initialized with the size of the kernel and
     * the width of the Gaussian curve. The same values will be used to any
     * dimension of the arrays to process.
     * 
     * @param diameters
     *            the size of the kernel in all dimensions
     * @param sigmaList
     *            the width of the Gaussian curve in all dimensions
     */
    public GaussianFilter(int kernelSize, double sigma)
    {
        this.diameters = new int[] { kernelSize };
        this.sigmas = new double[] { sigma };
    }
    
    /**
     * Creates a new Gaussian filter initialized with the size of the kernel
     * along each dimension and the width of the Gaussian curve along each
     * dimension. Both arrays must have same length. The {@code GaussianFilter}
     * operator must be applied on array with same number of dimensions.
     * 
     * @param kernelSizes
     *            the size of the kernel along each dimension
     * @param sigmaList
     *            the width of the Gaussian curve along each dimension
     */
    public GaussianFilter(int[] kernelSizes, double[] sigmaList)
    {
        if (kernelSizes.length != sigmaList.length)
        {
            throw new IllegalArgumentException("kernel sizes and sigma values must have same length");
        }
        this.diameters = kernelSizes;
        this.sigmas = sigmaList;
    }
    
    
    // =============================================================
    // Implementation of ArrayOperator methods

    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        return switch (array.dimensionality())
        {
            case 2 -> processScalar2d(ScalarArray2D.wrapScalar2d(array));
            case 3 -> processScalar3d(ScalarArray3D.wrapScalar3d(array));
            default -> throw new IllegalArgumentException(
                    "Can not process dimensionality: " + array.dimensionality());
        };
    }
    
    private ScalarArray2D<?> processScalar2d(ScalarArray2D<?> array)
    {
        ScalarArray2D<?> res = ScalarArray2D.wrap(createResultArray(array));
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // adapt size of kernels
        double[] sigmas = adaptDim(this.sigmas, 2);
        int[] radiusList = computeKernelRadiusList(sigmas);
        
        // performs filtering along first dimension
        this.fireStatusChanged(this, String.format("Process axis %d over %d", 1, 2));
        double[] kernelX = createKernel(radiusList[0], sigmas[0]);
        double[] resRowX = new double[sizeX];
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            double[] row = getPaddedRow(array, new int[] { 0, y }, 0, radiusList[0]);
            filterRow(row, kernelX, resRowX);
            setRow(res, new int[] { 0, y }, 0, resRowX);
        }

        // performs filtering along second dimension
        this.fireStatusChanged(this, String.format("Process axis %d over %d", 2, 2));
        double[] kernelY = createKernel(radiusList[1], sigmas[1]);
        double[] resRowY = new double[sizeY];
        for (int x = 0; x < sizeX; x++)
        {
            this.fireProgressChanged(this, x, sizeX);
            double[] row = getPaddedRow(res, new int[] { x, 0 }, 1, radiusList[1]);
            filterRow(row, kernelY, resRowY);
            setRow(res, new int[] {x,0}, 1, resRowY);
        }
        
        return res;
    }
    
    private ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array)
    {
        ScalarArray3D<?> res = ScalarArray3D.wrap(createResultArray(array));
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // adapt size of kernels
        double[] sigmas = adaptDim(this.sigmas, 3);
        int[] radiusList = computeKernelRadiusList(sigmas);
        
        // performs filtering along first dimension
        this.fireStatusChanged(this, String.format("Process axis %d over %d", 1, 3));
        double[] kernelX = createKernel(radiusList[0], sigmas[0]);
        double[] resRowX = new double[sizeX];
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                double[] row = getPaddedRow(array, new int[] { 0, y, z }, 0, radiusList[0]);
                filterRow(row, kernelX, resRowX);
                setRow(res, new int[] { 0, y, z }, 0, resRowX);
            }
        }
        
        // performs filtering along second dimension
        this.fireStatusChanged(this, String.format("Process axis %d over %d", 2, 3));
        double[] kernelY = createKernel(radiusList[1], sigmas[1]);
        double[] resRowY = new double[sizeY];
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int x = 0; x < sizeX; x++)
            {
                double[] row = getPaddedRow(res, new int[] { x, 0, z }, 1, radiusList[1]);
                filterRow(row, kernelY, resRowY);
                setRow(res, new int[] { x, 0, z }, 1, resRowY);
            }
        }
        
        // performs filtering along third dimension
        this.fireStatusChanged(this, String.format("Process axis %d over %d", 3, 3));
        double[] kernelZ = createKernel(radiusList[2], sigmas[2]);
        double[] resRowZ = new double[sizeZ];
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                double[] row = getPaddedRow(res, new int[] { x, y, 0 }, 2, radiusList[2]);
                filterRow(row, kernelZ, resRowZ);
                setRow(res, new int[] { x, y, 0 }, 2, resRowZ);
            }
        }
        
        return res;
    }
    
    private ScalarArray<?> createResultArray(ScalarArray<?> array)
    {
        ScalarArray.Factory<?> factory = this.factory != null ? this.factory : array.factory();
        return factory.create(array.size());
    }
    
    private double[] getPaddedRow(ScalarArray<?> array, int[] pos0, int dim, int pad)
    {
        int n = array.size(dim);
        int n2 = n + 2 * pad;
        
        double[] row = new double[n2];
        int[] pos = Arrays.copyOf(pos0, pos0.length);
        pos[dim] = 0;
        Arrays.fill(row, 0, pad, array.getValue(pos));
        for (int i = 0; i < n; i++)
        {
            pos[dim] = i;
            row[i + pad] = array.getValue(pos);
        }
        pos[dim] = n - 1;
        Arrays.fill(row, n+pad, n2, array.getValue(pos));
        
        return row;
    }
    
    private double[] createKernel(int r, double sigma)
    {
        double denom = 2 * sigma * sigma; 
        double k = 1.0 / (sigma * Math.sqrt(2 * Math.PI));
        
        double[] kernel = new double[2 * r + 1];
        double sum = 0;
        for (int i = 0; i < 2 * r + 1; i++)
        {
            double x = (i - r);
            double y = k * Math.exp(-x * x / denom);
            kernel[i] = y;
            sum += y;
        }
        
        // normalize kernel to have sum of weights equal to 1
        for (int i = 0; i < 2 * r + 1; i++)
        {
            kernel[i] /= sum;
        }
        
        return kernel;
    }

    /**
     * Computes cross-correlation of the input array with the specified kernel,
     * and stores the result in the specified array.
     * 
     * @param input
     *            the input array, with size n + 2 * r
     * @param kernel
     *            the array containing kernel values, with size 2*r+1
     * @param output
     *            the array to store the result, with size n
     */
    private void filterRow(double[] input, double[] kernel, double[] output)
    {
        int n = output.length;
        for (int i = 0; i < n; i++)
        {
            double sum = 0;
            for (int j = 0; j < kernel.length; j++)
            {
                sum += input[i + j] * kernel[j];
            }
            output[i] = sum;
        }
    }
    
    private void setRow(ScalarArray<?> array, int[] pos, int dim, double [] row)
    {
        for (int i = 0; i < array.size(dim); i++)
        {
            pos[dim] = i;
            array.setValue(pos, row[i]);
        }
    }

    private static final double[] adaptDim(double[] values, int n)
    {
        double[] res = new double[n];
        if (values.length == 1)
        {
            Arrays.fill(res, values[0]);
        }
        else
        {
            System.arraycopy(values, 0, res, 0, n);
        }
        return res;
    }
    
    private int[] computeKernelRadiusList(double[] sigmas)
    {
        if (this.diameters == null)
        {
            // compute kernel radius from sigma
            return Arrays.stream(sigmas).mapToInt(s -> (int) Math.round(s * 2)).toArray();
        }
        
        if (this.diameters.length == sigmas.length)
        {
            // compute kernel radius from diameter
            return Arrays.stream(diameters).map(s -> (int) ((s - 1) / 2)).toArray();
        }
        
        if (this.diameters.length == 1)
        {
            // use same radius for all dimensions
            int[] res = new int[sigmas.length];
            Arrays.fill(res, (this.diameters[0] - 1) / 2);
            return res;
        }
        
        throw new RuntimeException("The length of diameters array does not match sigma array");
    }
}
