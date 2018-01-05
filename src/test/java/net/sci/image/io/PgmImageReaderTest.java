package net.sci.image.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.image.Image;

public class PgmImageReaderTest
{
    @Test
    public void testReadImage() throws FileNotFoundException
    {
        String fileName = getClass().getResource("/files/feep.pgm").getFile();
        PgmImageReader reader = new PgmImageReader(fileName);
        Image image = null;
        try
        {
            image = reader.readImage();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return;
        }
        
        assertNotNull(image);
        
        Array<?> data = image.getData();
        int[] dim = data.getSize();
        assertEquals(2, dim.length);
        assertEquals(24, dim[0]);
        assertEquals(7, dim[1]);
    }
    
}
