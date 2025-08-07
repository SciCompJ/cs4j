/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.Polygon2D;
import net.sci.geom.polygon2d.Polygons2D;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;

/**
 * Computes the convex hull of each region, in pixel coordinates.
 * 
 * The convex hull of each region is computed from a collection of points
 * located on its boundary. The boundary points are chosen on the middle of the
 * boundary edges of the region (edges between the region and either the
 * background or another region). In practice, the extraction of boundary point
 * is performed by the {@code BoundaryPixelEdgeMidPoints} feature.
 * 
 * @see BoundaryPixelEdgeMidPoints
 */
public class ConvexHull extends AlgoStub implements Feature
{
    /**
     * Default empty constructor.
     */
    public ConvexHull()
    {
    }
    
    @Override
    public Polygon2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        @SuppressWarnings("unchecked")
        ArrayList<Point2D>[] boundaryPoints = (ArrayList<Point2D>[]) data.results.get(BoundaryPixelEdgeMidPoints.class);
        
        return Arrays.stream(boundaryPoints)
                .map(Polygons2D::convexHull)
                .toArray(Polygon2D[]::new);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(BoundaryPixelEdgeMidPoints.class);
    }
}
