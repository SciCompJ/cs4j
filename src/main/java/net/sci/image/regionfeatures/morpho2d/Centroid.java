package net.sci.image.regionfeatures.morpho2d;

import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionTabularFeature;
import net.sci.table.Table;

/**
 * Computes centroid of each region within the label map.
 */
public class Centroid extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Centroid_X", "Centroid_Y"};
    
    /**
     * Default empty constructor.
     */
    public Centroid()
    {
    }
    
    @Override
    public Point2D[] compute(RegionFeatures data)
    {
        // retrieve image data
        Array<?> array = data.labelMap.getData();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray2D<?> labelMap = IntArray2D.wrap(IntArray.wrap((Array<? extends Int>) array));

        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        
        // size of image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);

        // Extract spatial calibration
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            sx = calib.getXAxis().getSpacing();
            sy = calib.getYAxis().getSpacing();
            ox = calib.getXAxis().getOrigin();
            oy = calib.getYAxis().getOrigin();
        }
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        int[] counts = new int[nLabels];
        double[] cx = new double[nLabels];
        double[] cy = new double[nLabels];
        
        // compute extreme coordinates of each region
        fireStatusChanged(this, "Compute centroids");
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = labelMap.getInt(x, y);
                if (label == 0)
                    continue;

                // do not process labels that are not in the input list 
                if (!labelIndices.containsKey(label))
                    continue;
                
                int index = labelIndices.get(label);
                
                cx[index] += x * sx;
                cy[index] += y * sy;
                counts[index]++;
            }
        }

        // normalize by number of pixels in each region
        for (int i = 0; i < nLabels; i++)
        {
            if (counts[i] != 0)
            {
                cx[i] /= counts[i];
                cy[i] /= counts[i];
            }
        }

        // add coordinates of origin pixel
        for (int i = 0; i < nLabels; i++)
        {
            if (counts[i] == 0) continue;

            cx[i] += ox;
            cy[i] += oy;
        }

        // create array of Point3D
        Point2D[] points = new Point2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            points[i] = new Point2D(cx[i], cy[i]);
        }

        return points;
    }

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Point2D[] array)
        {
            for (String colName : colNames)
            {
                table.addColumn(colName, new double[array.length]);
            }
            
            for (int r = 0; r < array.length; r++)
            {
                // current bounds
                Point2D centroid = array[r];
                
                // put bounds values
                table.setValue(r, colNames[0], centroid.x());
                table.setValue(r, colNames[1], centroid.y());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Bounds2D");
        }
    }
    
    @Override
    public String[] columnUnitNames(RegionFeatures data)
    {
        // setup table info
        Calibration calib = data.labelMap.getCalibration();
        String unitName = calib.getXAxis().getUnitName();
        return new String[] { unitName, unitName, unitName, unitName };
    }
    
}
