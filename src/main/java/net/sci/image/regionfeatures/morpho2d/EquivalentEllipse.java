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
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Ellipse2D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionTabularFeature;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * Compute equivalent ellipse of regions stored within label map.
 * 
 * The equivalent ellipse of a region is computed such that is has same second
 * order moments as the region. The code is adapted from that of MorphoLibJ.
 */
public class EquivalentEllipse extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns, without unit name.
     */
    private static final String[] colNames = new String[] { "Ellipse_Center_X", "Ellipse_Center_Y", "Ellipse_Radius_1", "Ellipse_Radius_2", "Ellipse_Orientation" };

    /**
     * Empty constructor.
     */
    public EquivalentEllipse()
    {
    }

    @Override
    public Ellipse2D[] compute(RegionFeatures data)
    {
        // retrieve image data
        Array<?> array = data.labelMap.getData();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray2D<?> labelMap = IntArray2D.wrap(IntArray.wrap((Array<? extends Int>) array));
        
        // retrieve label map and list of labels
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

        // allocate memory for temporary arrays
        int nLabels = labels.length;
        int[] counts = new int[nLabels];
        double[] cx = new double[nLabels];
        double[] cy = new double[nLabels];
        double[] Ixx = new double[nLabels];
        double[] Iyy = new double[nLabels];
        double[] Ixy = new double[nLabels];

        fireStatusChanged(this, "Compute centroids");
        // compute centroid of each region
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
            cx[i] = cx[i] / counts[i];
            cy[i] = cy[i] / counts[i];
        }

        // compute centered inertia matrix of each region
        fireStatusChanged(this, "Compute Inertia Matrices");
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = labelMap.getInt(x, y);
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

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Ellipse2D[] ellipses)
        {
            // add new empty columns to table
            String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
            for (int i = 0; i < 4; i++)
            {
                NumericColumn col = NumericColumn.create(colNames[i], ellipses.length);
                if (unitName != null && !unitName.isBlank())
                {
                    col.setUnitName(unitName);
                }
                table.addColumn(col);
            }
            NumericColumn orientCol = NumericColumn.create(colNames[4], ellipses.length);
            orientCol.setUnitName("degree");
            table.addColumn(orientCol);
            
            for (int r = 0; r < ellipses.length; r++)
            {
                // current ellipse
                Ellipse2D ellipse = ellipses[r];
                
                // coordinates of centroid
                Point2D center = ellipse.center();
                table.setValue(r, colNames[0], center.x());
                table.setValue(r, colNames[1], center.y());
                
                // ellipse size
                table.setValue(r, colNames[2], ellipse.semiMajorAxisLength());
                table.setValue(r, colNames[3], ellipse.semiMinorAxisLength());
        
                // ellipse orientation (degrees)
                table.setValue(r, colNames[4], ellipse.orientation());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Ellipse2D");
        }
    }

}
