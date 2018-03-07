package net.sci.image.io;

public class Validate
{
    private static final String UNSPECIFIED_PARAM_NAME = "method parameter";

    private Validate() {}

    // ============================================
    // Not null
    
    public static <T> T notNull(final T pParameter)
    {
        return notNull(pParameter, null);
    }
    
    public static <T> T notNull(final T pParameter, final String pParamName)
    {
        if (pParameter == null)
        {
            throw new IllegalArgumentException(String.format("%s may not be null",
                    pParamName == null ? UNSPECIFIED_PARAM_NAME : pParamName));
        }
        
        return pParameter;
    }
    
    // Is true
    
    public static boolean isTrue(final boolean pExpression, final String pMessage)
    {
        return isTrue(pExpression, pExpression, pMessage);
    }
    
    public static <T> T isTrue(final boolean condition, final T value, final String message)
    {
        if (!condition)
        {
            throw new IllegalArgumentException(
                    String.format(message == null ? "expression may not be %s" : message, value));
        }
        
        return value;
    }
}
