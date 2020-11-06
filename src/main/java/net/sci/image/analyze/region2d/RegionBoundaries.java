/**
 * 
 */
package net.sci.image.analyze.region2d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.sci.array.scalar.IntArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.label.LabelImages;

/**
 * Utility functions for computing position of boundary points/corners of
 * regions within binary or label images.
 * 
 * @author dlegland
 *
 */
public class RegionBoundaries
{
	/**
	 * Returns a set of points located at the corners of each region.
	 * Point coordinates are integer (ImageJ locates pixels in a [0 1]^d area.
	 * 
	 * @param array
	 *            a label image representing the particles
	 * @param labels
	 *            the list of labels to process
	 * @return a list of points that can be used for convex hull computation
	 */
	public final static Map<Integer, ArrayList<Point2D>> runLengthsCornersMap(IntArray2D<?> array, int[] labels)
	{
		int sizeX = array.size(0);
		int sizeY = array.size(1);
		
        // For each label, create a list of corner points
        Map<Integer, ArrayList<Point2D>> labelCornerPoints = new TreeMap<Integer, ArrayList<Point2D>>();
        for (int label : labels)
        {
        	labelCornerPoints.put(label, new ArrayList<Point2D>());
        }
		
		// for each row, add corner point for first and last pixel of each run-length
		for (int y = 0; y < sizeY; y++)
		{
			// start from background
			int previous = 0;

			// Identify transition inside and outside the each label
			for (int x = 0; x < sizeX; x++)
			{
				int current = array.getInt(x, y);

				// check if we have a transition 
				if (current != previous)
				{
					// if leave a region, add a new corner points for the end of the region
					if (previous > 0)
					{
						ArrayList<Point2D> corners = labelCornerPoints.get(previous);
						Point2D p = new Point2D(x, y);
						if (!corners.contains(p))
						{
							corners.add(p);
						}
						corners.add(new Point2D(x, y+1));
					}
					
					// transition into a new region
					if (current > 0)
					{
						// add a new corner points for the beginning of the new region
						ArrayList<Point2D> corners = labelCornerPoints.get(current);
						Point2D p = new Point2D(x, y);
						if (!corners.contains(p))
						{
							corners.add(p);
						}
						corners.add(new Point2D(x, y+1));
					}
					
					// update current label
					previous = current;
				}
			}
			
			// if particle touches right border, add another point
			if (previous > 0)
			{
				ArrayList<Point2D> corners = labelCornerPoints.get(previous);
				Point2D p = new Point2D(sizeX, y);
				if (!corners.contains(p))
				{
					corners.add(p);
				}
				corners.add(new Point2D(sizeX, y+1));
			}
		}
		
		return labelCornerPoints;
	}

	/**
	 * Returns a set of points located at the corners of a binary particle.
	 * Point coordinates are integer (ImageJ locates pixels in a [0 1]^d area.
	 * 
	 * @param array
	 *            a binary image representing the particle
	 * @param labels
	 *            the list of labels to process
	 * @return for each label, an array of points
	 */
	public final static ArrayList<Point2D>[] runlengthsCorners(IntArray2D<?> array, int[] labels)
	{
		// Compute corner points for each label
		Map<Integer, ArrayList<Point2D>> cornerPointsMap = runLengthsCornersMap(array, labels);
		
		// allocate array
		int nLabels = labels.length;
		@SuppressWarnings("unchecked")
		ArrayList<Point2D>[] labelCornerPoints = (ArrayList<Point2D>[]) new ArrayList<?>[nLabels];
		
		// convert map to array
		for (int i = 0; i < nLabels; i++)
		{
			labelCornerPoints[i] = cornerPointsMap.get(labels[i]);
		}		
		
		return labelCornerPoints;
	}

	/**
	 * Returns a set of boundary points from a binary image.
	 * 
	 * @param array
	 *            a binary image representing the particle
	 * @return a list of points that can be used for convex hull computation
	 */
	public final static ArrayList<Point2D> runLengthsBoundaryPixels(IntArray2D<?> array)
	{
		// size of input image
		int sizeX = array.size(0);
		int sizeY = array.size(1);
		
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		
		// try to find a pair of points for each row
		for (int y = 0; y < sizeY; y++)
		{
			// Identify transition inside and outside the particle 
			boolean inside = false;
			for (int x = 0; x < sizeX; x++)
			{
				if (array.getInt(x, y) > 0 && !inside)
				{
					// transition from background to foreground
					points.add(new Point2D(x, y));
					inside = true;
				} 
				else if (array.getInt(x, y) == 0 && inside)
				{
					// transition from foreground to background 
					points.add(new Point2D(x-1, y));
					inside = false;
				}
			}
			
			// if particle touches right border, add another point
			if (inside)
			{
				points.add(new Point2D(sizeX-1, y));
			}
		}
		
		return points;
	}
	
