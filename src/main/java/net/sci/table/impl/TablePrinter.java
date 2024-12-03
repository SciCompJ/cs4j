/**
 * 
 */
package net.sci.table.impl;

import java.util.Arrays;
import java.util.NoSuchElementException;

import net.sci.axis.Axis;
import net.sci.table.CategoricalColumn;
import net.sci.table.Column;
import net.sci.table.IntegerColumn;
import net.sci.table.LogicalColumn;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * 
 */
public class TablePrinter
{

    int maxColumnWidth = 16;
    
//    int maxRowCount = 50;
    
    public String print(Table table)
    {
        Axis rowAxis = table.getRowAxis();
        int nRows = table.rowCount();
        int nCols = table.columnCount();
        String[] colNames = table.getColumnNames();
        String[] rowNames = table.getRowNames();
        
        // compute column widths
        int rowNamesSize = Math.min(maxLength(rowNames), maxColumnWidth);
        int[] columnWidths = determineColumnWidths(table);
        for (int i = 0; i < nCols; i++)
        {
            columnWidths[i] = Math.min(columnWidths[i], maxColumnWidth);
        }
        
        // initialize string builder
        StringBuilder sb = new StringBuilder();
        
        // First display column headers
        if (colNames != null)
        {
            // first line display the name of columns
            if (rowNames != null)
            {
                String rowNameHeader = rowAxis != null ? rowAxis.getName() : "";
                sb.append(formatString(rowNameHeader, rowNamesSize));
            }
            for (int c = 0; c < nCols; c++)
            {
                sb.append(formatString(colNames[c], columnWidths[c]));
            }
            sb.append("\n");
            
            // second line display separator
            if (rowNames != null)
            {
                repString(sb, "-", maxLength(rowNames));
                sb.append(" ");
            }
            for (int c = 0; c < nCols; c++)
            {
                repString(sb, "-", columnWidths[c]);
                sb.append(" ");
            }
            sb.append("\n");
        }

        // Then display content of each row
        for (int r = 0; r < nRows; r++)
        {
            // row header
            if (rowNames != null)
            {
                sb.append(formatString(rowNames[r], rowNamesSize));
            }
            
            // row data
            for (int c = 0; c < nCols; c++)
            {
                String str = table.column(c).getString(r);
                sb.append(formatString(str, columnWidths[c]));
            }
            sb.append("\n");
        }
        
        // display table size
        sb.append(String.format("%d rows x %d columns\n", nRows, nCols));

        return sb.toString();
    }
    
    private static final int maxLength(String... strings)
    {
        return strings == null ? 0 : Arrays.stream(strings)
                .map(str -> str.length())
                .mapToInt(v -> v)
                .max()
                .orElseThrow(NoSuchElementException::new);
    }
    
    private final static int[] determineColumnWidths(Table table)
    {
        return table.columns().stream()
                .map(col -> determineColumnWidth(col))
                .mapToInt(v -> v)
                .toArray();
    }
    
    private final static int determineColumnWidth(Column col)
    {
        int width = col.getName().length();
        if (col instanceof IntegerColumn)
        {
            int maxInt = Arrays.stream(((IntegerColumn) col).getIntValues()).max().orElse(0);
            width = Math.max(width, (int) Math.floor(Math.log10(maxInt)) + 1);
        }
        else if (col instanceof LogicalColumn)
        {
            width = Math.max(width, 5); // five digits for 'false'
        }
        else if (col instanceof NumericColumn)
        {
            width = Math.max(width, 6); // default format for floating-points, could be adapted
        }
        else if (col instanceof CategoricalColumn)
        {
            width = Math.max(width, maxLength(((CategoricalColumn) col).levelNames()));
        }
        else
        {
            System.err.println("could not determine best width for column with class: " + col.getClass().getName());
        }
        
        return width;
    }
    
    private final static String formatString(String str, int maxWidth)
    {
        if (str.length() > maxWidth)
        {
            str = str.substring(0, maxWidth-2) + "..";
        }
        return String.format("%" + maxWidth + "s ", str);
    }
    
    private final static StringBuilder repString(StringBuilder sb, String str, int n)
    {
        for (int i = 0; i < n; i++)
        {
            sb.append(str);
        }
        return sb;
    }
    
    public final static void main(String... args)
    {
        DefaultTable tbl = new DefaultTable(20, 3);
        tbl.setColumnNames(new String[] { "t", "sin(t)", "cos(t)"});
        for (int i = 0; i < 20; i++)
        {
            double t = i * Math.PI / 20;
            tbl.setValue(i, 0, t);
            tbl.setValue(i, 1, Math.sin(t));
            tbl.setValue(i, 2, Math.cos(t));
        }
        
        TablePrinter printer = new TablePrinter();
        System.out.println(printer.print(tbl));
    }
}
