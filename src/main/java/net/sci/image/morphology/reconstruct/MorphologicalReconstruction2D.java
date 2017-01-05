/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import net.sci.array.data.scalar2d.ScalarArray2D;

/**
 * <p>
 * Defines the interface for morphological reconstructions algorithms applied to
 * planar arrays.
 * </p>
 * 
 * <p>
 * There are currently four implementation of morphological reconstruction for planar
 * images:
 * <ul>
 * <li>GeodesicReconstructionByDilation: implements reconstruction by dilation,
 * using scanning algorithm</li>
 * <li>GeodesicReconstructionByErosion: implements reconstruction by erosion,
 * using scanning algorithm</li>
 * <li>GeodesicReconstructionScanning: implements reconstruction by dilation or
 * erosion, using scanning algorithm.</li>
 * <li>GeodesicReconstructionHybrid: implements reconstruction by dilation or
 * erosion, using a classical forward pass, a backward pass that initialize a
 * processing queue, and processes each pixel in the queue until it is empty.</li>
 * </ul>
 * 
 * The most versatile one is the "Hybrid" version.
 * 
 * @author David Legland
 */
public interface MorphologicalReconstruction2D 
{
	/**
	 * Applies morphological reconstruction algorithm to the input marker and
	 * mask images.
	 * 
	 * @param marker
	 *            image used to initialize the reconstruction
	 * @param mask
	 *            image used to constrain the reconstruction
	 * @return the geodesic reconstruction of marker image constrained by mask
	 *         image
	 */
	public ScalarArray2D<?> process(ScalarArray2D<?> marker, ScalarArray2D<?> mask);
	
//	/**
//	 * Returns the chosen connectivity of the algorithm, either 4 or 8. 
//	 * 
//	 * @return the current connectivity for this algorithm
//	 */
//	public int getConnectivity();
//
//	/**
//	 * Changes the connectivity of the algorithm to either 4 or 8.
//	 * 
//	 * @param conn the connectivity to use, either 4 or 8
//	 */
//	public void setConnectivity(int conn);
}
