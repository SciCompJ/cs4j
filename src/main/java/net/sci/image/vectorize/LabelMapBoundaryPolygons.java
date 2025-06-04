/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;
import java.util.Map;

import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.image.connectivity.Connectivity2D;

import java.util.HashMap;

/**
 * Computes boundary polygons of region(s) within a binary or label image by
 * tracking boundary pixel of the region label. Returns the result as a map that
 * associates each label to a collection of (simple) polygons.
 * 
 * @see Isocontour
 * @see BinaryImage2DBoundaryGraph
 * 
 * @author dlegland
 */
public class LabelMapBoundaryPolygons
{
    /**
     * The connectivity to use for tracking boundary. Should be either 4 or 8.
     * Default is 4.
     */
    Connectivity2D conn = Connectivity2D.C4;
    
    enum VertexLocation
    {
        /** Considers that boundary totally encloses the squares around the pixels. */
        CORNER,
        /**
         * Creates a boundary the joins the middle point of the edges between
         * boundary pixels and adjacent background pixels.
         */
        EDGE_CENTER,
        /** Creates a boundary that joins the centers of adjacent boundary pixels. */
        PIXEL;
    }
    
    VertexLocation vertex = VertexLocation.EDGE_CENTER;
    
    /**
     * The tracking directions.
     */
    enum Direction
    {
        RIGHT
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{1, 0}, {1, 1}};
            }
            
            @Override
            public Point2D getVertex(Position pos)
            {
                return new Point2D(pos.x - 0.5, pos.y + 0.5);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, UP);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x + 1, pos.y, RIGHT);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x + 1, pos.y + 1, DOWN);
            }

            @Override
            int directionMask()
            {
                return 0x01;
            }
        },
        
        UP
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{0, -1}, {1, -1}};
            }
            
            @Override
            public Point2D getVertex(Position pos)
            {
                return new Point2D(pos.x + 0.5, pos.y + 0.5);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, LEFT);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x, pos.y - 1, UP);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x + 1, pos.y - 1, RIGHT);
            }

            @Override
            int directionMask()
            {
                return 0x02;
            }
        },
        
        LEFT
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{-1, 0}, {-1, -1}};
            }
            
            @Override
            public Point2D getVertex(Position pos)
            {
                return new Point2D(pos.x + 0.5, pos.y - 0.5);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, DOWN);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x - 1, pos.y, LEFT);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x - 1, pos.y - 1, UP);
            }

            @Override
            int directionMask()
            {
                return 0x04;
            }
        },
        
        DOWN
        {
            @Override
            public int[][] coordsShifts()
            {
                return new int[][] {{0, +1}, {-1, 1}};
            }
            
            @Override
            public Point2D getVertex(Position pos)
            {
                return new Point2D(pos.x - 0.5, pos.y - 0.5);
            }
            
            @Override
            public Position turnLeft(Position pos)
            {
                return new Position(pos.x, pos.y, RIGHT);
            }

            @Override
            public Position forward(Position pos)
            {
                return new Position(pos.x, pos.y + 1, DOWN);
            }

            @Override
            public Position turnRight(Position pos)
            {
                return new Position(pos.x - 1, pos.y + 1, LEFT);
            }

            @Override
            int directionMask()
            {
                return 0x08;
            }
        };

        /**
         * Returns a 2-by-2 array corresponding to a pair of coordinates shifts,
         * that will be used to access coordinates of next pixels within
         * configuration.
         * 
         * The first coordinates will be the pixel in the continuation of the
         * current direction. The second coordinate will be the pixel in the
         * opposite current 2-by-2 configuration.
         * 
         * @return a 2-by-2 array corresponding to a pair of coordinates shifts.
         */
        public abstract int[][] coordsShifts();
        
        public abstract Point2D getVertex(Position pos);
        
        /**
         * Keeps current reference pixel and turns the direction by +90 degrees
         * in counter-clockwise direction.
         * 
         * @param pos
         *            the position to update
         * @return the new position
         */
        public abstract Position turnLeft(Position pos);

        /**
         * Updates the specified position by iterating by one step in the
         * current direction.
         * 
         * @param pos
         *            the position to update
         * @return the new position
         */
        public abstract Position forward(Position pos);
        
        /**
         * Keeps current reference pixel and turns the direction by -90 degrees
         * in counter-clockwise direction.
         * 
         * @param pos
         *            the position to update
         * @return the new position
         */
        public abstract Position turnRight(Position pos);

        /**
         * Returns an integer mask of boolean flags used to identify the
         * direction(s) a pixel was traveled through.
         * 
         * @return the integer mask of boolean flags.
         */
        abstract int directionMask();
    }
    
    /**
     * Identifies the position of the boundary tracker. The Position is composed
     * of the (x,y) coordinates of the current pixel, and of a direction of tracking.
     */
    static final class Position
    {
        /** The x-coordinate of this position's reference pixel. */
        int x;
        /** The y-coordinate of this position's reference pixel. */
        int y;
        /** The current travel direction along the boundary. */
        Direction direction;
        
        Position(int x, int y, Direction direction)
        {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
        
        public Point2D getVertex(Position pos)
        {
            return this.direction.getVertex(this);
        }
        
        /**
         * Computes the position of the current boundary vertex, based on the
         * position of the reference pixel, and on the type of vertex used to
         * build the boundary.
         * 
         * @param vertex
         *            the location of the boundary vertex with respect to the
         *            current pixel
         * @return a new boundary vertex position
         */
        public Point2D getVertex(VertexLocation vertex)
        {
            return switch (vertex)
            {
                case CORNER -> this.direction.getVertex(this);
                case EDGE_CENTER -> switch(direction)
                {
                    case DOWN -> new Point2D(this.x - 0.5, this.y);
                    case UP -> new Point2D(this.x + 0.5, this.y);
                    case LEFT -> new Point2D(this.x, this.y - 0.5);
                    case RIGHT -> new Point2D(this.x, this.y + 0.5);
                    default -> throw new IllegalArgumentException("Unexpected Direction enum value: " + direction);
                };
                case PIXEL-> new Point2D(this.x, this.y);
                default-> throw new IllegalArgumentException("Unexpected VertexLocation enum value: " + vertex);
            };
        }
        
        @Override
        public boolean equals(Object obj)
        {
            // check class
            if (!(obj instanceof Position))
                return false;
            Position that = (Position) obj;
            
            // check each class member
            if (this.x != that.x)
                return false;
            if (this.y != that.y)
                return false;
            if (this.direction != that.direction)
                return false;
            
            // return true when all tests checked
            return true;
        }
    }
    
    /**
     * Default empty constructor, using Connectivity 4.
     */
    public LabelMapBoundaryPolygons()
    {
    }
    
    /**
     * Constructor that allows to specify connectivity.
     * 
     * @param conn
     *            the connectivity to use (must be either 4 or 8)
     */
    public LabelMapBoundaryPolygons(Connectivity2D conn)
    {
        if (conn != Connectivity2D.C4 && conn != Connectivity2D.C8)
        { throw new IllegalArgumentException("Connectivity must be either C4 or C8"); }
        this.conn = conn;
    }
    
    /**
     * Tracks the boundary that starts at the current position by iterating on
     * successive neighbor positions, and returns the set of boundary points.
     * 
     * The positions are defined by two coordinates and a direction. The initial
     * position must correspond to a transition into a region, and the resulting
     * boundary will surround this region.
     * 
     * @param array
     *            the array containing binary or label representing the
     *            region(s)
     * @param x0
     *            the x-coordinate of the start position
     * @param y0
     *            the y-coordinate of the start position
     * @param initialDirection
     *            the direction of the start position
     * @return the list of points that form the boundary starting at specified
     *         position
     */
    public ArrayList<Point2D> trackBoundary(IntArray2D<?> array, int x0,
            int y0, Direction initialDirection)
    {
        // retrieve image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // initialize result array
        ArrayList<Point2D> vertices = new ArrayList<Point2D>();
        
        // initialize tracking algo state
        int value = array.getInt(x0, y0);
        Position pos0 = new Position(x0, y0, initialDirection);
        Position pos = new Position(x0, y0, initialDirection);
        
        // iterate over boundary until we come back at initial position
        do
        {
            vertices.add(pos.getVertex(vertex));
            
            // compute position of the two other points in current 2-by-2 configuration
            int[][] shifts = pos.direction.coordsShifts();
            // the pixel in the continuation of current direction
            int xn = pos.x + shifts[0][0];
            int yn = pos.y + shifts[0][1];
            // the pixel in the diagonal position within current configuration
            int xd = pos.x + shifts[1][0];
            int yd = pos.y + shifts[1][1];
            
            // determine configuration of the two pixels in current direction
            // initialize with false, to manage the case of configuration on the
            // border. In any cases, assume that reference pixel in current
            // position belongs to the array.
            boolean b0 = false;
            if (xn >= 0 && xn < sizeX && yn >= 0 && yn < sizeY)
            {
                b0 = array.getInt(xn, yn) == value;
            }
            boolean b1 = false;
            if (xd >= 0 && xd < sizeX && yd >= 0 && yd < sizeY)
            {
                b1 = array.getInt(xd, yd) == value;
            }
            
            // Depending on the values of the two other pixels in configuration,
            // update the current position
            if (!b0 && (!b1 || conn == Connectivity2D.C4))
            {
                // corner configuration -> +90 direction
                pos = pos.direction.turnLeft(pos);
            } 
            else if (b1 && (b0 || conn == Connectivity2D.C8))
            {
                // reentrant corner configuration -> -90 direction
                pos = pos.direction.turnRight(pos);
            } 
            else if (b0 && !b1)
            {
                // straight border configuration -> same direction
                pos = pos.direction.forward(pos);
            } 
            else
            {
                throw new RuntimeException("Should not reach this part...");
            }
        } while (!pos0.equals(pos));
        
        return vertices;
    }
    
    /**
     * Computes boundary polygons of region(s) within a binary or label image by
     * tracking boundary pixel of the region label. Returns the result as a map
     * that associates each label to a collection of (simple) polygons.
     *
     * @param array
     *            the array containing the labels of the regions to process. Can
     *            be a binary array or a label array (array containing integers)
     * @return a map that associates each label to a collection of (simple)
     *         polygons.
     */
    public Map<Integer, ArrayList<Polygon2D>> process(IntArray2D<?> array)
    {
        // retrieve image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create a mask that contains flag when pixel was visited according to
        // a direction
        UInt8Array2D maskArray = UInt8Array2D.create(sizeX, sizeY);
        
        // initialize result
        Map<Integer, ArrayList<Polygon2D>> boundaries = new HashMap<>();
        
        // iterate over all image pixels, and process only transition into a new
        // label (different from 0 and from the previous pixel)
        for (int y = 0; y < sizeY; y++)
        {
            // re-initialize current label to surround also regions that touch
            // image borders
            int currentLabel = 0;
            
            for (int x = 0; x < sizeX; x++)
            {
                int label = array.getInt(x, y);

                // first check if this is a transition between two labels
                if (label == currentLabel)
                {
                    continue;
                }
                currentLabel = label;
                
                // do not process background values
                if (label == 0)
                {
                    continue;
                }
                // if the boundary was already tracked, no need to work again
                if ((maskArray.getInt(x, y) & Direction.DOWN.directionMask()) > 0)
                {
                    continue;
                }
                
                // ok, we are at a transition that can be used to initialize a new boundary
                // -> track the boundary, and convert to polygon object
                ArrayList<Point2D> vertices = trackBoundary(array, maskArray, x, y, Direction.DOWN);
                Polygon2D poly = Polygon2D.create(vertices);
                
                // update map from labels to array of polygons
                ArrayList<Polygon2D> polygons = boundaries.get(label);
                if (polygons == null)
                {
                    polygons = new ArrayList<Polygon2D>(4);
                }
                polygons.add(poly);
                boundaries.put(label, polygons);
            }
        }
        
        return boundaries;
    }
    
    /**
     * Tracks the boundary that starts at the current position by iterating on
     * successive neighbor positions, and returns the set of boundary points.
     * 
     * The positions are defined by two coordinates and a direction. The initial
     * position must correspond to a transition into a region, and the resulting
     * boundary will surround this region.
     * 
     * @param array
     *            the array containing binary or label representing the
     *            region(s)
     * @param maskArray
     *            an array the same size as <code>array</code> containing a
     *            4-bits values that indicates which directions of current pixel
     *            have been visited.
     * @param x0
     *            the x-coordinate of the start position
     * @param y0
     *            the y-coordinate of the start position
     * @param initialDirection
     *            the direction of the start position
     * @return the list of points that form the boundary starting at specified
     *         position
     */
    private ArrayList<Point2D> trackBoundary(IntArray2D<?> array, UInt8Array2D maskArray, int x0,
            int y0, Direction initialDirection)
    {
        // retrieve image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // initialize result array
        ArrayList<Point2D> vertices = new ArrayList<Point2D>();
        
        // initialize tracking algo state
        int value = array.getInt(x0, y0);
        Position pos0 = new Position(x0, y0, initialDirection);
        Position pos = new Position(x0, y0, initialDirection);
        
        // iterate over boundary until we come back at initial position
        do
        {
            // update vertices
            vertices.add(pos.getVertex(vertex));
            
            // mark the current pixel with integer that depends on position
            int mask = maskArray.getInt(pos.x, pos.y) | pos.direction.directionMask();
            maskArray.setInt(pos.x, pos.y, mask);
            
            // compute position of the two other points in current 2-by-2 configuration
            int[][] shifts = pos.direction.coordsShifts();
            // the pixel in the continuation of current direction
            int xn = pos.x + shifts[0][0];
            int yn = pos.y + shifts[0][1];
            // the pixel in the diagonal position within current configuration
            int xd = pos.x + shifts[1][0];
            int yd = pos.y + shifts[1][1];
            
            // determine configuration of the two pixels in current direction
            // initialize with false, to manage the case of configuration on the
            // border. In any cases, assume that reference pixel in current
            // position belongs to the array.
            boolean b0 = false;
            if (xn >= 0 && xn < sizeX && yn >= 0 && yn < sizeY)
            {
                b0 = array.getInt(xn, yn) == value;
            }
            boolean b1 = false;
            if (xd >= 0 && xd < sizeX && yd >= 0 && yd < sizeY)
            {
                b1 = array.getInt(xd, yd) == value;
            }
            
            // Depending on the values of the two other pixels in configuration,
            // update the current position
            if (!b0 && (!b1 || conn == Connectivity2D.C4))
            {
                // corner configuration -> +90 direction
                pos = pos.direction.turnLeft(pos);
            } 
            else if (b1 && (b0 || conn == Connectivity2D.C8))
            {
                // reentrant corner configuration -> -90 direction
                pos = pos.direction.turnRight(pos);
            } 
            else if (b0 && !b1)
            {
                // straight border configuration -> same direction
                pos = pos.direction.forward(pos);
            } 
            else
            {
                throw new RuntimeException("Should not reach this part...");
            }
        } while (!pos0.equals(pos));
        
        return vertices;
    }
}
