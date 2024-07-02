/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.HashMap;
import java.util.Map;

import net.sci.array.numeric.IntArray2D;
import net.sci.axis.NumericalAxis;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Ellipse2D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.table.Table;

/**
 * Computation of equivalent ellipses for each region within a label map.
 * 
 * @author dlegland
 *
 */
public class EquivalentEllipse2D extends RegionAnalyzer2D<Ellipse2D>
{
    /**
     * Utility method that transforms the mapping between labels and equivalent
     * ellipses instances into a Table containing summary parameters of all
     * ellipses.
     * 
     * @param map
     *            the mapping between labels and Inertia Ellipses
     * @return a ResultsTable that can be displayed with ImageJ.
     */
    @Override
    public Table createTable(Map<Integer, Ellipse2D> map)
    {
        // Initialize a new result table
        Table table = Table.create(map.size(), 6);
        table.setColumnNames(new String[] {
                "Label", 
                "Ellipse.CenterX", "Ellipse.CenterY", 
                "Ellipse.Radius1", "Ellipse.Radius2", 
                "Ellipse.Orientation"});
    
        // convert the (key, value) pairs in the map into a table with one row
        // per label
        int row = 0;
        for (int label : map.keySet())
        {
            // current diameter
            Ellipse2D ellipse = map.get(label);
            
            // add an entry to the resulting data table
            table.setValue(row, "Label", label);
            
            // coordinates of centroid
            Point2D center = ellipse.center();
            table.setValue(row, "Ellipse.CenterX", center.x());
            table.setValue(row, "Ellipse.CenterY", center.y());
            
            // ellipse size
            table.setValue(row, "Ellipse.Radius1", ellipse.semiMajorAxisLength());
            table.setValue(row, "Ellipse.Radius2", ellipse.semiMinorAxisLength());
    
            // ellipse orientation (degrees)
            table.setValue(row, "Ellipse.Orientation", ellipse.orientation());
            
            row++;
        }
    
        return table;
    }

    /**
     * Computes equivalent ellipse of each region within the input label image.
     * 
     * @param array
     *            the input array containing label of regions
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the calibration of the image
     * @return an array of Ellipse2D representing the calibrated ellipses 
     */
    @Override
    public Ellipse2D[] analyzeRegions(IntArray2D<?> array, int[] labels, Calibration calib)
    {
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // Extract spatial calibration
        double ox = 0.0, sx = 1.0;
        double oy = 0.0, sy = 1.0;
        if (calib != null)
        {
            NumericalAxis xAxis = calib.getXAxis(); 
            ox = xAxis.getOrigin();
            sx = xAxis.getSpacing();
            NumericalAxis yAxis = calib.getYAxis(); 
            oy = yAxis.getOrigin();
            sy = yAxis.getSpacing();
        }
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        int[] counts = new int[nLabels];
        double[] cx = new double[nLabels];
        double[] cy = new double[nLabels];

        fireStatusChanged(this, "Compute centroids");
        // compute centroid of each region
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = array.getInt(x, y);
                if (label == 0)
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
            cx[i] = cx[i] / counts[i];
            cy[i] = cy[i] / counts[i];
        }

        // compute centered inertia matrix of each label
        fireStatusChanged(this, "Compute Inertia Matrices");
        double[] Ixx = new double[nLabels];
        double[] Iyy = new double[nLabels];
        double[] Ixy = new double[nLabels];
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = array.getInt(x, y);
                if (label == 0)
                    continue;

                int index = labelIndices.get(label);
                double x2 = x * sx - cx[index];
                double y2 = y * sy - cy[index];
                Ixx[index] += x2 * x2;
                Ixy[index] += x2 * y2;
                Iyy[index] += y2 * y2;
            }
        }

        // normalize by number of pixels in each region
        for (int i = 0; i < nLabels; i++)
        {
            Ixx[i] = Ixx[i] / counts[i] + sx / 12.0;
            Ixy[i] = Ixy[i] / counts[i];
            Iyy[i] = Iyy[i] / counts[i] + sy / 12.0;
        }

        // Create array of result
        Ellipse2D[] ellipses = new Ellipse2D[nLabels];
        
        // compute ellipse parameters for each region
        fireStatusChanged(this, "Compute Ellipses");
        for (int i = 0; i < nLabels; i++) 
        {
            Point2D center = new Point2D(cx[i] + ox, cy[i] + oy);
            ellipses[i] = Ellipse2D.fromInertiaCoefficients(center, Ixx[i], Iyy[i], Ixy[i]);
        }

        return ellipses;
    }

}
