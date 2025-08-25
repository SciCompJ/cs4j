/**
 * 
 */
package net.sci.image.binary.distmap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.Calibration;
import net.sci.image.Image;
import net.sci.image.ImageOperator;
import net.sci.image.ImageType;
import net.sci.image.binary.distmap.DistanceTransform.Result;

/**
 * Computes Euclidean distance transform of a 2D or 3D binary array using
 * algorithm of Saito and Toriwaki (1994). Uses floating point computations.
 * 
 * Details:
 * <ul>
 * <li>works for both 2D or 3D arrays</li>
 * <li>uses floating point computations</li>
 * <li>can manage spatial calibration</li>
 * </ul>
 */
public class SaitoToriwakiDistanceTransform extends AlgoStub implements ArrayOperator, ImageOperator
{
    // =============================================================
    // Class members

    /**
     * The factory to use for creating the result array.
     */
    private ScalarArray.Factory<?> factory = Float32Array.defaultFactory;
    
    
    // =============================================================
    // Constructor

    /**
     * Default empty constructor.
     */
    public SaitoToriwakiDistanceTransform()
    {
    }
    
    
    /**
     * Set the factory for creating the result array.
     * 
     * @param factory
     *            the factory for creating the result array.
     */
    public void setFactory(ScalarArray.Factory<?> factory)
    {
        this.factory = factory;
    }
    

    // ==================================================
    // Override the ImageArrayProcessor interface
    
    /**
     * Overrides default behavior of ImageArrayOperator interface to return an
     * instance of Image initialized with the type "DISTANCE", and a display
     * range between 0 and the maximum distance within the map.
     * 
     * @param image
     *            the image to process (image data must be binary)
     * @return a new Image instance of type DISTANCE, containing the distance
     *         map of the input image and preserving spatial calibration.
     */
    @Override
    public Image process(Image image)
    {
        // check validity of array data type
        Array<?> array = image.getData();
        if (!array.elementClass().isAssignableFrom(Binary.class))
        {
            throw new IllegalArgumentException("Requires an input array containing Binary data");
        }
        
        // retrieve spatial calibration
        Calibration calib = image.getCalibration();
        double[] spacings = new double[array.dimensionality()];
        for (int d = 0; d < array.dimensionality(); d++)
        {
            spacings[d] = calib.getAxis(d).getSpacing();
        }
        
        // compute distance map together with max distance
        BinaryArray binaryArray = BinaryArray.wrap(array);
        Result res = computeResult(binaryArray, spacings);
        
        // create result image
        Image resultImage = new Image(res.distanceMap, ImageType.DISTANCE, image);
        resultImage.getDisplaySettings().setDisplayRange(new double[] {0, res.maxDistance});
        return resultImage;
    }
    
    /**
     * Computes the distance map of the specified binary array and using the
     * specified array of spacings, and returns the result within an instance of
     * DistanceMap.Result containing both the distance map and the maximum
     * distance within the map.
     * 
     * @param array
     *            the binary array to process
     * @param spacings
     *            the physical spacings between array elements (with as many
     *            elements as array dimensionality)
     * @return an instance of DistanceMap.Result containing both the distance
     *         map and the maximum distance within the map
     */
    public Result computeResult(BinaryArray array, double[] spacings)
    {
        // allocate memory for result array
        ScalarArray<?> output = factory.create(array.size());
        
        // dispatch processing according to dimensionality
        double distMax = switch (array.dimensionality())
        {
            case 2 -> distanceMapSquared2d(BinaryArray2D.wrap(array), ScalarArray2D.wrapScalar2d(output), spacings);
            case 3 -> distanceMapSquared3d(BinaryArray3D.wrap(array), ScalarArray3D.wrapScalar3d(output), spacings);
            default -> throw new RuntimeException(
                    "Unable to manage binary arrays with dimension " + array.dimensionality());
        };

        // convert squared distance to distance
        output.apply(Math::sqrt, output);
        distMax = Math.sqrt(distMax);
        
        return new Result(output, distMax);
    }
    
    
    // =============================================================
    // Computation methods

    /**
     * Computes the distance map of the specified binary array, using a default
     * spatial calibration equal to 1 for each dimension.
     * 
     * @param array
     *            the binary array to process
     * @return the distance map
     */
    public ScalarArray2D<?> process2d(BinaryArray2D array)
    {
        return distanceMap2d(array, new double[] { 1.0, 1.0 });
    }
    
