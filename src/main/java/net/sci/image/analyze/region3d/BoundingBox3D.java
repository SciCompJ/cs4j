/**
 * 
 */
package net.sci.image.analyze.region3d;

import java.util.HashMap;
import java.util.Map;

import net.sci.array.scalar.IntArray3D;
import net.sci.geom.geom3d.Bounds3D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.table.Table;

/**
 * @author dlegland
 *
 */
public class BoundingBox3D extends RegionAnalyzer3D<Bounds3D>
{
    // ==================================================
    // Static methods

    /**
     * Computes 3D bounding box of each region in the input 3D label image and returns
     * the result as an array of Bounds3D for each region.
     * 
     * @param labelImage
     *            the input image containing label of regions
     * @param labels
     *            an array of unique labels in image
     * @param calib
     *            the calibration of the image
     * @return an array of Bounds3D instances containing for each region its extent
     *         in each dimension
     */
    public static final Bounds3D[] boundingBoxes(IntArray3D<?> labelImage, int[] labels, Calibration calib)
    {
        return new BoundingBox3D().analyzeRegions(labelImage, labels, calib);
    }

    @Override
    public Bounds3D[] analyzeRegions(IntArray3D<?> array, int[] labels, Calibration calib)
    {
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        // Extract spatial calibration
        double sx = 1, sy = 1, sz = 1;
        double ox = 0, oy = 0, oz = 0;
        if (calib != null)
        {
            sx = calib.getXAxis().getSpacing();
            sy = calib.getYAxis().getSpacing();
            sz = calib.getZAxis().getSpacing();
            ox = calib.getXAxis().getOrigin();
            oy = calib.getYAxis().getOrigin();
            oz = calib.getZAxis().getOrigin();
        }
        
        // create associative array     to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        double[] zmin = new double[nLabels];
        double[] zmax = new double[nLabels];
        
        // initialize to extreme values
        for (int i = 0; i < nLabels; i++)
        {
            xmin[i] = Double.POSITIVE_INFINITY;
            xmax[i] = Double.NEGATIVE_INFINITY;
            ymin[i] = Double.POSITIVE_INFINITY;
            ymax[i] = Double.NEGATIVE_INFINITY;
            zmin[i] = Double.POSITIVE_INFINITY;
            zmax[i] = Double.NEGATIVE_INFINITY;
        }

        // compute extreme coordinates of each region
        fireStatusChanged(this, "Compute bounds");
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = array.getInt(x, y, z);
                    if (label == 0)
                        continue;

                    // do not process labels that are not in the input list 
                    if (!labelIndices.containsKey(label))
                        continue;
                    int index = labelIndices.get(label);

                    xmin[index] = Math.min(xmin[index], x);
                    xmax[index] = Math.max(xmax[index], x + 1);
                    ymin[index] = Math.min(ymin[index], y);
                    ymax[index] = Math.max(ymax[index], y + 1);
                    zmin[index] = Math.min(zmin[index], z);
                    zmax[index] = Math.max(zmax[index], z + 1);
                }
            }
        }
        
        // create bounding box instances
        Bounds3D[] boxes = new Bounds3D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            boxes[i] = new Bounds3D(
                    xmin[i] * sx + ox, xmax[i] * sx + ox,
                    ymin[i] * sy + oy, ymax[i] * sy + oy, 
                    zmin[i] * sz + oz, zmax[i] * sz + oz);
        }
        return boxes;
    }

    @Override
    public Table createTable(Map<Integer, Bounds3D> results)
    {
        // Initialize a new result table
        Table table = Table.create(results.size(), 6);
        table.setColumnNames(new String[] {"Box.XMin", "Box.XMax", "Box.YMin", "Box.YMax", "Box.ZMin", "Box.ZMax"});
    
        // convert Bounds3D instances into values within table
        int row = 0;
        for (int label : results.keySet())
        {
            // current diameter
            Bounds3D box = results.get(label);
            
            // add an entry to the resulting data table
            table.setRowName(row, Integer.toString(label));
            
            // coordinates of bounds
            table.setValue(row, 0, box.getXMin());
            table.setValue(row, 1, box.getXMax());
            table.setValue(row, 2, box.getYMin());
            table.setValue(row, 3, box.getYMax());
            table.setValue(row, 4, box.getZMin());
            table.setValue(row, 5, box.getZMax());
        }
    
        return table;
    }
}
