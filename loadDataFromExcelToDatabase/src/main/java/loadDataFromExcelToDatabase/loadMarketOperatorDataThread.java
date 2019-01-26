package loadDataFromExcelToDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class loadMarketOperatorDataThread extends Thread{
	XSSFSheet sheetToOperateOn;
	public loadMarketOperatorDataThread(XSSFSheet sheetToOperateOn){
		this.sheetToOperateOn = sheetToOperateOn;
	}
	public void run() {
		int mcc;
		int mnc;
		String country;
		String operator;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/lte_network_failures";
			Connection con = DriverManager.getConnection(url, "root", "admin");
			
			PreparedStatement ps = con.prepareStatement("INSERT into market_operator(mcc,mnc,country,operator) values(?,?,?,?);");
			for(Row row: sheetToOperateOn) {
				if(row.getRowNum() != 0 && row.getCell(0) != null) {
					mcc = (int) row.getCell(0).getNumericCellValue();
					mnc = (int) row.getCell(1).getNumericCellValue();
					country = row.getCell(2).getStringCellValue();
					operator = row.getCell(3).getStringCellValue();
					
					ps.setInt(1, mcc);
					ps.setInt(2, mnc);
					ps.setString(3, country);
					ps.setString(4, operator);
					
					ps.execute();
				}
				
					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}