    /**
     * Computes the distance map of the specified binary array and the specified
     * array of spacings between array elements.
     * 
     * @param array
     *            the binary array to process
     * @param spacings
     *            the spacings between array elements (with as many elements as
     *            array dimensionality)
     * @return the distance map
     */
    public ScalarArray2D<?> distanceMap2d(BinaryArray2D array, double[] spacings)
    {
        if (spacings.length != 2)
        {
            throw new IllegalArgumentException("Spacing array must have length 2");
        }
        ScalarArray2D<?> output = ScalarArray2D.wrapScalar2d(factory.create(array.size()));
        
        processStep1(array, output, spacings[0]);
        processStep2(array, output, spacings[1]);

        // convert squared distance to distance
        output.apply(Math::sqrt, output);
        
        return output;
    }
    
    /**
     * Computes the squared distance map of the specified binary array and the
     * specified array of spacings between array elements.
     * 
     * @param array
     *            the binary array to process
     * @param output
     *            the array of scalar values used to store the computation result
     * @param spacings
     *            the spacings between array elements (with as many elements as
     *            array dimensionality)
     * @return the squared distance map
     */
    public double distanceMapSquared2d(BinaryArray2D array, ScalarArray2D<?> output, double[] spacings)
    {
        if (spacings.length != 2)
        {
            throw new IllegalArgumentException("Spacing array must have length 2");
        }
        
        processStep1(array, output, spacings[0]);
        return processStep2(array, output, spacings[1]);
    }
    
    private void processStep1(BinaryArray2D array, ScalarArray2D<?> output, double spacingX)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        double absoluteMaximum = (sizeX + sizeY) * spacingX;
        
        // forward scan
        for (int y = 0; y < sizeY; y++)
        {
            double df = absoluteMaximum;
            for (int x = 0; x < sizeX; x++)
            {
                // either increment or reset current distance
                df = array.getBoolean(x, y) ? df + spacingX : 0;
                output.setValue(x, y, df * df);
            }
        }
        
