/**
 * 
 */
package net.sci.image.binary.geoddist;

import java.io.File;
import java.io.IOException;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.binary.BinaryImages;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.io.ImageIOImageWriter;

/**
 * 
 */
public class CreateSpiralImage
{
    public static final void main(String... args) throws IOException
    {
        int sizeX = 512;
        int centerX = sizeX / 2 - 1;
        
        // create a square marker in the middle of the image
        BinaryArray2D marker = BinaryArray2D.create(sizeX, sizeX);
        marker.fill(true);
        marker.setBoolean(centerX, centerX, false);
        marker.setBoolean(centerX+1, centerX, false);
        marker.setBoolean(centerX, centerX+1, false);
        marker.setBoolean(centerX+1, centerX+1, false);
        
        // Create concentric squares
        ScalarArray<?> distMap = BinaryImages.distanceMap2d(marker, ChamferMask2D.CHESSBOARD, false, false);
        BinaryArray2D spiral = BinaryArray2D.create(sizeX, sizeX);
        spiral.fillBooleans(pos -> (distMap.getValue(pos) % 4) >= 2);
        
        for (int y = centerX; y < centerX + 2; y++)
        {
            for (int x = 0; x < centerX; x++)
            {
                spiral.setBoolean(x, y, false);
            }
        }
        
        for (int pos = 0; pos < centerX - 4; pos += 4)
        {
            if ((pos/4) % 2 > 0)
            {
                spiral.setBoolean(pos + 2, centerX - 2, true);
                spiral.setBoolean(pos + 3, centerX - 2, true);
                spiral.setBoolean(pos + 2, centerX - 1, true);
                spiral.setBoolean(pos + 3, centerX - 1, true);
            }
            else
            {
                spiral.setBoolean(pos + 2, centerX + 2, true);
                spiral.setBoolean(pos + 3, centerX + 2, true);
                spiral.setBoolean(pos + 2, centerX + 3, true);
                spiral.setBoolean(pos + 3, centerX + 3, true);
            }
        }
//        spiral.printContent(System.out);
        
        // convert to grayscale
        UInt8Array2D res8 = UInt8Array2D.create(sizeX, sizeX);
        res8.fillInts((x,y) -> spiral.getBoolean(x, y) ? 255 : 0);
        
        Image image = new Image(res8);
        ImageIOImageWriter writer = new ImageIOImageWriter(new File("spiral.png"));
        writer.writeImage(image);
    }
}
