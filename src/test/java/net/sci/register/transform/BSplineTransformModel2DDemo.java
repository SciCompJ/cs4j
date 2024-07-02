/**
 * 
 */
package net.sci.register.transform;

import java.io.File;
import java.io.IOException;

import net.sci.array.color.ColorMap;
import net.sci.array.color.ColorMaps;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;
import net.sci.register.image.TransformedImage2D;

/**
 * 
 */
public class BSplineTransformModel2DDemo
{
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
        System.out.println("hello...");
        
        // Read input images
        String fileName = BSplineTransformModel2DDemo.class.getResource("/images/cameraman.tif").getFile();
        File file1 = new File(fileName);
        System.out.println("file exists: " + file1.exists());
        TiffImageReader reader = new TiffImageReader(file1);
        Image image1 = reader.readImage();
        System.out.println("image1: " + image1.getSize(0) + " x " + image1.getSize(1));
        
        image1.show();
        
        int[] gridSize = new int[] {3, 3};
        double[] gridSpacing = new double[] {128, 128};
        Point2D origin = new Point2D(128, 128);
        BSplineTransformModel2D transfo = new BSplineTransformModel2D(gridSize, gridSpacing, origin);
        double[] params = new double[] {
                 60,  60,  -60,  60,    0,   0,  
                 60, -60,  -60, -60,   60, -60,  
                  0,   0,  -60, +60,  +60, +60,  
        };
        transfo.setParameters(params);
        
        ScalarArray2D<?> array1 = (ScalarArray2D<?>) image1.getData();
        TransformedImage2D tim = new TransformedImage2D(array1, transfo);
        
        UInt8Array2D array2 = UInt8Array2D.create(array1.size(0), array1.size(1));
        for (int[] pos : array2.positions())
        {
            array2.setValue(pos, tim.evaluate(pos[0], pos[1]));
        }
        
        Image image2 = new Image(array2, image1);
        image2.show();
        
        // Compute Map of Jacobian
        UInt8Array2D jacArray = UInt8Array2D.create(array1.size(0), array1.size(1));
        for (int[] pos : jacArray.positions())
        {
            double jac = transfo.detJacobian(new Point2D(pos[0], pos[1]));
            // convert to log, and rescale
            double jac2 = 64.0 * Math.log(jac) / Math.log(2.0) + 127.0;
            jacArray.setValue(pos, jac2);
        }
        
        Image image3 = new Image(jacArray, image1);
        ColorMap colormap = ColorMaps.BLUE_WHITE_RED.createColorMap(256);
        image3.getDisplaySettings().setColorMap(colormap);
        image3.show();
    }
}
