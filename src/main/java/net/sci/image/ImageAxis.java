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

        public X(String name, double spacing, double origin, String unitName)
        {
            super(name, Type.SPACE, spacing, origin, unitName);
        }

        @Override
        public X duplicate()
        {
            return new X(this.name, this.spacing, this.origin, this.unitName);
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
        
        public Y(String name, double spacing, double origin, String unitName)
        {
            super(name, Type.SPACE, spacing, origin, unitName);
        }

        @Override
        public Y duplicate()
        {
            return new Y(this.name, this.spacing, this.origin, this.unitName);
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

        public Z(String name, double spacing, double origin, String unitName)
        {
            super(name, Type.SPACE, spacing, origin, unitName);
        }

        @Override
        public Z duplicate()
        {
            return new Z(this.name, this.spacing, this.origin, this.unitName);
        }
    }
    
    /**
     * Default axis implementation for channel axis.
     */
    public class C extends CategoricalAxis
    {
        public C()
        {
            super("Channels", Type.CHANNEL, new String[] {"Value"});
        }
        
        public C(String[] channelNames)
        {
            super("Channels", Type.CHANNEL, channelNames);
        }

        @Override
        public C duplicate()
        {
            String[] names = new String[this.itemNames.length];
            for (int i = 0; i < names.length; i++)
            {
                names[i] = this.itemNames[i];
            }
            C axis = new C(names);
            axis.name = this.name;
            return axis;
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

        public T(String name, double spacing, double origin, String unitName)
        {
            super(name, Type.SPACE, spacing, origin, unitName);
        }

        @Override
        public T duplicate()
        {
            T axis = new T(this.spacing, this.origin, this.unitName);
            axis.name = this.name;
            return axis;
        }
    }
    
    // =============================================================
    // Method declarations

    /**
     * @return the name
     */
    public String getName();
    
    /**
     * @return the type of this axis
     */
    public Type getType();
    
    /**
     * @return a duplicated image axis
     */
    public ImageAxis duplicate();
}
