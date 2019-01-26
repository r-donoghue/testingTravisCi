package loadDataFromExcelToDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

public class LoadData {
	private static Connection con = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;
	private static String sql;

	public static void main(String[] args) throws Exception {

		final long startTime = System.currentTimeMillis();
		
		connectToDatabase();
		List<XSSFSheet> excelFileData = loadDataFromFile("data.xlsx");
		XSSFSheet marketOperatorData = excelFileData.get(4);
		XSSFSheet tacData = excelFileData.get(3);
		XSSFSheet failureClassData = excelFileData.get(2);
		XSSFSheet eventCauseData = excelFileData.get(1);
		XSSFSheet callFailureData = excelFileData.get(0);
		
		Thread loadMarketOperatorsThread = new loadMarketOperatorDataThread(marketOperatorData);
		
		run(tacData);
		
		final long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	public static boolean validateValidDataType(Cell cell, CellType cellType) {
		if(cell.getCellTypeEnum().equals(cellType)) {
			return true;
		}
		return false;
	}
	public static void run(XSSFSheet sheetToOperateOn) {
		int tac;
		String marketingName;
		String country;
		String operator;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/lte_network_failures";
			Connection con = DriverManager.getConnection(url, "root", "admin");
			
			PreparedStatement ps = con.prepareStatement("INSERT into tac(ue_type,marketing_name,manufacturer,access_capability) values(?,?,?,?);");
			for(Row row: sheetToOperateOn) {
				if(row.getRowNum() != 0 && row.getCell(0) != null) {
					if(validateValidDataType(row.getCell(0),CellType.STRING)) {
						
					}
					tac = (int) row.getCell(0).getNumericCellValue();
					marketingName = row.getCell(1).getStringCellValue();
					country = row.getCell(2).getStringCellValue();
					operator = row.getCell(3).getStringCellValue();
					
					ps.setInt(1, tac);
					ps.setString(2, marketingName);
					ps.setString(3, country);
					ps.setString(4, operator);
					
					ps.execute();
				}
				
					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void connectToDatabase() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/lte_network_failures";
			con = DriverManager.getConnection(url, "root", "admin");
			stmt = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<XSSFSheet> loadDataFromFile(String fileName) throws IOException {
		try {
			FileInputStream myInput = new FileInputStream(fileName);
			XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);
			List<XSSFSheet> sheets = new ArrayList<XSSFSheet>(5);
			myWorkBook.createCellStyle().setDataFormat(999999999);
			sheets.add(myWorkBook.getSheetAt(0));
			sheets.add(myWorkBook.getSheetAt(1));
			sheets.add(myWorkBook.getSheetAt(2));
			sheets.add(myWorkBook.getSheetAt(3));
			sheets.add(myWorkBook.getSheetAt(4));
			myWorkBook.close();
			return sheets;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<XSSFSheet>(0);

	}
}


