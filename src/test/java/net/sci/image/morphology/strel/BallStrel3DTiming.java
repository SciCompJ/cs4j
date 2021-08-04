/**
 * 
 */
package net.sci.image.morphology.strel;

import java.io.File;
import java.io.IOException;

import net.sci.array.scalar.UInt8Array3D;
import net.sci.table.Table;
import net.sci.table.io.DelimitedTableWriter;
import net.sci.table.io.TableWriter;

/**
 * @author dlegland
 *
 */
public class BallStrel3DTiming
{
    
    /**
     * @param args arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
        // Create a dummy 3D array
        UInt8Array3D array = UInt8Array3D.create(100, 100, 100);
        array.fillValues((x,y,z) -> {
            double h = Math.hypot(Math.hypot(z-50.0, y-50.0), x-50.0);
            return h > 40 ? 0.0 : Math.sqrt(30.0*30.0 - h*h);
        });
        
        int nRepet = 5;
        int rMax = 10;
        
        Table resNaive = Table.create(rMax, nRepet);
        Table resSliding = Table.create(rMax, nRepet);
        
        for (int iRepet = 0; iRepet < nRepet; iRepet++)
        {
            System.out.println("Repet: " + iRepet + " / " + nRepet);
            
            for (int r = 0; r < rMax; r++)
            {
                double radius = r + 1.0;
                
                Strel3D strelNaive = new NaiveBallStrel3D(radius);
                
                long t0n = System.nanoTime();
                strelNaive.dilation(array);
                long t1n = System.nanoTime();
                double dtn = (t1n - t0n) / 1_000_000_000.0;
                resNaive.setValue(r, iRepet, dtn);

                Strel3D strelSliding = new SlidingBallStrel3D(radius);
                
                long t0s = System.nanoTime();
                strelSliding.dilation(array);
                long t1s = System.nanoTime();
                double dts = (t1s - t0s) / 1_000_000_000.0;
                resSliding.setValue(r, iRepet, dts);
                
                System.out.println(String.format("  radius = %4.1f, naive = %8.3f s, sliding = %8.3f s", radius, dtn, dts));
                
            }
        }
        
        TableWriter writer = new DelimitedTableWriter();
        writer.writeTable(resNaive, new File("resNaive.csv"));
        writer.writeTable(resSliding, new File("resSliding.csv"));
    }
    
}
