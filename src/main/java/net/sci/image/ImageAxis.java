/**
 * 
 */
package net.sci.image;

/**
 * Meta-data associated to an individual image axis: calibration, unit name...
 * 
 * @author dlegland
 *
 */
public interface ImageAxis
{
    // =============================================================
    // Enumerations

    enum Type 
    {
        SPACE, 
        CHANNEL, 
        TIME, 
        WAVELENGTH,
        UNKNOWN
    };
    
    // =============================================================
    // Static classes

    /**
     * Default axis implementation for X-axis.
     */
    public class X extends NumericalAxis
    {
        public X()
        {
            this(1.0, 0.0, "");
        }
        
        public X(double spacing, double origin)
        {
            this(spacing, origin, "");
        }
        
        public X(double spacing, double origin, String unitName)
        {
            super("X-Axis", Type.SPACE, spacing, origin, unitName);
        }
    }
    
    /**
     * Default axis implementation for Y-axis.
     */
    public class Y extends NumericalAxis
    {
        public Y()
        {
            this(1.0, 0.0, "");
        }
        
        public Y(double spacing, double origin)
        {
            this(spacing, origin, "");
        }
        
        public Y(double spacing, double origin, String unitName)
        {
            super("Y-Axis", Type.SPACE, spacing, origin, unitName);
        }
    }
    
    /**
     * Default axis implementation for Z-axis.
     */
    public class Z extends NumericalAxis
    {
        public Z()
        {
            this(1.0, 0.0, "");
        }
        
        public Z(double spacing, double origin)
        {
            this(spacing, origin, "");
        }

        public Z(double spacing, double origin, String unitName)
        {
            super("Z-Axis", Type.SPACE, spacing, origin, unitName);
        }
    }
    
    /**
     * Default axis implementation for channel axis.
     */
    public class C extends CategoricalAxis
    {
        public C()
        {
            super("Channel");
        }
        
        public Type getType()
        {
            return Type.CHANNEL;
        }
    }
    
    /**
     * Default axis implementation for time axis.
     */
    public class T extends NumericalAxis
    {
        public T()
        {
            this(1.0, 0.0, "");
        }
        
        public T(double spacing, double origin)
        {
            this(spacing, origin, "");
        }
        
        public T(double spacing, double origin, String unitName)
        {
            super("Time", Type.TIME, spacing, origin, unitName);
        }
    }
    
    // =============================================================
    // Method declarations

    /**
     * @return the name
     */
    public String getName();
    
    public default Type getType()
    {
        return Type.UNKNOWN;
    }
}
