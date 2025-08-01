/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionTabularFeature;
import net.sci.table.Table;

/**
 * Computes the bounds of each region within a label map.
 */
public class Bounds extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Bounds2D_XMin", "Bounds2D_XMax", "Bounds2D_YMin", "Bounds2D_YMax"};
    
    /**
     * Default empty constructor.
     */
    public Bounds()
    {
    }
    
    @Override
    public Bounds2D[] compute(RegionFeatures data)
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
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        
        // initialize to extreme values
        for (int i = 0; i < nLabels; i++)
        {
            xmin[i] = Double.POSITIVE_INFINITY;
            xmax[i] = Double.NEGATIVE_INFINITY;
            ymin[i] = Double.POSITIVE_INFINITY;
            ymax[i] = Double.NEGATIVE_INFINITY;
        }

        // compute extreme coordinates of each region
        fireStatusChanged(this, "Compute bounds");
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
                
                xmin[index] = Math.min(xmin[index], x);
                xmax[index] = Math.max(xmax[index], x);
                ymin[index] = Math.min(ymin[index], y);
                ymax[index] = Math.max(ymax[index], y);
            }
        }

        // create bounding box instances
        Bounds2D[] boxes = new Bounds2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            boxes[i] = new Bounds2D(
                    xmin[i] * sx + ox, (xmax[i] + 1) * sx + ox,
                    ymin[i] * sy + oy, (ymax[i] + 1) * sy + oy);
        }
        return boxes;
    }

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Bounds2D[] array)
        {
            for (String colName : colNames)
            {
                table.addColumn(colName, new double[array.length]);
            }
            
            for (int r = 0; r < array.length; r++)
            {
                // current bounds
                Bounds2D bound = array[r];
                
                // put bounds values
                table.setValue(r, colNames[0], bound.xMin());
                table.setValue(r, colNames[1], bound.xMax());
                table.setValue(r, colNames[2], bound.yMin());
                table.setValue(r, colNames[3], bound.yMax());
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
