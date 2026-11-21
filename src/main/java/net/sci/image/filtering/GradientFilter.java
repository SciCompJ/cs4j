/**
 * 
 */
package net.sci.image.filtering;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.color.ColorMaps;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.Float32VectorArray3D;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.VectorArray;
import net.sci.array.numeric.VectorArray2D;
import net.sci.array.numeric.VectorArray3D;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;

/**
 * Compute gradient of a scalar array using specified sigma value.
 * 
 * @author dlegland
 */
public class GradientFilter extends AlgoStub implements ImageArrayOperator
{
    double sigma;
    
    /**
     * Creates a new instance of Sobel Gradient operator.
     */
    public GradientFilter()
    {
        this(1.0);
    }

    /**
     * Creates a new instance of Sobel Gradient operator.
     */
    public GradientFilter(double sigma)
    {
        this.sigma = sigma;
    }

    public void processScalar(ScalarArray<?> source, VectorArray<?,?> target)
    {
        int nd1 = source.dimensionality();
        int nd2 = target.dimensionality();
        if (nd1 != nd2)
        {
            throw new IllegalArgumentException("Both arrays must have the same dimensionality");
        }
        
        if (!net.sci.array.Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Both arrays must have the same size");
        }
        
        // Choose the most appropriate implementation, depending on array dimensions
        switch (nd1)
        {
            case 2 -> processScalar2d(ScalarArray2D.wrap(source), VectorArray2D.wrap(target));
            case 3 -> processScalar3d(ScalarArray3D.wrap(source), VectorArray3D.wrap(target));
            default -> throw new IllegalArgumentException("Can process Sobel Gradient only on 2D or 3D images");
        };
    }

    public void processScalar2d(ScalarArray2D<?> source, VectorArray2D<?, ?> target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);

        if (target.size(0) != sizeX || target.size(1) != sizeY)
        {
            throw new IllegalArgumentException("Input image and output image must have same size");
        }

        // create box filtering operator from user settings
        int radius = (int) Math.ceil(sigma * 2);
        double[][] kernels = computeKernels(radius, sigma);
        
        for (int axis = 0; axis < 2; axis++)
        {
            ScalarArray2D<?> channel = Float32Array2D.create(sizeX, sizeY);
            channel.fillValues((x, y) -> source.getValue(x, y));
            
            for (int d = 0; d < 2; d++)
            {
                String msg = String.format("Process axis %d/%d", (d + 1), 2);
                fireStatusChanged(this, msg);

                double[] kernel = d == axis ? kernels[1] : kernels[0];
                Convolution1D op = new Convolution1D(d, kernel);
                op.setFactory(Float32Array.defaultFactory);

                channel = ScalarArray2D.wrap(op.processScalar(channel));
            }

            target.setChannel(axis, channel);
        }
    }
    
    public void processScalar3d(ScalarArray3D<?> source, VectorArray3D<?,?> target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        
        if (target.size(0) != sizeX || target.size(1) != sizeY || target.size(2) != sizeZ)
        {
            throw new IllegalArgumentException("Input image and output image must have same size");
        }
        
        // create box filtering operator from user settings
        int radius = (int) Math.ceil(sigma * 2);
        double[][] kernels = computeKernels(radius, sigma);
        
        // iterate over gradient components
        for (int axis = 0; axis < 3; axis++)
        {
            ScalarArray3D<?> channel = Float32Array3D.create(sizeX, sizeY, sizeZ);
            channel.fillValues((x, y, z) -> source.getValue(x, y, z));
            
            // iterate over processing directions: 1 gradient, two smooths
            for (int d = 0; d < 3; d++)
            {
                String msg = String.format("Process axis %d/%d, step %d", (axis + 1), 3, (d+1));
                fireStatusChanged(this, msg);

                double[] kernel = d == axis ? kernels[1] : kernels[0];
                Convolution1D op = new Convolution1D(d, kernel);
                op.setFactory(Float32Array.defaultFactory);

                channel = ScalarArray3D.wrap(op.processScalar(channel));
            }

            target.setChannel(axis, channel);
        }
    }
    
    private static final double[][] computeKernels(int radius, double sigma)
    {
        // initialize smoothing and derivative kernel
        int diameter = 2 * radius + 1;
        double[] ks = new double[diameter];
        double[] kd = new double[diameter];
        
        // compute values
        double sumKs = 0;
        double sumKd = 0;
        for (int i = 0; i < diameter; i++)
        {
            double x = i - radius;
            double kx = Math.exp(-(x * x) / (2 * sigma * sigma));
            ks[i] = kx;
            sumKs += ks[i];
            kd[i] = -kx * x / sigma;
            if (kd[i] > 0) sumKd += kd[i];
        }
        
        // normalization
        for (int i = 0; i < diameter; i++)
        {
            ks[i] /= sumKs;
            kd[i] /= sumKd;
        }
        
        return new double[][] {ks, kd};
    }
    

    /**
     * Overrides the default implementation to update display range at the end
     * of image creation.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Image process(Image image)
    {
        // retrieve input array and check validity
        Array<?> array = image.getData();
        if (!(array.sampleElement() instanceof Scalar<?>))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        
        // apply processing on a newly created result array
        VectorArray<?,?> result = createEmptyOutputArray(array);
        processScalar(ScalarArray.wrap((Array<Scalar>) array), result);
        
        // create result image and update default display
        Image resImage = new Image(result, image);
        double[] range = image.getDisplaySettings().getDisplayRange();
        double extent = range[1] - range[0];
        resImage.getDisplaySettings().setDisplayRange(new double[] {-extent, extent});
        resImage.getDisplaySettings().setColorMap(ColorMaps.BLUE_WHITE_RED.createColorMap(256));
        resImage.setName(image.getName() + "-grad");
        return resImage;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (!(array.sampleElement() instanceof Scalar<?>))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        VectorArray<?,?> result = createEmptyOutputArray(array);
        processScalar(ScalarArray.wrap((Array<Scalar>) array), result);
        return result;
    }

    private VectorArray<?,?> createEmptyOutputArray(Array<?> array)
    {
        if (!array.elementInstanceOf(Scalar.class))
        {
            throw new RuntimeException("Can not process arrays with elements of class: " + array.getClass());
        }
        
        if (array.dimensionality() == 2)
        {
            int size0 = array.size(0);
            int size1 = array.size(1);
            return Float32VectorArray2D.create(size0, size1, 2);
        }
        else if (array.dimensionality() == 3)
        {
            int size0 = array.size(0);
            int size1 = array.size(1);
            int size2 = array.size(2);
            return Float32VectorArray3D.create(size0, size1, size2, 3);
        }
        else
        {
            throw new RuntimeException("Can not process arrays with dimension " + array.dimensionality());
        }
    }

    public boolean canProcess(Array<?> array)
    {
        return array.elementInstanceOf(Scalar.class);
    }
}
