/**
 * 
 */
package net.sci.image;

import java.util.Locale;

import net.sci.axis.NumericalAxis;

/**
 * Specialization of the NumericalAxis class for recurrent types of axes used
 * for images.
 * 
 * @author dlegland
 *
 */
public class ImageAxis extends NumericalAxis
{
    // =============================================================
    // Enumerations

    public enum Type 
    {
        SPACE, 
        CHANNEL, 
        TIME, 
        WAVELENGTH,
        UNKNOWN
    };
    
    protected Type type;
    

    // =============================================================
    // Constructors
    
    /**
     * Creates a new numerical axis
     * 
     * @param name the name of the numerical axis
     */
    public ImageAxis(String name)
    {
        this(name, 1.0, 0.0);
    }

    /**
     * Creates a new numerical axis
     * 
     * @param name
     *            the name of the numerical axis
     * @param spacing
     *            the spacing between each tick of the axis
     * @param origin
     *            the origin of the axis (position of the first tick)
     */
    public ImageAxis(String name, double spacing, double origin)
    {
        this(name, Type.UNKNOWN, spacing, origin, "");
    }

    /**
     * Constructor with initial value for all fields.
     * 
     * @param name
     *            the name of the axis
     * @param type
     *            the type of axis
     * @param spacing
     *            the spacing between two elements of the array
     * @param origin
     *            the position of the first element of the array
     * @param unitName
     *            the name of the unit
     */
    public ImageAxis(String name, Type type, double spacing, double origin, String unitName)
    {
        super(name, spacing, origin, unitName);
        this.type = type;
    }

    
    // =============================================================
    // New methods
    
    /**
     * @return the type of this axis
     */
    public Type type()
    {
        return type;
    }

    public boolean isCalibrated()
    {
        if (this.name != null) return true;
        if (this.spacing != 1.0) return true;
        if (this.origin != 0.0) return true;
        if (this.unitName != null) return true;
        return false;
    }
    
    
    // =============================================================
    // Specialize Axis interface methods
    
    @Override
    public ImageAxis duplicate()
    {
        return new ImageAxis(this.name, this.type, this.spacing, this.origin, this.unitName);
    }
    
    @Override
    public String toString()
    {
        String format = "ImageAxis(name=\"%s\", type=%s, spacing=%.4g, origin=%.4g, unit=\"%s\")";
        return String.format(Locale.ENGLISH, format, this.name, this.type, this.spacing, this.origin, this.unitName);
    }

    
    // =============================================================
    // static inner classes

    /**
     * Default axis implementation for X-axis.
     */
    public static class X extends ImageAxis
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
    public static class Y extends ImageAxis
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
    public static class Z extends ImageAxis
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
    
//    /**
//     * Default axis implementation for channel axis.
//     */
//    public class C extends CategoricalAxis
//    {
//        public C()
//        {
//            super("Channels", Type.CHANNEL, new String[] {"Value"});
//        }
//        
//        public C(String[] channelNames)
//        {
//            super("Channels", Type.CHANNEL, channelNames);
//        }
//
//        @Override
//        public C duplicate()
//        {
//            String[] names = new String[this.itemNames.length];
//            for (int i = 0; i < names.length; i++)
//            {
//                names[i] = this.itemNames[i];
//            }
//            C axis = new C(names);
//            axis.name = this.name;
//            return axis;
//        }
//    }
    
    /**
     * Default axis implementation for time axis.
     */
    public static class T extends ImageAxis
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
    
}
