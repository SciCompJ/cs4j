/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFrame;

import net.sci.array.scalar.UInt8Array1D;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Basin;
import net.sci.image.morphology.watershed.HierarchicalWatershed.MergeRegion;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Region;

/**
 * @author dlegland
 *
 */
public class HierarchicalWatershedTree
{
    public static final HierarchicalWatershedTree buildTree(Region region)
    {
        return new Builder().buildTree(region);
    }
    
    /**
     * Builds a tree from a region.
     */
    private static class Builder
    {
        int nextFreePos = 0;
        
        public HierarchicalWatershedTree buildTree(Region region)
        {
            return new HierarchicalWatershedTree(createNode(region));
        }
        
        private Node createNode(Region region)
        {
            if (region instanceof Basin)
            {
                return createBasinNode((Basin) region);
            }
            else if (region instanceof MergeRegion)
            {
                return createMergeNode((MergeRegion) region);
            }
            else
            {
                throw new RuntimeException("Unknown type of region!");
            }
        }
        
        private BasinNode createBasinNode(Basin basin)
        {
            BasinNode node = new BasinNode(basin);
            node.pos = nextFreePos++;
            node.height = 0;
            return node;
        }
        
        private MergeNode createMergeNode(MergeRegion mergeRegion)
        {
            ArrayList<Node> children = new ArrayList<>(mergeRegion.regions.size());
            for (Region region : mergeRegion.regions)
            {
                children.add(createNode(region));
            }
            return new MergeNode(mergeRegion, children);
        }
    }
    
    
    public Node root = null;
    
    private HierarchicalWatershedTree(Node rootNode)
    {
        this.root = rootNode;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        appendRegionTree(sb, this.root, 0);
        return sb.toString();
    }
    
    private static final void appendRegionTree(StringBuilder sb, Node node, int indentLevel)
    {
        if (indentLevel > 0)
        {
            sb.append(". ".repeat(indentLevel));
        }
        
        String nodeString = String.format(Locale.ENGLISH, "(x=%5.2f, y=%5.2f) label = %2d", node.pos, node.height, node.getRegion().label);
        sb.append("+ " + nodeString + "\n");
        if (node instanceof MergeNode)
        {
            for (Node child : ((MergeNode) node).children)
            {
                appendRegionTree(sb, child, indentLevel+1);
            }
        }
    }
        

    public abstract static class Node
    {
        /** The position of this node, between 0 and the total number of nodes */ 
        public double pos;
        
        /**
         * The "height" of this node, that is usually computed from the dynamic
         * values.
         */
        public double height;
        
        /**
         * @return the region associated to this node.
         */
        public abstract Region getRegion();
        
        /**
         * @return the range of positions of the children of this node, or a
         *         singleton range if this node is a leaf (Basin node).
         */
        public abstract double[] getChildrenRange();
        
        /**
         * @return the full range of positions of the children of this node
         *         (recursively computed), or a singleton range if this node is
         *         a leaf (Basin node).
         */
        public abstract double[] getFullRange();
    }

    
    public static class BasinNode extends Node
    {
        Basin basin;
        
        BasinNode(Basin basin)
        {
            this.basin = basin;
        }

        @Override
        public Region getRegion()
        {
            return basin;
        }

        @Override
        public double[] getChildrenRange()
        {
            return new double[] {this.pos, this.pos};
        }
        
        @Override
        public double[] getFullRange()
        {
            return new double[] {this.pos, this.pos};
        }
    }
    
    public static class MergeNode extends Node
    {
        MergeRegion mergeRegion;
        
        ArrayList<Node> children;
        
        MergeNode(MergeRegion mergeRegion, ArrayList<Node> children)
        {
            this.mergeRegion = mergeRegion;
            this.children = children;
            
            // x position is middle of range interval
            double[] xRange = getChildrenRange();
            this.pos = (xRange[0] + xRange[1]) * 0.5;
            
            // uses a height equal to the minimum of the dynamic values obtained
            // from the basins.
            double yMin = Double.MAX_VALUE;
            for (Region region : mergeRegion.regions)
            {
                yMin = Math.min(yMin, region.dynamic);
            }
            this.height = yMin;
        }

        @Override
        public Region getRegion()
        {
            return mergeRegion;
        }

        @Override
        public double[] getChildrenRange()
        {
            double xMin = Double.POSITIVE_INFINITY;
            double xMax = Double.NEGATIVE_INFINITY;
            for (Node child : children)
            {
                xMin = Math.min(xMin, child.pos);
                xMax = Math.max(xMax, child.pos);
            }
            return new double[] {xMin, xMax};        }
        
        @Override
        public double[] getFullRange()
        {
            double xMin = Double.POSITIVE_INFINITY;
            double xMax = Double.NEGATIVE_INFINITY;
            for (Node child : children)
            {
                double[] posRange = child.getFullRange();
                xMin = Math.min(xMin, posRange[0]);
                xMax = Math.max(xMax, posRange[1]);
            }
            return new double[] {xMin, xMax};
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // create a simple array with five minima
        // (located at odd position indices)
        int[] data = new int[] {6, 2, 4, 3, 8, 4, 6, 0, 4, 1, 7};
        UInt8Array1D array = UInt8Array1D.fromIntArray(data);
        
        System.out.println(array);
        
        HierarchicalWatershed1D algo = new HierarchicalWatershed1D();
        HierarchicalWatershed1D.WatershedGraph res = algo.computeResult(array);
        
        System.out.println(res.labelMap);
        
        System.out.println(res.saliencyMap);
        
//        System.out.println(res.root);
//        System.out.println(HierarchicalWatershed.printRegionTree(res.root));
        HierarchicalWatershedTree tree = HierarchicalWatershedTree.buildTree(res.root);
        
        System.out.println(tree);
        
        JFrame frame = new JFrame("Watershed Tree");
        HierarchicalWatershedTreeDisplayPanel mainPanel = new HierarchicalWatershedTreeDisplayPanel(tree);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setPreferredSize(new Dimension(600,400));
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
