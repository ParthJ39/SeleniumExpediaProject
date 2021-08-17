package operations;

import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadWriteExcelFile {
	Workbook wb;
	public ReadWriteExcelFile(String pathWithFileName ) {
		try {
	if(pathWithFileName.endsWith(".xls")) {
		 wb = new HSSFWorkbook(new FileInputStream(pathWithFileName));
	}
	else if(pathWithFileName.endsWith(".xlsx")){
		 wb = new XSSFWorkbook(new FileInputStream(pathWithFileName));
	
	}
		}
		catch(Exception E) {
			System.out.println(E.getMessage());
		} 
	}
		public String readData(String sheetname,int row,int col )
		{
			String data=wb.getSheet(sheetname).getRow(row).getCell(col).toString();
			return data;
		}
		public int getLastRowNum(String sheetName) {
			return wb.getSheet(sheetName).getLastRowNum();
		}
		public  Workbook getWorkbook() {
			return wb;
		}
		public void setCellValue(String sheetName, int rowNum, int colNum, String value) throws IllegalArgumentException {
		    
		    if (rowNum <= 0 || colNum <= 0) throw new IllegalArgumentException("Both \"rowNum\" and \"colNum\" must be greater then zero.");
		    --rowNum;
		    --colNum;
		    Sheet sheet = wb.getSheet(sheetName);
		    if (sheet == null) throw new IllegalArgumentException("There is no sheet called \"" + sheetName + "\".");
		    Row row = sheet.getRow(rowNum);
		    if (row == null) row = sheet.createRow(rowNum);
		    Cell cell = row.getCell(colNum);
		    if (cell != null) {
		        if (cell.getCellType() != CellType.STRING && cell.getCellType() != CellType.BLANK) 
		            throw new IllegalArgumentException("Cannot set String value in a Cell whose type is \"" + cell.getCellType() + "\".");
		    } else {
		        cell = row.createCell(rowNum);
		    }
		    
		    cell.setCellValue(value);
		    System.out.println(rowNum+" "+colNum+" "+value);
		}
	

}
