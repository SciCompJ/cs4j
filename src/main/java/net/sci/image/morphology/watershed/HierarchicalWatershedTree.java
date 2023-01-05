/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
            node.x = nextFreePos++;
            node.y = 0;
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
        
        String nodeString = String.format(Locale.ENGLISH, "(x=%5.2f, y=%5.2f) label = %2d", node.x, node.y, node.getRegion().label);
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
        /** The x-position of this node, between 0 and the total number of nodes */ 
        public double x;
        
        /**
         * The y-position of this node, corresponding to the "height" which is
         * computed from the dynamic values.
         */
        public double y;
        
        public abstract Region getRegion();
        
        public abstract double[] getChildrenRange();
        
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
            return new double[] {this.x, this.x};
        }
        
        @Override
        public double[] getFullRange()
        {
            return new double[] {this.x, this.x};
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
            this.x = (xRange[0] + xRange[1]) * 0.5;
            
            // uses a height equal to the minimum of the dynamic values obtained
            // from the basins.
            double yMin = Double.MAX_VALUE;
            for (Region region : mergeRegion.regions)
            {
                yMin = Math.min(yMin, region.dynamic);
            }
            this.y = yMin;
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
                xMin = Math.min(xMin, child.x);
                xMax = Math.max(xMax, child.x);
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
    
    private static class TreeDisplay extends JPanel
    {
        /**
         * To comply with AWT conventions
         */
        private static final long serialVersionUID = 1L;
        
        private static final int xShift = 50;
        private static final int yShift = 50;
        
        private static final int xScale = 50;
        private static final int yScale = 50;
        
        private static final int radius = 15;
        
        HierarchicalWatershedTree tree;
        
        public TreeDisplay(HierarchicalWatershedTree tree)
        {
            this.tree = tree;
        }
        public void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D) g;
            drawNodeTree(g2, tree.root);
        }
        
        public void drawNodeTree(Graphics2D g2, Node node)
        {
            double[] xRange = node.getChildrenRange();
            g2.setColor(Color.BLACK);
            double x0 = xRange[0] * xScale + xShift;
            double x1 = xRange[1] * xScale + xShift;
            double yNode = node.y * yScale + yShift; 
            g2.draw(new Line2D.Double(x0, yNode, x1, yNode));
            
            
            drawNodeLabel(g2, node);
            
            if (node instanceof MergeNode)
            {
                for (Node child : ((MergeNode) node).children)
                {
                    double xChild = child.x * xScale + xShift;
                    double yChild = child.y * yScale + yShift; 
                    g2.setColor(Color.BLACK);
                    g2.draw(new Line2D.Double(xChild, yNode, xChild, yChild));

                    drawNodeTree(g2, child);
                }
            }
        }
        
        private void drawNodeLabel(Graphics2D g2, Node node)
        {
            double x = node.x * xScale + xShift;
            double y = node.y * yScale + yShift;
            
            Shape oval = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            g2.setPaint(new Color(200, 200, 50));
            g2.fill(oval);
            g2.setColor(Color.BLACK);
            g2.draw(oval);
            
//            System.out.println("Draw node with region label = " + node.getRegion().label);
            g2.drawString(Integer.toString(node.getRegion().label), (float) (x-4), (float) (y+4));
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
        TreeDisplay mainPanel = new TreeDisplay(tree);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setPreferredSize(new Dimension(600,400));
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
