/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Locale;

import javax.swing.JPanel;

import net.sci.image.morphology.watershed.HierarchicalWatershedTree.MergeNode;
import net.sci.image.morphology.watershed.HierarchicalWatershedTree.Node;

/**
 * @author dlegland
 *
 */
public class HierarchicalWatershedTreeDisplayPanel extends JPanel 
{
    /**
     * To comply with AWT conventions
     */
    private static final long serialVersionUID = 1L;
    
    int offsetX = 50;
    int offsetY = 50;
    
    int scaleX = 20;
    int scaleY = 20;
    
    private static final int radius = 10;
    
    private boolean drawLabelDisks = false;
    
    private boolean drawMergeNodeHeights = true;
    
    private boolean displaySideLabels = true;
    
    private boolean horizontalLayout = true;
    
    
    HierarchicalWatershedTree tree;
    
    public HierarchicalWatershedTreeDisplayPanel(HierarchicalWatershedTree tree)
    {
        this.tree = tree;
    }
    
    /**
     * @return the xOffset
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * @param xOffset the xOffset to set
     */
    public void setOffsetX(int xOffset)
    {
        this.offsetX = xOffset;
    }

    /**
     * @return the yOffset
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /**
     * @param yOffset the yOffset to set
     */
    public void setOffsetY(int yOffset)
    {
        this.offsetY = yOffset;
    }

    /**
     * @return the xScale
     */
    public int getScaleX()
    {
        return scaleX;
    }

    /**
     * @param xScale the xScale to set
     */
    public void setScaleX(int xScale)
    {
        this.scaleX = xScale;
    }

    /**
     * @return the yScale
     */
    public int getScaleY()
    {
        return scaleY;
    }

    /**
     * @param yScale the yScale to set
     */
    public void setScaleY(int yScale)
    {
        this.scaleY = yScale;
    }

    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        drawNodeTree(g2, tree.root);
    }
    
    public void drawNodeTree(Graphics2D g2, Node node)
    {
        if (node instanceof MergeNode)
        {
            drawMergeNodeBranches(g2, (MergeNode) node);
            for (Node child : ((MergeNode) node).children)
            {
                drawNodeTree(g2, child);
            }
            
            if (this.drawMergeNodeHeights)
            {
                drawMergeNodeHeight(g2, node);
            }
        }
        else
        {
            if (displaySideLabels)
            {
                drawSideNodeLabel(g2, node);
            }
        }
        
        if (drawLabelDisks)
        {
            drawNodeLabel(g2, node);
        }
    }
    
    private void drawMergeNodeBranches(Graphics2D g2, MergeNode node)
    {
        double[] posRange = node.getChildrenRange();
        double nodeHeight;
        
        g2.setColor(Color.BLACK);
        if (horizontalLayout)
        {
            double pos0 = posRange[0] * scaleY + offsetY;
            double pos1 = posRange[1] * scaleY + offsetY;
            nodeHeight = node.height * scaleX + offsetX; 
            g2.draw(new Line2D.Double(nodeHeight, pos0, nodeHeight, pos1));
        }
        else
        {
            double pos0 = posRange[0] * scaleX + offsetX;
            double pos1 = posRange[1] * scaleX + offsetX;
            nodeHeight = node.height * scaleY + offsetY; 
            g2.draw(new Line2D.Double(pos0, nodeHeight, pos1, nodeHeight));
        }
        
        for (Node child : ((MergeNode) node).children)
        {
            g2.setColor(Color.BLACK);
            
            if (horizontalLayout)
            {
                double childPos = child.pos * scaleY + offsetY;
                double childHeight = child.height * scaleX + offsetX;
                g2.draw(new Line2D.Double(nodeHeight, childPos, childHeight, childPos));
            }
            else
            {
                double childPos = child.pos * scaleX + offsetX;
                double childHeight = child.height * scaleY + offsetY;
                g2.draw(new Line2D.Double(childPos, nodeHeight, childPos, childHeight));
            }
        }

    }
    
    private void drawNodeLabel(Graphics2D g2, Node node)
    {
        double x, y;
        if (horizontalLayout)
        {
            x = node.height * scaleX + offsetX;
            y = node.pos * scaleY + offsetY;
        }
        else
        {
            x = node.pos * scaleX + offsetX;
            y = node.height * scaleY + offsetY;
        }
        
        Shape oval = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
        g2.setPaint(new Color(200, 200, 50));
        g2.fill(oval);
        g2.setColor(Color.BLACK);
        g2.draw(oval);
        
//        System.out.println("Draw node with region label = " + node.getRegion().label);
        g2.drawString(Integer.toString(node.getRegion().label), (float) (x-4), (float) (y+4));
    }
    
    private void drawMergeNodeHeight(Graphics2D g2, Node node)
    {
        double x, y;
        if (horizontalLayout)
        {
            x = node.height * scaleX + offsetX;
            y = node.pos * scaleY + offsetY;
        }
        else
        {
            x = node.pos * scaleX + offsetX;
            y = node.height * scaleY + offsetY;
        }
        
        g2.setColor(Color.BLACK);
//        System.out.println("Draw node with region label = " + node.getRegion().label);
        String str = String.format(Locale.ENGLISH, "%4.1f", node.height);
        g2.drawString(str, (float) (x+4), (float) (y-4));
    }
    
    
    private void drawSideNodeLabel(Graphics2D g2, Node node)
    {
        String str = Integer.toString(node.getRegion().label);
        g2.setColor(Color.BLACK);
        
        if (horizontalLayout)
        {
            double y = node.pos * scaleY + offsetY;
            g2.drawString(str, (float) (offsetX - 16), (float) (y+4));
        }
        else
        {
            double x = node.pos * scaleX + offsetX;
            g2.drawString(str, (float) (x-4), (float) (1 * scaleY + offsetY));
        }
    }

}
