/**
 * 
 */
package net.sci.image.analyze.region3d;

import java.util.HashMap;
import java.util.Map;

import net.sci.array.numeric.IntArray3D;
import net.sci.axis.NumericalAxis;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.surface.Ellipsoid3D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.table.Table;

/**
 * Computation of equivalent ellipsoid for each region within a 3D label map.
 * 
 * @author dlegland
 *
 */
public class EquivalentEllipsoid3D extends RegionAnalyzer3D<Ellipsoid3D>
{
    // ==================================================
    // Static methods 
    
    /**
     * Computes equivalent ellipsoid of each region in the input 3D label image.
     * 
     * @param image
     *            the input label map containing region index of each voxel
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the calibration of the image
     * @return an array of Ellipsoid instances representing the calibrated
     *         coordinates of the equivalent ellipsoid of each region
     */
    public static final Ellipsoid3D[] equivalentEllipsoids(IntArray3D<?> image, int[] labels, Calibration calib)
    {
        return new EquivalentEllipsoid3D().analyzeRegions(image, labels, calib);
    }
    
    
    // ==================================================
    // Computation methods

    /**
     * Utility method that transforms the mapping between labels and equivalent
     * ellipsoid instances into a Table containing summary parameters of all
     * ellipsoids.
     * 
     * @param map
     *            the mapping between labels and Inertia Ellipses
     * @return a ResultsTable that can be displayed with ImageJ.
     */
    @Override
    public Table createTable(Map<Integer, Ellipsoid3D> map)
    {
        // Initialize a new result table
        String[] colNames = new String[] {
                "Label", 
                "Ellipsoid.CenterX", "Ellipsoid.CenterY", "Ellipsoid.CenterZ", 
                "Ellipsoid.Radius1", "Ellipsoid.Radius2", "Ellipsoid.Radius3", 
                "Ellipsoid.EulerAngleX", "Ellipsoid.EulerAngleY", "Ellipsoid.EulerAngleZ"};
        Table table = Table.create(map.size(), colNames);
        
        // convert the (key, value) pairs in the map into a table with one row
        // per label
        int row = 0;
        for (int label : map.keySet())
        {
            // current diameter
            Ellipsoid3D elli = map.get(label);
            
            // add an entry to the resulting data table
            table.setValue(row, 0, label);
            
            // coordinates of centroid
            Point3D center = elli.center();
            table.setValue(row, 1, center.x());
            table.setValue(row, 2, center.y());
            table.setValue(row, 3, center.z());
            
            // ellipse size
            double[] radList = elli.radiusList();
            table.setValue(row, 4, radList[0]);
            table.setValue(row, 5, radList[1]);
            table.setValue(row, 6, radList[2]);
    
            // ellipse orientation (degrees)
            double[] angles = elli.orientation().eulerAngles();
            table.setValue(row, 7, Math.toDegrees(angles[0]));
            table.setValue(row, 8, Math.toDegrees(angles[1]));
            table.setValue(row, 9, Math.toDegrees(angles[2]));
            
            row++;
        }
    
        return table;
    }

    /**
     * Computes equivalent ellipsoid of each region within the input label image.
     * 
     * @param array
     *            the input array containing label of regions
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the calibration of the image
     * @return an array of Ellipsoid3D representing the calibrated ellipsoids 
     */
    @Override
    public Ellipsoid3D[] analyzeRegions(IntArray3D<?> array, int[] labels, Calibration calib)
    {
        Moments3D[] moments = computeMoments(array, labels, calib);
        return momentsToEllipsoids(moments);
    }
    
    /**
     * Converts an array of 3D moments into equivalent ellipsoid representation.
     * 
     * @param moments
     *            the moments to convert
     * @return the array of ellipsoids corresponding to the moments
     */
    public Ellipsoid3D[] momentsToEllipsoids(Moments3D[] moments)
    {
        int n = moments.length;
        Ellipsoid3D[] ellipsoids = new Ellipsoid3D[n];

        // compute ellipsoid parameters for each region
        fireStatusChanged(this, "Ellipsoid: compute SVD");
        for (int i = 0; i < n; i++) 
        {
            this.fireProgressChanged(this, i, n);
            // create the new ellipsoid
            ellipsoids[i] = moments[i].equivalentEllipsoid();
        }
        fireProgressChanged(this, 1, 1);
        
        return ellipsoids;
    }

