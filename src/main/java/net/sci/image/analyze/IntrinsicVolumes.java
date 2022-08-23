/**
 * 
 */
package net.sci.image.analyze;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.image.Calibration;
import net.sci.image.analyze.region2d.BinaryConfigurationsHistogram2D;
import net.sci.image.analyze.region2d.IntrinsicVolumes2DUtils;
import net.sci.image.analyze.region3d.BinaryConfigurationsHistogram3D;
import net.sci.image.analyze.region3d.IntrinsicVolumes3DUtils;

/**
 * Collection of static methods for the computation of intrinsic volumes from
 * binary or label 2D/3D images.
 * 
 * For 2D images, intrinsic volumes correspond to area, perimeter (or boundary
 * length) and (2D) Euler number. For 3D images, they correspond to volume,
 * surface area, mean breadth, and (3D) Euler number.
 * 
 * For binary images, a binary array is expected as input, together with the
 * spatial calibration of the image. For label images, the list of region labels
 * within images should be specified as an array of integers.
 *
 * @see net.sci.image.analyze.region2d.IntrinsicVolumesAnalyzer2D
 * @see net.sci.image.analyze.region3d.IntrinsicVolumesAnalyzer3D
 * 
 * @author dlegland
 */
public class IntrinsicVolumes
{
    /**
     * Measures the area of the foreground region within a binary image.
     * 
     * @see #perimeter(BinaryArray2D, Calibration, int)
     * @see #eulerNumber2d(BinaryArray2D, int)
     * 
     * @param array
     *            the array containing binary image data
     * @param calib
     *            the spatial calibration of the image
     * @return the (calibrated) area of the foreground region
     */
    public static final double area(BinaryArray2D array, Calibration calib)
    {
        // area of individual pixel
        double pixelArea = calib.getXAxis().getSpacing() * calib.getYAxis().getSpacing();

        // count the number of foreground in each region
        long count = array.trueElementCount();
        double area = count * pixelArea;
        return area;
    }
    
    /**
     * Measures the perimeter of the foreground region within a binary image. 
     * 
     * @see #area(BinaryArray2D, Calibration)
     * @see #eulerNumber2d(BinaryArray2D, int)
     * 
     * @param array
     *            the array containing binary image data
     * @param calib
     *            the spatial calibration of the image
     * @param nDirs
     *            the number of directions to consider, either 2 or 4
     * @return the perimeter of the binary region within the image
     */
    public static final double perimeter(BinaryArray2D array, Calibration calib, int nDirs)
    {
        double[] lut = IntrinsicVolumes2DUtils.perimeterLut(calib, nDirs);
        int[] histo = new BinaryConfigurationsHistogram2D().process(array);
        return BinaryConfigurationsHistogram2D.applyLut(histo, lut);
    }
    
    /**
     * Measures the (2D) Euler number of the foreground region within the binary
     * image, using the specified connectivity.
     * 
     * @see #area(BinaryArray2D, Calibration)
     * @see #perimeter(BinaryArray2D, Calibration, int)
     * 
     * @param array
     *            the array containing binary image data
     * @param conn
     *            the connectivity to use (either 4 or 8)
     * @return the Euler number of the region within the binary image
     */
    public static final int eulerNumber2d(BinaryArray2D array, int conn)
    {
        double[] lut = IntrinsicVolumes2DUtils.eulerNumberLut(conn);
        int[] histo = new BinaryConfigurationsHistogram2D().process(array);
        return (int) BinaryConfigurationsHistogram2D.applyLut(histo, lut);
    }

    
    /**
     * Measures the volume of the foreground region within a 3D binary image.
     * 
     * @param array
     *            the array containing binary image data
     * @param calib
     *            the spatial calibration of the image
     * @return the volume of the region in the image
     */
    public final static double volume(BinaryArray3D array, Calibration calib)
    {
        // area of individual pixel
        double voxelVolume = calib.getXAxis().getSpacing() * calib.getYAxis().getSpacing() * calib.getZAxis().getSpacing();

        // count the number of pixels in foreground region
        long count = array.trueElementCount();
        double volume = count * voxelVolume;
        return volume;
        
    }
    
    /**
     * Measures the surface area the foreground region within a label image.
     * 
     * Uses discretization of the Crofton formula, that consists in computing
     * numbers of intersections with lines of various directions.
     * 
     * @param array
     *            the array containing binary image data
     * @param labels
     *            the set of labels in the image
     * @param calib
     *            the spatial calibration of the image
     * @param nDirs
     *            the number of directions to consider, either 3 or 13
     * @return the surface area of each region within the image
     */
    public static final double surfaceArea(BinaryArray3D array, Calibration calib, int nDirs)
    {
        // compute LUT corresponding to calibration and number of directions
        double[] lut = IntrinsicVolumes3DUtils.surfaceAreaLut(calib, nDirs);

        // Compute index of each 2x2x2 binary voxel configuration, associate LUT
        // contribution, and sum up
        int[] histo = new BinaryConfigurationsHistogram3D().process(array);
        return BinaryConfigurationsHistogram3D.applyLut(histo, lut);
    }
    
    /**
     * Measures the mean breadth of the foreground region within a 3D binary
     * image.
     * 
     * The mean breadth is proportional to the integral of mean curvature: mb =
     * 2*pi*IMC.
     * 
     * This implementation uses discretization of the Crofton formula, that
     * consists in computing Eluer number of intersections with planes of
     * various orientations.
     * 
     * 
     * @param array
     *            the array containing binary image data
     * @param calib
     *            the spatial calibration of the image
     * @param nDirs
     *            the number of directions to consider, either 3 or 13
     * @param conn2d
     *            the connectivity to use on planar sections with square tiles
     *            (either 4 or 8)
     * @return the mean breadth of the binary region within the image
     */
    public static final double meanBreadth(BinaryArray3D array, Calibration calib, int nDirs, int conn2d)
    {
        // compute LUT corresponding to calibration and number of directions
        double[] lut = IntrinsicVolumes3DUtils.meanBreadthLut(calib, nDirs, conn2d);

        // Compute index of each 2x2x2 binary voxel configuration, associate LUT
        // contribution, and sum up
        int[] histo = new BinaryConfigurationsHistogram3D().process(array);
        return BinaryConfigurationsHistogram3D.applyLut(histo, lut);
    }
    
    public static final int eulerNumber3d(BinaryArray3D array, int conn)
    {
        double[] lut = IntrinsicVolumes3DUtils.eulerNumberLut(conn);
        int[] histo = new BinaryConfigurationsHistogram3D().process(array);
        return (int) BinaryConfigurationsHistogram2D.applyLut(histo, lut);
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private IntrinsicVolumes()
    {
    }
}
