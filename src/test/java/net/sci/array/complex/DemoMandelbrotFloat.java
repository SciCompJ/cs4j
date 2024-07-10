/**
 * 
 */
package net.sci.array.complex;

import java.io.File;
import java.io.IOException;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.io.ImageIOImageWriter;

/**
 * 
 */
public class DemoMandelbrotFloat
{
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
        System.out.println("hello...");
        System.out.println(new File(".").getAbsolutePath());
        
        double x0 = -1.5;
        double x1 = +0.5;
        double y0 = -1;
        double y1 = +1;
        
        int nX = 2000;
        int nY = 2000;
        
        int iterMax = 256;
        
        double stepX = (x1 - x0) / nX;
        double stepY = (y1 - y0) / nY;
        
        UInt8Array2D result = UInt8Array2D.create(nX, nY);
        
        long t0 = System.currentTimeMillis();
        
        for (int iy = 0; iy < nY; iy++)
        {
            System.out.println(iy);
            
            double y = y0 + iy * stepY;
            
            for (int ix = 0; ix < nX; ix++)
            {
                double x = x0 + ix * stepX;
                
//                Complex64 c0 = new Complex64(x, y);
//                Complex c = new Complex64();
                
                double cx = 0;
                double cy = 0;
                
                int iter;
                for (iter = 0; iter < iterMax; iter++)
                {
                    // zn = zn^2 + c
                    // et xn+1 = xn2 � yn2 + a ; yn+1 = 2xnyn + b
                    double tmp = cx * cx - cy * cy + x;
                    cy = 2 * cx * cy + y;
                    cx = tmp;
//                    c = c.times(c).plus(c0);
                    
                    
                    if (cx*cx + cy*cy > 4.0)
                    {
                        break;
                    }
                }
                
                result.setInt(ix,  iy, iter);
            }
        }
        long t1 = System.currentTimeMillis();
        double elapsedTime  = (t1 - t0) / 1000.0;
        System.out.println("Elapsed time: " + elapsedTime + " s");
        
        Image image = new Image(result);
        new ImageIOImageWriter(new File("mandelbrotFloat.png")).writeImage(image);
        
        System.out.println("success!");
    }
    
}
