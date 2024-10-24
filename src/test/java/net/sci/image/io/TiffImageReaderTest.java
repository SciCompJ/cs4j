package net.sci.image.io;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.numeric.impl.FileMappedUInt8Array3D;
import net.sci.axis.NumericalAxis;
import net.sci.image.Image;
import net.sci.image.ImageAxis;

public class TiffImageReaderTest
{
	@Test
	public void testReadImage_Gray8_2D() throws IOException
	{
		String fileName = getClass().getResource("/images/grains.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(256, data.size(0));
		assertEquals(256, data.size(1));
		
		assertEquals(193, data.getValue(150, 135), .1);
	}

    @Test
    public void testReadImage_Binary_2D_WheatGrain() throws IOException
    {
        String fileName = getClass().getResource("/images/binary/L_150_1_segXY_sub20.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());

        BinaryArray2D data = (BinaryArray2D) image.getData();
        assertEquals(308, data.size(0));
        assertEquals(247, data.size(1));
        
        assertFalse(data.getBoolean(100, 150));
        assertFalse(data.getBoolean(250, 100));
        assertTrue(data.getBoolean(250, 200));
    }

	@Test
	public void testReadImage_Float_2D_grains() throws IOException
	{
		String fileName = getClass().getResource("/images/grains_float.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(256, data.size(0));
		assertEquals(256, data.size(1));
		
		assertEquals(193, data.getValue(150, 135), .1);
	}

	/**
	 * Uses a test Array<?> that generate some unknown tags.
	 * @throws IOException
	 */
	@Test
	public void testReadImage_Cameraman() throws IOException
	{
		String fileName = getClass().getResource("/images/cameraman.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());

		ScalarArray2D<?> data = (ScalarArray2D<?>) image.getData();
		assertEquals(512, data.size(0));
		assertEquals(512, data.size(1));
	}

	@Test
	public void testReadImage_Gray8_3D_Stack() throws IOException
	{
		String fileName = getClass().getResource("/images/mri.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(3, image.getDimension());

		Array<?> data = image.getData();
		assertEquals(128, data.size(0));
		assertEquals(128, data.size(1));
		assertEquals( 27, data.size(2));
	}

	@Test
	public void testReadImage_RGB8_2D() throws IOException
	{
		String fileName = getClass().getResource("/images/lena_color_512.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		
		assertEquals(2, image.getDimension());
		assertEquals(512, image.getSize(0));
		assertEquals(512, image.getSize(1));
	}

	/**
	 * Read an image coded with uint16, and containing a LUT. 
	 * 
	 * The LUT is currently not tested.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadImage_UInt16_M51() throws IOException
	{
		String fileName = getClass().getResource("/images/m51.tif").getFile();
		
		TiffImageReader reader = new TiffImageReader(fileName);
		Image image = reader.readImage();
		assertEquals(2, image.getDimension());
		assertEquals(320, image.getSize(0));
		assertEquals(510, image.getSize(1));
		
		ScalarArray<?> array = (ScalarArray<?>) image.getData();
		assertEquals(218, array.getValue(new int[]{0, 0}), .1);
		assertEquals(275, array.getValue(new int[]{5, 0}), .1);
		assertEquals(10106, array.getValue(new int[]{80, 347}), .1);
		
		// try also with Array2D interface
		UInt16Array2D array2d = (UInt16Array2D) image.getData();
		assertEquals(218, array2d.getValue(0, 0), .1);
		assertEquals(275, array2d.getValue(5, 0), .1);
		assertEquals(10106, array2d.getValue(80, 347), .1);
	}
	
	/**
	 * Try to read an image containing RGB colors coded as 48 bits.
	 * 
	 * @throws IOException
	 */
    @Test
    public void testReadImage_RGB16_2D() throws IOException
    {
        // image size 324x238
        String fileName = getClass().getResource("/images/imagej/hela-cells-crop.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        assertEquals(324, image.getSize(0));
        assertEquals(238, image.getSize(1));
        
        RGB16Array2D data = (RGB16Array2D) image.getData();
        
        // pixel at position (0,0) has value (519, 414, 351)
        RGB16 rgb_0_0 = data.get(0, 0);
        assertEquals(519, rgb_0_0.getSample(0));
        assertEquals(414, rgb_0_0.getSample(1));
        assertEquals(351, rgb_0_0.getSample(2));
        
        // pixel at position (1,0) has value (495, 392, 362)
        RGB16 rgb_1_0 = data.get(1, 0);
        assertEquals(495, rgb_1_0.getSample(0));
        assertEquals(392, rgb_1_0.getSample(1));
        assertEquals(362, rgb_1_0.getSample(2));
        
        // pixel at position (140,140) has value (1182, 620, 1673)
        RGB16 rgb = data.get(140, 140);
        assertEquals(1182, rgb.getSample(0));
        assertEquals( 620, rgb.getSample(1));
        assertEquals(1673, rgb.getSample(2));
    }

    /**
     * Read a TIFF image containing spatial calibration info.
     * 
     * @throws IOException
     */
    @Test
    public void testReadImage_SpatialCalibration() throws IOException
    {
        // image size 324x238
        String fileName = getClass().getResource("/images/arabidopsis_embryo/16c_Col0_PFS_DAPI_015_C2_cropf_z054.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        assertEquals(349, image.getSize(0));
        assertEquals(372, image.getSize(1));

        ImageAxis[] axes = image.getCalibration().getAxes();
        assertEquals(2, axes.length);
        
        assertTrue(axes[0].type() == ImageAxis.Type.SPACE);
        assertTrue(axes[1].type() == ImageAxis.Type.SPACE);
    }

    /**
     * Read a TIFF image containing known spatial calibration info.
     * 
     * @throws IOException
     */
    @Test
    public void testReadImage_2D_SpatialCalibration() throws IOException
    {
        // image size 109x112
        String fileName = getClass().getResource("/images/arabidopsis_embryo/31c_Col0_762_crop_z061.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        assertEquals(109, image.getSize(0));
        assertEquals(112, image.getSize(1));

        ImageAxis[] axes = image.getCalibration().getAxes();
        assertEquals(2, axes.length);
        
        assertTrue(axes[0].type() == ImageAxis.Type.SPACE);
        assertTrue(axes[1].type() == ImageAxis.Type.SPACE);
        
        ImageAxis xAxis = axes[0];
        assertEquals(0.350, ((NumericalAxis) xAxis).getSpacing(), .001);
        ImageAxis yAxis = axes[1];
        assertEquals(0.350, ((NumericalAxis) yAxis).getSpacing(), .001);
    }
    
//    /**
//     * Read a 5D Tiff image as saved by ImageJ.
//     * 
//     * @throws IOException
//     */
//    @Test
//    public void testReadImage_5D_Mitosis() throws IOException
//    {
//        String fileName = getClass().getResource("/images/imagej/mitosis.tif").getFile();
//        
//        TiffImageReader reader = new TiffImageReader(fileName);
//        Image image = reader.readImage();
//        
//        Array<?> array = image.getData();
//        assertEquals(5, array.dimensionality());
//        
//        assertEquals(2, array.size(2));
//        assertEquals(5, array.size(3));
//        assertEquals(51, array.size(4));
//    }

    // The following code depends on local configuration. It is kept as comment intentionally.
//    /**
//     * Read a Tiff file larger than 4GB as saved by ImageJ.
//     * 
//     * @throws IOException
//     */
//    @Test
//    public void testReadImage_3D_MoreThan4GB() throws IOException
//    {
//        File file = new File("D:/images/wheat/perigrain/Psiche_2018/LR/gray8/images/L_200_1.tif");
//        System.out.println(file.exists());
//        
//        TiffImageReader reader = new TiffImageReader(file.getAbsolutePath());
//        Image image = reader.readImage();
//        
//        Array<?> array = image.getData();
//        assertEquals(3, array.dimensionality());
//    }

    /**
     * Read a 3D Tiff image as saved by ImageJ.
     * 
     * @throws IOException
     */
    @Test
    public void testReadImage_3D_Virtual_mri() throws IOException
    {
        String fileName = getClass().getResource("/images/imagej/mri_ij.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readVirtualImage3D();
        
        assertEquals(128, image.getSize(0));
        assertEquals(128, image.getSize(1));
        assertEquals(27, image.getSize(2));
        
        UInt8Array3D array = (UInt8Array3D ) image.getData();
        assertTrue(array instanceof FileMappedUInt8Array3D);
        
//        Image sliceImage = new Image(array.slice(13));
//        sliceImage.show();
    }
}
