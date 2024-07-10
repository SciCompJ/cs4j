/**
 * 
 */
package net.sci.image;

import java.io.IOException;

import net.sci.image.io.ImageIOImageReader;
import net.sci.image.io.ImageReader;


/**
 * @author dlegland
 *
 */
public class ImageShowDemo
{
    public static final void main(String[] args) throws IOException
    {
        String fileName = "sunflower2.png";
        String filePath = ImageShowDemo.class.getResource("/images/" + fileName).getFile();
        
        ImageReader reader = new ImageIOImageReader(filePath);
        Image image = reader.readImage();
        
        image.show();
    }
}
