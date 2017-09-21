package application.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public final class ExcelHandler {
	
	public static void write(String filePath, List<Object[]> data){
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        
        int rowNum = 0;
        int colNum = 0;
        if(Main.DEBUG)
    		System.out.println("Creating excel:");
        
        XSSFCellStyle styleCell = (XSSFCellStyle) workbook.createCellStyle();
        styleCell.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        
        for (Object[] datatype : data) {
            Row row = sheet.createRow(rowNum++);
            colNum = 0;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                cell.setCellStyle(styleCell);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }
       
        //auto size columns:
        for(int i=0; i<colNum; i++)
        	sheet.autoSizeColumn(i);
        //auto size rows:
        short lineHeight = 255;
        for(int i=1; i<rowNum; i++){
        	String line = (String)(data.get(i)[1]);
        	short nLines = (short)(1+line.length() - line.replace("\n", "").length());
        	sheet.getRow(i).setHeight((short)(lineHeight*nLines));
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            if(Main.DEBUG)
        		System.out.println("Excell Creation Success :)");
            return;
        } catch (Exception e) {
        	if(Main.DEBUG)
        		e.printStackTrace();
			Platform.runLater(new Runnable() {
				public void run() {
		    		Alert alert = new Alert(AlertType.NONE, "Erro ao criar XML, feche o arquivo de resultados caso esteja aberto." , ButtonType.OK);
		    		alert.showAndWait();
				}
			});
        }
        if(Main.DEBUG)
        	System.out.println("Excell Creation Failed :(");
	}
	
	public static List<String> read(String fPath){
    	List<String> result = new ArrayList<String>();
        try {
            FileInputStream excelFile = new FileInputStream(new File(fPath));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            if(Main.DEBUG)
        		System.out.println("PLACAS:");
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    String value = currentCell.getStringCellValue();
                    if(value.contains("PLACA"))
                    	break;
                    //if(Main.DEBUG)
    	        		//System.out.println(value);
                    result.add(value.toUpperCase().trim());
                    break; //just get first column!
                }
            }
        } catch (Exception e) {
        	if(Main.DEBUG)
        		e.printStackTrace();
			Platform.runLater(new Runnable() {
				public void run() {
		    		Alert alert = new Alert(AlertType.NONE, "Erro ao ler XML" , ButtonType.OK);
		    		alert.showAndWait();
				}
			});
        } 
        return result;
	}
}
