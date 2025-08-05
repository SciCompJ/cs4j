/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.OrientedBox2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.image.Calibration;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionTabularFeature;
import net.sci.image.regionfeatures.morpho2d.core.ConvexHull;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * The object-oriented bounding box of each region.
 */
public class OrientedBoundingBox extends AlgoStub implements RegionTabularFeature
{
    public static final String[] colNames = new String[] { "Oriented_Box_Center_X", "Oriented_Box_Center_Y", "Oriented_Box_Length", "Oriented_Box_Width", "Oriented_Box_Orientation" };

    @Override
    public OrientedBox2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        
        // retrieve spatial calibration of image
        Calibration calib = data.labelMap.getCalibration();
        
        // create affine transform that calibrates geometries
        AffineTransform2D transfo = createCalibrationTransform(calib);

        // Compute the oriented box of each set of corner points
        return Arrays.stream(hulls)
                .map(hull -> hull.transform(transfo))
                .map(hull -> OrientedBox2D.orientedBoundingBox(hull.vertexPositions()))
                .toArray(OrientedBox2D[]::new);
    }
    
    private AffineTransform2D createCalibrationTransform(Calibration calib)
    {
        if (calib == null || !calib.isCalibrated()) return AffineTransform2D.IDENTITY;
        
        AffineTransform2D tra = AffineTransform2D.createTranslation(calib.getXAxis().getOrigin(), calib.getYAxis().getOrigin());
        AffineTransform2D sca = AffineTransform2D.createScaling(calib.getXAxis().getSpacing(), calib.getYAxis().getSpacing());
        return tra.compose(sca);
    }

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof OrientedBox2D[] boxes)
        {
            // add new empty columns to table
            String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
            for (int i = 0; i < 4; i++)
            {
                NumericColumn col = NumericColumn.create(colNames[i], boxes.length);
                if (unitName != null && !unitName.isBlank())
                {
                    col.setUnitName(unitName);
                }
                table.addColumn(col);
            }
            NumericColumn orientCol = NumericColumn.create(colNames[4], boxes.length);
            orientCol.setUnitName("degree");
            table.addColumn(orientCol);
            
            for (int r = 0; r < boxes.length; r++)
            {
                OrientedBox2D obox = boxes[r];
                Point2D center = obox.center();
                table.setValue(r, colNames[0], center.x());
                table.setValue(r, colNames[1], center.y());
                table.setValue(r, colNames[2], obox.size1());
                table.setValue(r, colNames[3], obox.size2());
                table.setValue(r, colNames[4], obox.orientation());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of double");
        }
    }

    @Override
    public Collection<Class<? extends Feature>>requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }    
}
