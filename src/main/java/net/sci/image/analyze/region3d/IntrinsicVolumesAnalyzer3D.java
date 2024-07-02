/**
 * 
 */
package net.sci.image.analyze.region3d;

import java.util.Map;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.array.numeric.IntArray3D;
import net.sci.image.Calibration;
import net.sci.table.Table;

/**
 * Computation of intrinsic volumes (Volume, Surface Area, Mean Breadth and
 * Euler Number) for 3D binary or label images, based on the
 * <code>RegionAnalyzer3D</code> interface.
 * 
 * The <code>IntrinsicVolumes3D</code> class provides static classes to
 * facilitate usage when no algorithm monitoring is necessary.
 * 
 * @see inra.ijpb.measure.IntrinsicVolumes3D
 * @see inra.ijpb.measure.region2d.IntrinsicVolumesAnalyzer2D
 * 
 * @author dlegland
 *
 */
public class IntrinsicVolumesAnalyzer3D extends RegionAnalyzer3D<IntrinsicVolumesAnalyzer3D.Result>
        implements AlgoListener
{
    // ==================================================
    // Static methods


    // ==================================================
    // Class members

    boolean computeVolume = true;
    boolean computeSurfaceArea = true;
    boolean computeMeanBreadth = true;
    boolean computeEulerNumber = true;
    
    /**
     * Number of directions for computing surface area or mean breadth with
     * Crofton Formula. Default is 13.
     */
    int directionNumber = 13;

    /**
     * Connectivity for computing 3D Euler number. Default is 6. 
     */
    int connectivity = 6;
   
    
    // ==================================================
    // Constructors

    /**
     * Default empty constructor.
     */
    public IntrinsicVolumesAnalyzer3D()
    {
    }
    
    
    // ==================================================
    // Implementation of RegionAnalyzer3D methods

    /**
     * @return the directionNumber used to compute surface area and mean breadth
     */
    public int getDirectionNumber()
    {
        return directionNumber;
    }

    /**
     * @param directionNumber
     *            the number of directions used to compute surface area and mean
     *            breadth (either 3 or 13, default is 13)
     */
    public void setDirectionNumber(int directionNumber)
    {
        this.directionNumber = directionNumber;
    }

    /**
     * @return the connectivity used to compute Euler number
     */
    public int getConnectivity()
    {
        return connectivity;
    }

    /**
     * @param connectivity
     *            the connectivity used to compute Euler number (either 6 or 26,
     *            default is 6)
     */
    public void setConnectivity(int connectivity)
    {
        this.connectivity = connectivity;
    }


    // ==================================================
    // Implementation of RegionAnalyzer3D methods

    @Override
    public Table createTable(Map<Integer, Result> results)
    {
        // Initialize a new result table
        Table table = Table.create(results.size(), 4);
        table.setColumnNames(new String[] {"Volume", "SurfaceArea", "MeanBreadth", "EulerNumber"});
        
        // populate table
        int row = 0;
        for (int label : results.keySet())
        {
            // current diameter
            Result res = results.get(label);
            
            // add an entry to the resulting data table
            table.setRowName(row, Integer.toString(label));

            // add each measure
            table.setValue(row, 0, res.volume);
            table.setValue(row, 1, res.surfaceArea);
            table.setValue(row, 2, res.meanBreadth);
            table.setValue(row, 3, res.eulerNumber);
        }
    
        return table;
    }

    @Override
    public Result[] analyzeRegions(IntArray3D<?> image, int[] labels, Calibration calib)
    {
        // Histogram of binary configurations for each region label
        BinaryConfigurationsHistogram3D algo = new BinaryConfigurationsHistogram3D();
        algo.addAlgoListener(this);
        int[][] histograms = algo.process(image, labels);

        // initialize result array
        Result[] results = new Result[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            results[i] = new Result();
        }
        
        // Compute volume if necessary
        if (this.computeVolume)
        {
            double[] volumeLut = IntrinsicVolumes3DUtils.volumeLut(calib);
            double[] volumes = BinaryConfigurationsHistogram3D.applyLut(histograms, volumeLut);
            for (int i = 0; i < labels.length; i++)
            {
                results[i].volume = volumes[i];
            }
        }

        // Compute surface area if necessary
        if (this.computeVolume)
        {
            double[] areaLut = IntrinsicVolumes3DUtils.surfaceAreaLut(calib, this.directionNumber);
            double[] areas = BinaryConfigurationsHistogram3D.applyLut(histograms, areaLut);
            for (int i = 0; i < labels.length; i++)
            {
                results[i].surfaceArea = areas[i];
            }
        }
        
        // Compute mean breadth if necessary
        if (this.computeMeanBreadth)
        {
            double[] breadthLut = IntrinsicVolumes3DUtils.meanBreadthLut(calib, this.directionNumber, 8);
            double[] breadths = BinaryConfigurationsHistogram3D.applyLut(histograms, breadthLut);
            for (int i = 0; i < labels.length; i++)
            {
                results[i].meanBreadth = breadths[i];
            }
        }
        
        // Compute Euler number if necessary
        if (this.computeEulerNumber)
        {
            double[] eulerLut = IntrinsicVolumes3DUtils.eulerNumberLut(this.connectivity);
            double[] eulers = BinaryConfigurationsHistogram3D.applyLut(histograms, eulerLut);
            for (int i = 0; i < labels.length; i++)
            {
                results[i].eulerNumber = eulers[i];
            }
        }
        
        return results;
    }
    
    // ==================================================
    // Implementation of Algolistener interface

    /* (non-Javadoc)
     * @see inra.ijpb.algo.AlgoListener#algoProgressChanged(inra.ijpb.algo.AlgoEvent)
     */
    @Override
    public void algoProgressChanged(AlgoEvent evt)
    {
        this.fireProgressChanged(evt);
    }

    /* (non-Javadoc)
     * @see inra.ijpb.algo.AlgoListener#algoStatusChanged(inra.ijpb.algo.AlgoEvent)
     */
    @Override
    public void algoStatusChanged(AlgoEvent evt)
    {
        this.fireStatusChanged(evt);
    }

    // ==================================================
    // Inner class for storing results
    
    /**
     * Inner class for storing results.
     */
    public class Result
    {
    	/** The volume of the region */
        public double volume = Double.NaN;
    	/** The surface area of the region */
        public double surfaceArea = Double.NaN;
    	/** The mean breadth of the region (proportional to the integral of average curvature)*/
        public double meanBreadth = Double.NaN;
    	/** The Euler Number of the region */
        public double eulerNumber = Double.NaN;
        
        /**
         * Empty constructor.
         */
        public Result()
        {
        }
        
        /**
		 * Creates a new data class for storing intrinsic volume measurements.
		 * 
		 * @param volume
		 *            the volume of the region.
		 * @param surf
		 *            the surface area of the region.
		 * @param breadth
		 *            the mean breadth of the region
		 * @param euler
		 *            the Euler number of the region
		 */
        public Result(double volume, double surf, double breadth, double euler)
        {
            this.volume = volume;
            this.surfaceArea = surf;
            this.meanBreadth = breadth;
            this.eulerNumber = euler;
        }
    }
}
