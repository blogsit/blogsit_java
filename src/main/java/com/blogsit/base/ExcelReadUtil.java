package com.blogsit.base;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExcelReadUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelReadUtil.class);

    public static List<ExcelColumn> readExcel(String fileAddress, int excelSheet, int startColumn, int endColumn) {
        File file = new File(fileAddress);
        List<ExcelColumn> result = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(excelSheet);
            int startRowIndex = sheet.getFirstRowNum() > startColumn ? sheet.getFirstRowNum() : startColumn;
            int endRowIndex = sheet.getLastRowNum() < endColumn ? sheet.getLastRowNum() : endColumn;
            if(startColumn >= endColumn){
                logger.info("excel读取结束");
                return null;
            }

            for (int rIndex = startRowIndex; rIndex <= endRowIndex; rIndex++) {
                Row row = sheet.getRow(rIndex);
                ExcelColumn column = new ExcelColumn();
                if(row == null || row.getCell(0) == null){
                    continue;
                }
                column.setColumnOne(getCellValue(row.getCell(0)));
                if(row.getCell(1) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnTwo(getCellValue(row.getCell(1)));
                if(row.getCell(2) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnThree(getCellValue(row.getCell(2)));
                if(row.getCell(3) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnFour(getCellValue(row.getCell(3)));
                if(row.getCell(4) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnFive(getCellValue(row.getCell(4)));
                if(row.getCell(5) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnSix(getCellValue(row.getCell(5)));
                if(row.getCell(6) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnSeven(getCellValue(row.getCell(6)));
                if(row.getCell(7) == null){
                    continue;
                }
                column.setColumnEight(getCellValue(row.getCell(7)));
                if(row.getCell(8) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnNine(getCellValue(row.getCell(8)));
                if(row.getCell(9) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnTen(getCellValue(row.getCell(9)));
                if(row.getCell(10) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnTen(getCellValue(row.getCell(10)));
                if(row.getCell(11) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnTen(getCellValue(row.getCell(11)));
                if(row.getCell(12) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnTen(getCellValue(row.getCell(12)));
                if(row.getCell(13) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnTen(getCellValue(row.getCell(13)));
                if(row.getCell(14) == null){
                    result.add(column);
                    continue;
                }
                column.setColumnTen(getCellValue(row.getCell(14)));
                result.add(column);
            }
        } catch (Exception e) {
            logger.error("excel读取异常",e);
        }
        return result;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 判断数据的类型
        switch (cell.getCellType()) {
            // 数字
            case Cell.CELL_TYPE_NUMERIC:
                // 处理日期格式、时间格式
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = null;
                    // 验证short值
                    if (cell.getCellStyle().getDataFormat() == 14) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd");
                    } else if (cell.getCellStyle().getDataFormat() == 21) {
                        sdf = new SimpleDateFormat("HH:mm:ss");
                    } else if (cell.getCellStyle().getDataFormat() == 22) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    } else {
                        throw new RuntimeException("日期格式错误!!!");
                    }
                    Date date = cell.getDateCellValue();
                    cellValue = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 0) {//处理数值格式
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cellValue = String.valueOf(cell.getRichStringCellValue().getString());
                }
                break;
            case Cell.CELL_TYPE_STRING: // 字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: // 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: // 空值
                cellValue = null;
                break;
            case Cell.CELL_TYPE_ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
}
