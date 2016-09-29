package net.sci.gui.table;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sci.table.DataTable;

/**
 * Display the content of a data table in a stage. This version implements a 
 * wrapper to a DataTable row as an implementation of "Record", and uses closure
 * to simplify (?) code.
 * 
 * @author dlegland
 *
 */
public class DataTableFrame3 extends Stage 
{
	private DataTable table;
	
	private Map<String, Integer> columnNameIndices;
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	public DataTableFrame3(DataTable table) 
	{
		this.table = table;
	
		Scene scene = new Scene(new VBox(), 400, 350);
		
		MenuBar menuBar = createMenuBar();
		TableView<Record> tableView = createTableView(table);
        ((VBox) scene.getRoot()).getChildren().addAll(menuBar, tableView);

        this.setTitle("Table Frame");
        this.setScene(scene);
        this.show();
	}

	private TableView<Record> createTableView(DataTable table)
	{
		// compute necessary inner data
		computeColumnNameIndices();
		
		// Create Table view with data converted to hash maps 
		ObservableList<Record> data = generateRecordList();
		TableView<Record> tableView = new TableView<Record>(data);
		
		// Define some columns to display the table
        TableColumn<Record, Number> column;
        for (String colName : table.getColumnNames())
        {
        	column = new TableColumn<Record, Number>(colName);
			column.setCellValueFactory((CellDataFeatures<Record, Number> p) -> 
			{
				// TableView row
				Record record = p.getValue();
				int colIndex = columnNameIndices.get(colName).intValue();
				return new SimpleDoubleProperty(record.getValue(colIndex));
			});
			
			column.setMinWidth(50);
			tableView.getColumns().add(column);
        }

		return tableView;
	}
	
	private void computeColumnNameIndices()
	{
		if (this.table == null)
		{
			throw new RuntimeException("Inner data table should have been initialized");
		}
		
		this.columnNameIndices = new HashMap<String, Integer>();
		for (int i = 0; i < this.table.getColumnNumber(); i++)
		{
			this.columnNameIndices.put(this.table.getColumnNames()[i], i);
		}
	}
	
//	private ObservableList<Map<String, Object>> generateDataInMap() 
//	{
//		ObservableList<Map<String, Object>> allData = FXCollections.observableArrayList();
//
//		String[] colNames = this.table.getColumnNames();
//
//		for (int r = 0; r < this.table.getRowNumber(); r++) 
//		{
//			Map<String, Object> dataRow = new HashMap<>();
//
//			for (int c = 0; c < this.table.getColumnNumber(); c++)
//			{
//				dataRow.put(colNames[c], this.table.getValue(r, c));            	
//			}
//
//			allData.add(dataRow);
//		}
//		return allData;
//    }
	
	private ObservableList<Record> generateRecordList() 
	{
		ObservableList<Record> allData = FXCollections.observableArrayList();
		
		for (int r = 0; r < this.table.getRowNumber(); r++) 
		{
			allData.add(new Record(r));
		}
		
		return allData;
	}
	
	private MenuBar createMenuBar()
	{
		MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem sayHelloItem = new MenuItem("Say Hello");
        sayHelloItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        menuFile.getItems().addAll(saveItem, sayHelloItem);
        
        Menu menuEdit = new Menu("Edit");
        
        MenuItem linePlotItem = new MenuItem("Line Plot");
        linePlotItem.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) 
        	{
        		showLinePlot(1);
        	}
        });
        MenuItem scatterPlotItem = new MenuItem("Scatter Plot");
//        scatterPlotItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                showScatterPlot(0, 1);
//            }
//        });
        scatterPlotItem.setOnAction(event -> showScatterPlot(0, 1));
        menuEdit.getItems().addAll(linePlotItem, scatterPlotItem);
        
        Menu menuView = new Menu("View");
        
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);

        return menuBar;
	}
	
	private void showLinePlot(int index) 
	{
		Stage stage = new Stage();
		double[] yValues = this.table.getColumnValues(index);
		double[] xValues = new double[yValues.length];
		for (int i = 0; i < yValues.length; i++)
		{
			xValues[i] = i + 1;
		}
		
		String xLabel = "";
		String yLabel = this.table.getColumnNames()[index];
		
        stage.setTitle("Line Plot");
        
        // defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        
        // creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis, yAxis);
                
        lineChart.setTitle(yLabel);
        // defining a series
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("");
        
        // populating the series with data
        for (int i = 0; i < yValues.length; i++)
        {
            series.getData().add(new XYChart.Data<Number,Number>(xValues[i], yValues[i]));
        }
        
        Scene scene  = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);
        lineChart.setLegendVisible(false);
       
        stage.setScene(scene);
        stage.show();
        
        //TODO: add a dependency between new stage and parent stage
	}

	private void showScatterPlot(int xIndex, int yIndex) 
	{
		Stage stage = new Stage();
		double[] xValues = this.table.getColumnValues(xIndex);
		double[] yValues = this.table.getColumnValues(yIndex);
		
		String xLabel = this.table.getColumnNames()[xIndex];
		String yLabel = this.table.getColumnNames()[yIndex];
		
        stage.setTitle("Scatter Plot");
        
        
        // determine min and max values for each axis
        double xMin = Double.POSITIVE_INFINITY; 
        double xMax = Double.NEGATIVE_INFINITY; 
        double yMin = Double.POSITIVE_INFINITY; 
        double yMax = Double.NEGATIVE_INFINITY; 
        for (int i = 0; i < xValues.length; i++)
        {
        	xMin = Math.min(xMin, xValues[i]);
        	xMax = Math.max(xMax, xValues[i]);
        	yMin = Math.min(yMin, yValues[i]);
        	yMax = Math.max(yMax, yValues[i]);
        }
        	
   		// defining the axes
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMin);
        xAxis.forceZeroInRangeProperty().set(false);
        xAxis.setLabel(xLabel);

        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        yAxis.setLabel(yLabel);

       
        
        // creating the chart
        final ScatterChart<Number,Number> lineChart = 
                new ScatterChart<Number,Number>(xAxis, yAxis);
                
        lineChart.setTitle(yLabel + " versus " + xLabel);
        // defining a series
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("");
        
        // populating the series with data
        for (int i = 0; i < yValues.length; i++)
        {
            series.getData().add(new XYChart.Data<Number,Number>(xValues[i], yValues[i]));
        }
        
        Scene scene  = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);
        lineChart.setLegendVisible(false);
        
        stage.setScene(scene);
        stage.show();
        
        //TODO: add a dependency between new stage and parent stage
	}
	
//	class TableItem 
//	{
//		int index;
//		
//		public TableItem(int index)
//		{
//			this.index = index;
//		}
//		
//		public double[] getValues()
//		{
//			double[] values = new double[table.getColumnNumber()];
//			for (int c = 0; c < table.getColumnNumber(); c++)
//			{
//				values[c] = table.getValue(this.index, c);
//			}
//			return values;
//		}
//	}
	
	/**
	 * A record in the original data table, corresponding to a row, or individual.
	 * The record is identified by its index. Each value is a Double.
	 */
	public class Record
	{
		int index;
	
		public Record(int index)
		{
			this.index = index;
		}
		
		/**
		 * Returns the value for a given column.
		 * @param col the column index, zero-based.
		 * @return
		 */
		public double getValue(int col)
		{
			return table.getValue(this.index, col);
		}
	}
	
	
	    
}
