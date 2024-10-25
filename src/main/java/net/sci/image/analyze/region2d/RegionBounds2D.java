/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.HashMap;
import java.util.Map;

import net.sci.array.numeric.IntArray2D;
import net.sci.axis.NumericalAxis;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.table.Table;
import net.sci.table.impl.DefaultTable;

/**
 * @author dlegland
 *
 */
public class RegionBounds2D extends RegionAnalyzer2D<Bounds2D>
{
    // ==================================================
    // Implementation of RegionAnalyzer interface

    /**
     * Utility method that transforms the mapping between labels and Bounds2D
     * instances into a Table that can be displayed within a GUI.
     * 
     * @param map
     *            the mapping between labels and bounds
     * @return a Table.
     */
    public Table createTable(Map<Integer, Bounds2D> map)
    {
        // Initialize a new result table
        DefaultTable table = new DefaultTable(map.size(), 4);
        table.setColumnNames(new String[] {"Bounds.XMin", "Bounds.XMax", "Bounds.YMin", "Bounds.YMax"});
    
        // convert the (key, value) pairs in the map into a table with one row
        // per label
        int row = 0;
        for (int label : map.keySet())
        {
            table.setRowName(row, Integer.toString(label));

            // current diameter
            Bounds2D bound = map.get(label);
            
            table.setValue(row, 0, bound.getXMin());
            table.setValue(row, 1, bound.getXMax());
            table.setValue(row, 2, bound.getYMin());
            table.setValue(row, 3, bound.getYMax());
            row++;
        }
    
        return table;
    }

    
    /**
     * Computes bounds of each region in input label image.
     * 
     * @param image
     *            the input image containing label of particles
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the (spatial) calibration of the image. Can be null, in that
     *            case default calibration will be used.
     * @return an array of Point2D representing the calibrated centroid
     *         coordinates
     */
    public Bounds2D[] analyzeRegions(IntArray2D<?> image, int[] labels, Calibration calib)
    {
        // create associative array to know index of each label
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // Extract spatial calibration
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            NumericalAxis xAxis = calib.getXAxis(); 
            NumericalAxis yAxis = calib.getYAxis(); 
            sx = xAxis.getSpacing();
            sy = yAxis.getSpacing();
            ox = xAxis.getOrigin();
            oy = yAxis.getOrigin();
        }
        
        // allocate memory for result
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            xmin[i] = Double.POSITIVE_INFINITY; 
            xmax[i] = Double.NEGATIVE_INFINITY;
            ymin[i] = Double.POSITIVE_INFINITY; 
            ymax[i] = Double.NEGATIVE_INFINITY;
        }

        // size of input image
        int sizeX = image.size(0);
        int sizeY = image.size(1);

        // compute min/max coords of each region
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = image.getInt(x, y);
                if (label == 0)
                    continue;

                // do not process labels that are not in the input list 
                if (!labelIndices.containsKey(label))
                    continue;
                
                int index = labelIndices.get(label);

                xmin[index] = Math.min(xmin[index], x - 0.5);
                xmax[index] = Math.max(xmax[index], x + 0.5);
                ymin[index] = Math.min(ymin[index], y - 0.5);
                ymax[index] = Math.max(ymax[index], y + 0.5);
            }
        }
        
        // create result Bounds2D instances
        Bounds2D[] boxes = new Bounds2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            boxes[i] = new Bounds2D(xmin[i] * sx + ox, xmax[i] * sx + ox, ymin[i] * sy + oy, ymax[i] * sy + oy);
        }
        
        return boxes;
    }
}
