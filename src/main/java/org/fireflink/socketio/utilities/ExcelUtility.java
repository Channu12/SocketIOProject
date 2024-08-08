package org.fireflink.socketio.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtility {

	public Map<String, List<String>> getDataFromExcelAsMap(String excelPath, String sheetName) throws EncryptedDocumentException, IOException, InterruptedException {
		Thread.sleep(5000);
		Map<String, List<String>> entireData = new LinkedHashMap<String, List<String>>();
		FileInputStream fis = new FileInputStream(excelPath);
		Workbook workbook = WorkbookFactory.create(fis);
		DataFormatter df = new DataFormatter();
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		Sheet sheet = workbook.getSheet(sheetName);
		int rowCount = sheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rowCount; i++) {
			List<String> singleUserData = new LinkedList<String>();
			int columnCount = sheet.getRow(i).getPhysicalNumberOfCells();
			for (int j = 1; j < columnCount; j++) {
				singleUserData.add(df.formatCellValue(sheet.getRow(i).getCell(j), evaluator));
			}
			entireData.put(df.formatCellValue(sheet.getRow(i).getCell(0), evaluator), singleUserData);
		}
		workbook.close();
		fis.close();
		return entireData;
	}
}


