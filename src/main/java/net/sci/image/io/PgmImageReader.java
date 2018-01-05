/**
 * 
 */
package net.sci.image.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Scanner;

import net.sci.array.data.scalar2d.IntArray2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.array.type.Int;
import net.sci.image.Image;

/**
 * @author David Legland
 *
 */
public class PgmImageReader implements ImageReader
{
    
    Reader reader;
    
    public PgmImageReader(File file) throws FileNotFoundException
    {
        this.reader = new BufferedReader(new FileReader(file));
    }
    
    public PgmImageReader(String fileName) throws FileNotFoundException
    {
        this.reader = new BufferedReader(new FileReader(fileName));
        ;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see jipl.io.ArrayReader#readArray()
     */
    @Override
    public Image readImage() throws FileNotFoundException
    {
        // open a buffered text reader on the file
        Scanner scanner = new Scanner(this.reader);
        
        String magicCode = readNextLine(scanner);
        if (!magicCode.equalsIgnoreCase("P2"))
            throw new RuntimeException(
                    "Magic code for PGM file should be P2, " + magicCode + " was found instead");
        
        // Read image dimension
        String line = readNextLine(scanner);
        Scanner lineScanner = new Scanner(line);
        String widthString = lineScanner.next();
        String heightString = lineScanner.next();
        lineScanner.close();
        
        String maxValString = readNextToken(scanner);
        
        // System.out.println("width = " + widthString);
        // System.out.println("height = " + heightString);
        // System.out.println("maxVal = " + maxValString);
        
        int width, height;
        width = Integer.parseInt(widthString);
        height = Integer.parseInt(heightString);
        Integer.parseInt(maxValString);
        
        IntArray2D<? extends Int> array = UInt8Array2D.create(width, height);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                array.setInt(x, y, scanner.nextInt());
            }
        }
        
        return new Image(array);
    }
    
    private String readNextLine(Scanner scanner)
    {
        while (true)
        {
            String line = scanner.nextLine();
            if (line.trim().isEmpty())
                continue;
            if (line.trim().startsWith("#"))
                continue;
            
            return line;
        }
    }
    
    private String readNextToken(Scanner scanner)
    {
        while (true)
        {
            String token = scanner.next();
            if (token.trim().isEmpty())
                continue;
            if (token.trim().startsWith("#"))
                continue;
            
            return token;
        }
    }
    
    // private String readNextLine(Reader reader) throws IOException {
    // Scanner scanner = new Scanner(new FileReader(file));
    // }
    
}