	/**
	 * Returns a set of points located at the corners of a binary particle.
	 * Point coordinates are integer (ImageJ locates pixels in a [0 1]^d area.
	 * 
	 * @param array
	 *            a binary image representing the particle
	 * @return a list of points that can be used for convex hull computation
	 */
	public final static ArrayList<Point2D> runLengthsCorners(IntArray2D<?> array)
	{
		int sizeX = array.size(0);
		int sizeY = array.size(1);
		
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		
		// try to find a pair of points for each row
		for (int y = 0; y < sizeY; y++)
		{
			// Identify transition inside and outside the particle 
			boolean inside = false;
			for (int x = 0; x < sizeX; x++)
			{
				int pixel = array.getInt(x, y);
				if (pixel > 0 && !inside)
				{
					// transition from background to foreground
					Point2D p = new Point2D(x, y);
					if (!points.contains(p))
					{
						points.add(p);
					}
					points.add(new Point2D(x, y+1));
					inside = true;
				} 
				else if (pixel == 0 && inside)
				{
					// transition from foreground to background 
					Point2D p = new Point2D(x, y);
					if (!points.contains(p))
					{
						points.add(p);
					}
					points.add(new Point2D(x, y+1));
					inside = false;
				}
			}
			
			// if particle touches right border, add another point
			if (inside)
			{
				Point2D p = new Point2D(sizeX, y);
				if (!points.contains(p))
				{
					points.add(p);
				}
				points.add(new Point2D(sizeX, y+1));
			}
		}
		
		return points;
	}

    public static final ArrayList<Point2D>[] boundaryPixelsMiddleEdges(IntArray2D<?> labelArray, int[] labels)
    {
        // size of image
        int sizeX = labelArray.size(0);
        int sizeY = labelArray.size(1);

        // create the map of labels
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        @SuppressWarnings("unchecked")
        ArrayList<Point2D>[] result = (ArrayList<Point2D>[]) new ArrayList[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            result[i] = new ArrayList<Point2D>();
        }   
               
        int[] configValues = new int[4];
        
        // iterate on image pixel configurations
        for (int y = 0; y < sizeY + 1; y++) 
        {
            configValues[2] = 0;
            
            for (int x = 0; x < sizeX + 1; x++) 
            {
                // update pixel values of configuration
                configValues[1] = x < sizeX & y > 0 ? labelArray.getInt(x, y - 1) : 0;
                configValues[3] = x < sizeX & y < sizeY ? labelArray.getInt(x, y) : 0;
                
                // check boundary with upper pixel
                if (configValues[1] != configValues[3])
                {
                    if (configValues[1] != 0)
                    {
                        int index = labelIndices.get(configValues[1]); 
                        result[index].add(new Point2D(x, y - .5));
                    }
                    if (configValues[3] != 0)
                    {
                        int index = labelIndices.get(configValues[3]); 
                        result[index].add(new Point2D(x, y - .5));
                    }
                }

                // check boundary with pixel on the left
                if (configValues[2] != configValues[3])
                {
                    if (configValues[2] != 0)
                    {
                        int index = labelIndices.get(configValues[2]); 
                        result[index].add(new Point2D(x - .5, y));
                    }
                    if (configValues[3] != 0)
                    {
                        int index = labelIndices.get(configValues[3]); 
                        result[index].add(new Point2D(x - .5, y));
                    }
                }

                // update values of configuration for next iteration
                configValues[2] = configValues[3];
            }
        }

        return result;
    }
    
	public static final ArrayList<Point2D> boundaryPixelsMiddleEdges(IntArray2D<?> binaryArray)
	{
		// size of image
		int sizeX = binaryArray.size(0);
		int sizeY = binaryArray.size(1);

		ArrayList<Point2D> points = new ArrayList<Point2D>();
		
		boolean[] configValues = new boolean[4];
		
		// iterate on image pixel configurations
		for (int y = 0; y < sizeY + 1; y++) 
		{
			configValues[2] = false;
			
			for (int x = 0; x < sizeX + 1; x++) 
			{
        		// update pixel values of configuration
				configValues[1] = x < sizeX & y > 0 ? binaryArray.getInt(x, y - 1) > 0 : false;
				configValues[3] = x < sizeX & y < sizeY ? binaryArray.getInt(x, y) > 0 : false;

				// check boundary with upper pixel
				if (configValues[1] != configValues[3])
				{
					points.add(new Point2D(x + .5, y));
				}
				if (configValues[2] != configValues[3])
				{
					points.add(new Point2D(x, y + .5));
				}

				// update values of configuration for next iteration
				configValues[2] = configValues[3];
			}
		}

		return points;
	}

	
    /**
     * Private constructor to prevent instantiations.
     */
    private RegionBoundaries()
    {
    }
    
}
