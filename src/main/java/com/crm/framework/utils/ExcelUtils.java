package com.crm.framework.utils;

import com.crm.framework.exceptions.ExcelReaderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for reading test data from Excel (.xlsx) files.
 * Row 0 is treated as a header row and is skipped.
 * Returns Object[][] for use with TestNG @DataProvider.
 */
public class ExcelUtils {

    private static final Logger log = LogManager.getLogger(ExcelUtils.class);

    private ExcelUtils() {}

    /**
     * Reads all data rows from the specified sheet.
     *
     * @param filePath  absolute or relative path to the .xlsx file
     * @param sheetName name of the sheet to read
     * @return Object[][] where each row = one test data set
     * @throws ExcelReaderException if the file does not exist or cannot be parsed
     */
    public static Object[][] readSheetData(String filePath, String sheetName) {
        validateFile(filePath);
        log.info("Reading Excel data: {} | Sheet: {}", filePath, sheetName);
        List<Object[]> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new ExcelReaderException(
                    "Sheet '" + sheetName + "' not found in: " + filePath);
            }

            int totalRows = sheet.getLastRowNum();    // 0-indexed; row 0 = header
            int totalCols = sheet.getRow(0).getLastCellNum();

            for (int rowIdx = 1; rowIdx <= totalRows; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) continue;

                Object[] rowData = new Object[totalCols];
                for (int colIdx = 0; colIdx < totalCols; colIdx++) {
                    rowData[colIdx] = getCellValue(row.getCell(colIdx));
                }
                data.add(rowData);
            }

        } catch (IOException e) {
            throw new ExcelReaderException(
                "Failed to read Excel file: " + filePath, e);
        }

        log.info("Loaded {} data rows from sheet '{}'", data.size(), sheetName);
        return data.toArray(new Object[0][]);
    }

    /** Returns a specific cell value as String. */
    public static String getCellString(String filePath, String sheetName, int row, int col) {
        validateFile(filePath);
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new ExcelReaderException(
                    "Sheet '" + sheetName + "' not found in: " + filePath);
            }
            return String.valueOf(getCellValue(sheet.getRow(row).getCell(col)));

        } catch (IOException e) {
            throw new ExcelReaderException(
                "Failed to read cell [" + row + "," + col + "] from: " + filePath, e);
        }
    }

    private static void validateFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new ExcelReaderException("Excel file path must not be null or blank");
        }
        if (!Files.exists(Paths.get(filePath))) {
            throw new ExcelReaderException("Excel file not found: " + filePath);
        }
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCachedFormulaResultType() == CellType.NUMERIC
                    ? String.valueOf((long) cell.getNumericCellValue())
                    : cell.getStringCellValue();
            default      -> "";
        };
    }
}
