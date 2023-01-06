/**
 * 
 */
package net.sci.image.morphology.watershed;

import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.io.TiffImageReader;

/**
 * @author dlegland
 *
 */
public class HierarchicalWatershed2DDemo_AppleCells05
{

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
        String fileName = HierarchicalWatershed2DDemo_AppleCells05.class.getResource("/images/plant_tissues/appleCells_crop_smooth_sub05.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        assertEquals(2, image.getDimension());
        UInt8Array2D array = UInt8Array2D.wrap(UInt8Array.wrap((ScalarArray<?>) image.getData()));

        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
//        ConsoleAlgoListener.monitor(algo);
        
        
//        ScalarArray2D<?> res = algo.process(array);
        HierarchicalWatershed2D.WatershedGraph2D res = algo.computeResult(array);
        
//        UInt8Array2D res8 = UInt8Array2D.wrap(UInt8Array.wrap(res.saliencyMap));
//        Image resultImage = new Image(res8, image);
//        resultImage.show();
        
        HierarchicalWatershedTree tree = HierarchicalWatershedTree.buildTree(res.root);
        
//        System.out.println(tree);
        
        JFrame frame = new JFrame("Watershed Tree");
        HierarchicalWatershedTreeDisplayPanel mainPanel = new HierarchicalWatershedTreeDisplayPanel(tree);
        mainPanel.setScaleX(4);
        mainPanel.setScaleY(30);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setPreferredSize(new Dimension(600,400));
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

}
