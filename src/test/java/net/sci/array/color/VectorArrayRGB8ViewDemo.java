/**
 * 
 */
package net.sci.array.color;

import java.io.IOException;

import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.vector.VectorArray;
import net.sci.array.vector.VectorArray2D;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;


/**
 * @author dlegland
 *
 */
public class VectorArrayRGB8ViewDemo
{
    public static final void main(String[] args) throws IOException
    {
        String fileName = "maize/CTL1_crop64.tif";
        String filePath = VectorArrayRGB8ViewDemo.class.getResource("/files/" + fileName).getFile();
        
        TiffImageReader reader = new TiffImageReader(filePath);
        Image image = reader.readImage();
        
        ScalarArray3D<?> array = (ScalarArray3D<?>) image.getData();
        VectorArray<?,?> vectArray = VectorArray2D.fromStack(array);
        
        RGB8Array view = new VectorArrayRGB8View(vectArray, 1, 4, 7);
        
        int sizeX = view.size(0); 
        int sizeY = view.size(1); 
        RGB8Array2D rgb2d = RGB8Array2D.create(sizeX, sizeY);
        for (int[] pos : rgb2d.positions())
        {
            rgb2d.set(pos, view.get(pos));
        }
        
        Image img = new Image(rgb2d);
        img.show();
    }
}
