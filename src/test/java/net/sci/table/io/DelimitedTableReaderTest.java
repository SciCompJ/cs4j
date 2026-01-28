package net.sci.table.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sci.table.Table;

import org.junit.Test;

public class DelimitedTableReaderTest
{
    @Test
    public final void test_readTable_Iris() throws IOException
    {
        String fileName = getClass().getResource("/tables/iris/fisherIris.txt").getFile();
        File file = new File(fileName);

        TableReader reader = new DelimitedTableReader();

        Table table = reader.readTable(file);

        assertEquals(150, table.rowCount());
        assertEquals(5, table.columnCount());
    }

    @Test
    public final void test_readTable_IrisData() throws IOException
    {
        String fileName = getClass().getResource("/tables/iris/iris.data").getFile();
        File file = new File(fileName);

        DelimitedTableReader reader = new DelimitedTableReader()
                .setDelimiters(",")
                .setReadHeader(false)
                .setReadRowNames(false);

        Table table = reader.readTable(file);

        assertEquals(150, table.rowCount());
        assertEquals(5, table.columnCount());
    }

    @Test
    public final void test_readTable_InputStream_IrisData() throws IOException
    {
        String filePath = "tables/iris/iris.data";
        InputStream stream = getClass().getClassLoader().getResourceAsStream(filePath);

        DelimitedTableReader reader = new DelimitedTableReader()
                .setDelimiters(",")
                .setReadHeader(false)
                .setReadRowNames(false);

        Table table = reader.readTable(stream);

        assertEquals(150, table.rowCount());
        assertEquals(5, table.columnCount());
    }
    
    @Test
    public final void test_splitString_emptyFirstToken()
    {
        String string = ",adress";
        String delimiterRegexp = "[,]";

        String[] tokens = string.split(delimiterRegexp, 2);
        
        assertEquals(2, tokens.length);
        assertEquals("adress", tokens[1]);
    }
    
    /**
     * Test method for {@link net.sci.table.io.DelimitedTableReader#splitQuotedTokens(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void test_splitQuotedTokens_simple()
    {
        String string = "name,age,adress";
        
        String[] tokens = DelimitedTableReader.splitQuotedTokens(string, ",");
        
        assertEquals(3, tokens.length);
    }
    
    /**
     * Test method for {@link net.sci.table.io.DelimitedTableReader#splitQuotedTokens(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void test_splitQuotedTokens_quoted()
    {
        String string = "name,\"age\",adress";
        
        String[] tokens = DelimitedTableReader.splitQuotedTokens(string, ",");
        
        assertEquals(3, tokens.length);
    }
    
    /**
     * Test method for {@link net.sci.table.io.DelimitedTableReader#splitQuotedTokens(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void test_splitQuotedTokens_allQuoted()
    {
        String string = "\"name\",\"age\",\"adress\"";
        
        String[] tokens = DelimitedTableReader.splitQuotedTokens(string, ",");
        
        assertEquals(3, tokens.length);
        assertEquals("name", tokens[0]);
        assertEquals("age", tokens[1]);
        assertEquals("adress", tokens[2]);
    }
    
    /**
     * Test method for {@link net.sci.table.io.DelimitedTableReader#splitQuotedTokens(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void test_splitQuotedTokens_quotedWithDelimiter()
    {
        String string = "name,\"my,age\",adress";
        
        String[] tokens = DelimitedTableReader.splitQuotedTokens(string, ",");
        
        assertEquals(3, tokens.length);
        assertEquals("name", tokens[0]);
        assertEquals("my,age", tokens[1]);
        assertEquals("adress", tokens[2]);
    }
    
    /**
     * Test method for {@link net.sci.table.io.DelimitedTableReader#splitQuotedTokens(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void test_splitQuotedTokens_quotedWithEscaped()
    {
        String string = "name,\"new\"\"age\"\"\",adress";
        
        String[] tokens = DelimitedTableReader.splitQuotedTokens(string, ",");
        
        assertEquals(3, tokens.length);
        assertEquals("name", tokens[0]);
        assertEquals("new\"\"age\"\"", tokens[1]);
        assertEquals("adress", tokens[2]);
    }
}