        // backward scan
        for (int y = sizeY - 1; y >= 0; y--)
        {
            double db = absoluteMaximum;
            for (int x = sizeX - 1; x >= 0; x--)
            {
                // either increment or reset current distance
                db = array.getBoolean(x, y) ? db + spacingX : 0;
                output.setValue(x, y, Math.min(output.getValue(x, y), db * db));
            }
        }
    }

    private double processStep2(BinaryArray2D array, ScalarArray2D<?> output, double spacingY)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        double[] buffer = new double[sizeY];
        double distMax = 0.0;
        double dy2 = spacingY * spacingY;
        
        // iterate over y-columns of the array
        for (int x = 0; x < sizeX; x++)
        {
            // init buffer
            for (int y = 0; y < sizeY; y++)
            {
                buffer[y] = output.getValue(x, y);
            }
            
            for (int y = 0; y < sizeY; y++)
            {
                double dist = buffer[y];
                if (dist > 0)
                {
                    // compute bounds of interval to look for min distance 
                    int rMax = (int) Math.ceil(Math.sqrt(dist) / spacingY + 1);
                    int rStart = Math.min(rMax, y);
                    int rEnd = Math.min(rMax, sizeY - y);
                    
                    for (int n = -rStart; n < rEnd; n++)
                    {
                        double w = buffer[y + n] + n * n * dy2;
                        if (w < dist) dist = w;
                    }
                    
                    if (dist < buffer[y])
                    {
                        output.setValue(x, y, dist);
                    }
                    
                    if (dist > distMax)
                    {
                        distMax = dist;
                    }
                }
            }
        }
        
        return distMax;
    }

    /**
     * Computes the distance map of the specified binary array, using a default
     * spatial calibration equal to 1 for each dimension.
     * 
     * @param array
     *            the binary array to process
     * @return the distance map
     */
    public ScalarArray3D<?> process3d(BinaryArray3D array)
    {
        return distanceMap3d(array, new double[] { 1.0, 1.0, 1.0 });
    }
    
    /**
     * Computes the distance map of the specified binary array and the specified
     * array of spacings between array elements.
     * 
     * @param array
     *            the binary array to process
     * @param spacings
     *            the spacings between array elements (with as many elements as
     *            array dimensionality)
     * @return the distance map
     */
    public ScalarArray3D<?> distanceMap3d(BinaryArray3D array, double[] spacings)
    {
        if (spacings.length != 3)
        {
            throw new IllegalArgumentException("Spacing array must have length 3");
        }
        ScalarArray3D<?> output = ScalarArray3D.wrapScalar3d(factory.create(array.size()));
        
        distanceMapSquared3d(array, output, spacings);

        // convert squared distance to distance
        output.apply(Math::sqrt, output);
        
        return output;
    }
    
    /**
     * Computes the squared distance map of the specified binary array and the
     * specified array of spacings between array elements.
     * 
     * @param array
     *            the binary array to process
     * @param output
     *            the array of scalar values used to store the computation result
     * @param spacings
     *            the spacings between array elements (with as many elements as
     *            array dimensionality)
     * @return the squared distance map
     */
    public double distanceMapSquared3d(BinaryArray3D array, ScalarArray3D<?> output, double[] spacings)
    {
        if (spacings.length != 3)
        {
            throw new IllegalArgumentException("Spacing array must have length 3");
        }
        
        processStep1(array, output, spacings[0]);
        processStep2(array, output, spacings[1]);
        return processStep3(array, output, spacings[2]);
    }
    
    private void processStep1(BinaryArray3D array, ScalarArray3D<?> output, double spacingX)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        double sx = spacingX;
        double absoluteMaximum = (sizeX + sizeY) * sx;
        
        // forward scan
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                double df = absoluteMaximum;
                for (int x = 0; x < sizeX; x++)
                {
                    // either increment or reset current distance
                    df = array.getBoolean(x, y, z) ? df + sx : 0;
                    output.setValue(x, y, z, df * df);
                }
            }
        }
        
        // backward scan
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = sizeY - 1; y >= 0; y--)
            {
                double db = absoluteMaximum;
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    // either increment or reset current distance
                    db = array.getBoolean(x, y, z) ? db + sx : 0;
                    output.setValue(x, y, z, Math.min(output.getValue(x, y, z), db * db));
                }
            }
        }
    }

    private void processStep2(BinaryArray3D array, ScalarArray3D<?> output, double spacingY)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        double sy2 = spacingY * spacingY;
        
        double[] buffer = new double[sizeY];
        double distMax = 0.0;

        // iterate over y-columns of the array
        for (int z = 0; z < sizeZ; z++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // init buffer
                for (int y = 0; y < sizeY; y++)
                {
                    buffer[y] = output.getValue(x, y, z);
                }

                for (int y = 0; y < sizeY; y++)
                {
                    double dist = buffer[y];
                    if (dist > 0)
                    {
                        // compute bounds of interval to look for min distance 
                        int rMax = (int) Math.ceil(Math.sqrt(dist) / spacingY + 1);
                        int rStart = Math.min(rMax, y);
                        int rEnd = Math.min(rMax, sizeY - y);

                        for (int n = -rStart; n < rEnd; n++)
                        {
                            double w = buffer[y + n] + n * n * sy2;
                            if (w < dist) dist = w;
                        }

                        if (dist < buffer[y])
                        {
                            output.setValue(x, y, z, dist);
                        }

                        if (dist > distMax)
                        {
                            distMax = dist;
                        }
                    }
                }
            }
        }
    }

    private double processStep3(BinaryArray3D array, ScalarArray3D<?> output, double spacingZ)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        double sz2 = spacingZ * spacingZ;
        
        double[] buffer = new double[sizeZ];
        double distMax = 0.0;

        // iterate over y-columns of the array
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // init buffer
                for (int z = 0; z < sizeZ; z++)
                {
                    buffer[z] = output.getValue(x, y, z);
                }

                for (int z = 0; z < sizeZ; z++)
                {
                    double dist = buffer[z];
                    if (dist > 0)
                    {
                        // compute bounds of interval to look for min distance 
                        int rMax = (int) Math.ceil(Math.sqrt(dist) / spacingZ + 1);
                        int rStart = Math.min(rMax, z);
                        int rEnd = Math.min(rMax, sizeZ - z);

                        for (int n = -rStart; n < rEnd; n++)
                        {
                            double w = buffer[z + n] + n * n * sz2;
                            if (w < dist) dist = w;
                        }

                        if (dist < buffer[z])
                        {
                            output.setValue(x, y, z, dist);
                        }

                        if (dist > distMax)
                        {
                            distMax = dist;
                        }
                    }
                }
            }
        }

        return distMax;
    }
    
    
    // ==================================================
    // Specialization of ArrayOperator interface

    /**
     * Computes the distance map of the specified binary array, using a default
     * spatial calibration equal to 1 for each dimension.
     * 
     * The input array must be an instance of BinaryArray.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new instance of ScalarArray
     * @throws IllegalArgumentException
     *             if the input array is not an instance of BinaryArray
     */
    @Override
    public <T> ScalarArray<? extends Scalar<?>> process(Array<T> array)
    {
        // check validity of array data type
        if (!array.elementClass().isAssignableFrom(Binary.class))
        {
            throw new IllegalArgumentException("Requires an input array containing Binary data");
        }
        
        BinaryArray binaryArray = BinaryArray.wrap(array); 
        return switch (array.dimensionality())
        {
            case 2 -> process2d(BinaryArray2D.wrap(binaryArray));
            case 3 -> process3d(BinaryArray3D.wrap(binaryArray));
            default -> throw new RuntimeException("Unable to manage binary arrays with dimension " + array.dimensionality());
        };
    }

    /**
     * Override default behavior to check if input array contains binary data.
     * 
     * @return true if input array contains binary data.
     */
    @Override
    public boolean canProcess(Array<?> array)
    {
        return array.elementClass().isAssignableFrom(Binary.class);
    }
}