    /**
     * Computes the matrix of moments for each region within the 3D label map.
     * 
     * @param image
     *            the 3D image of labels (label map)
     * @param labels
     *            the array of region labels to process
     * @param calib
     *            the spatial calibration of the image
     * @return an array the same size as <code>labels</code>, containing for
     *         each processed region result of 3D Moments computations
     */
    public Moments3D[] computeMoments(IntArray3D<?> array, int[] labels, Calibration calib)
    {
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        // Extract spatial calibration
        double ox = 0.0, sx = 1.0;
        double oy = 0.0, sy = 1.0;
        double oz = 0.0, sz = 1.0;
        if (calib != null)
        {
            NumericalAxis xAxis = calib.getXAxis(); 
            ox = xAxis.getOrigin();
            sx = xAxis.getSpacing();
            NumericalAxis yAxis = calib.getYAxis(); 
            oy = yAxis.getOrigin();
            sy = yAxis.getSpacing();
            NumericalAxis zAxis = calib.getZAxis(); 
            oz = zAxis.getOrigin();
            sz = zAxis.getSpacing();
        }
        
        fireStatusChanged(this, "Ellipsoid: compute Moments");

        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        Moments3D[] moments = new Moments3D[nLabels]; 
        for (int i = 0; i < nLabels; i++)
        {
            moments[i] = new Moments3D();
        }

        // compute centroid of each region
        fireStatusChanged(this, "Ellipsoid: compute centroids");
        
        // compute centroid of each region
        for (int z = 0; z < sizeZ; z++) 
        {
            this.fireProgressChanged(this, z, sizeZ);
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
                    
                    // convert label to its index
                    int index = labelIndices.get(label);

                    // update sum coordinates, taking into account the spatial calibration
                    Moments3D moment = moments[index];
                    moment.cx += x * sx;
                    moment.cy += y * sy;
                    moment.cz += z * sz;
                    moment.count++;
                }
            }
        }
        
        // normalize by number of voxels in each region
        for (int i = 0; i < nLabels; i++)
        {
            if (moments[i].count > 0)
            {
                moments[i].cx /= moments[i].count;
                moments[i].cy /= moments[i].count;
                moments[i].cz /= moments[i].count;
            }
        }

        // compute matrix of centered moments for each region
        fireStatusChanged(this, "Ellipsoid: compute moment matrices");
        for (int z = 0; z < sizeZ; z++) 
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // get label of current region
                    int label = array.getInt(x, y, z);
                    if (label == 0 || !labelIndices.containsKey(label))
                        continue;

                    // convert label to its index
                    int index = labelIndices.get(label);
                    Moments3D moment = moments[index];

                    // convert coordinates relative to centroid 
                    double x2 = x * sx - moment.cx;
                    double y2 = y * sy - moment.cy;
                    double z2 = z * sz - moment.cz;

                    // update coefficients of matrix of moments
                    moment.Ixx += x2 * x2;
                    moment.Iyy += y2 * y2;
                    moment.Izz += z2 * z2;
                    moment.Ixy += x2 * y2;
                    moment.Ixz += x2 * z2;
                    moment.Iyz += y2 * z2;
                }
            }
        }
        
        // Normalize moments
        for (int i = 0; i < nLabels; i++)
        {
            if (moments[i].count == 0)
            {
                continue;
            }

            // normalize by number of voxels in each region
            moments[i].Ixx /= moments[i].count;
            moments[i].Iyy /= moments[i].count;
            moments[i].Izz /= moments[i].count;
            moments[i].Ixy /= moments[i].count;
            moments[i].Ixz /= moments[i].count;
            moments[i].Iyz /= moments[i].count;

            // Also adds the contribution of the central voxel to avoid zero
            // coefficients for regions with only one voxel
            moments[i].Ixx += sx * sx / 12.0;
            moments[i].Iyy += sy * sy / 12.0;
            moments[i].Izz += sz * sz / 12.0;

            // add coordinates of origin voxel
            // (use coordinate system starting in the center of first image voxel)
            moments[i].cx += ox;
            moments[i].cy += oy;
            moments[i].cz += oz;
        }

        return moments;
    }
    

    // ==================================================
    // Inner class for storing results
    
    /**
     * Encapsulates the results of 3D Moments computations. 
     */
    public class Moments3D
    {
        // the number of voxels 
        int count = 0;
        
        // The coordinates of the center
        double cx = 0;
        double cy = 0;
        double cz = 0;
        
        // the second-order coefficients
        double Ixx = 0;
        double Ixy = 0;
        double Ixz = 0;
        double Iyy = 0;
        double Iyz = 0;
        double Izz = 0;
        
        /**
         * Converts the 3D moments stored in this instance into a 3D ellipsoid.
         * 
         * @return the 3D ellipsoid with same inertia moments as the regions
         *         described by these moments.
         */
        public Ellipsoid3D equivalentEllipsoid()
        {
            // special case of one-voxel regions (and also empty regions)
            if (count <= 1)
            {
                double r1 = Math.sqrt(5 * Ixx);
                double r2 = Math.sqrt(5 * Iyy);
                double r3 = Math.sqrt(5 * Izz);
                // -> use default values (0,0,0) for angles
                return new Ellipsoid3D(this.cx, this.cy, this.cz, r1, r2, r3, 0.0, 0.0, 0.0);
            }
    
            // create the new ellipsoid
            Point3D center = new Point3D(this.cx, this.cy, this.cz);
            return Ellipsoid3D.fromInertiaCoefficients(center, Ixx, Iyy, Izz, Ixy, Ixz, Iyz);
        }
    }
}
