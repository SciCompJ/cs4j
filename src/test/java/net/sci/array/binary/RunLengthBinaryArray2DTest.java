/**
 * 
 */
package net.sci.array.binary;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryArray2DTest
{

//    @Test
//    public final void test()
//    {
//        fail("Not yet implemented"); // TODO
//    }
    
    public final static void main(String... args)
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(8, 4);
        
        array.setBoolean(2, 1, true);
        System.out.println("init:");
        array.print(System.out);
        
        array.setBoolean(3, 1, true);
        System.out.println("add after:");
        array.print(System.out);
        
        array.setBoolean(1, 1, true);
        System.out.println("add before:");
        array.print(System.out);
        
        array.setBoolean(5, 1, true);
        System.out.println("add further:");
        array.print(System.out);
        
        array.setBoolean(4, 1, true);
        System.out.println("add between:");
        array.print(System.out);
        
        System.out.println("number of runs: " + array.rows.get(1).runs.size());
        
        
        
        array.setBoolean(5, 1, false);
        System.out.println("remove at the end:");
        array.print(System.out);
        
        array.setBoolean(1, 1, false);
        System.out.println("remove at start:");
        array.print(System.out);
        
        array.setBoolean(3, 1, false);
        System.out.println("remove in between:");
        array.print(System.out);
        
        System.out.println("number of runs: " + array.rows.get(1).runs.size());

        array.setBoolean(2, 1, false);
        System.out.println("remove single run:");
        array.print(System.out);
        
        System.out.println("number of runs: " + array.rows.get(1).runs.size());
    }
}
