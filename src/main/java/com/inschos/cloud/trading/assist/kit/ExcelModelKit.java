package com.inschos.cloud.trading.assist.kit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建日期：2018/5/29 on 19:30
 * 描述：
 * 作者：zhangyunhe
 */
public class ExcelModelKit {

    /**
     * Model的所有字段都必须是String
     *
     * @param cls Model类
     * @param map Model类字段与表列名的字典
     * @param row Excel每行数据
     * @return Model类
     */
    public static <T> T initModel(Class<T> cls, Map<String, String> map, Row row) {
        if (cls == null || map == null || row == null) {
            return null;
        }

        T t = null;
        try {

            t = cls.newInstance();
            Field[] farray = cls.getDeclaredFields();

            if (farray == null || farray.length == 0) {
                return t;
            }

            Map<String, Field> fieldMap = new HashMap<>();

            for (Field field : farray) {
                fieldMap.put(field.getName(), field);
            }

            DataFormatter formatter = new DataFormatter();

            for (Cell cell : row) {
                String columnName = CellReference.convertNumToColString(cell.getAddress().getColumn());
                String fieldName = map.get(columnName);
                Field field = fieldMap.get(fieldName);

                if (field != null) {
                    field.set(t, formatter.formatCellValue(cell));
                }
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            t = null;
        }

        return t;
    }

    public static <T> byte[] createExcelByteArray(List<String> title, List<T> data, Map<String, String> map, String sheetName) {
        byte[] result = null;

        if (title == null || title.isEmpty()) {
            return null;
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        HSSFRow row = sheet.createRow(0);

        int size = title.size();
        for (int i = 0; i < size; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(title.get(i));
        }

        if (data != null && !data.isEmpty()) {
            for (int i = 1; i < data.size() + 1; i++) {
                HSSFRow contentRow = sheet.createRow(i);
                T t = data.get(i);
                Class<?> aClass = t.getClass();

                Field[] farray = aClass.getDeclaredFields();

                if (farray == null || farray.length == 0) {
                    break;
                }

                Map<String, Field> fieldMap = new HashMap<>();

                for (Field field : farray) {
                    fieldMap.put(field.getName(), field);
                }

                for (int j = 0; j < size; j++) {
                    HSSFCell cell = contentRow.createCell(j);

                    String columnName = CellReference.convertNumToColString(cell.getAddress().getColumn());
                    String fieldName = map.get(columnName);

                    Field field = fieldMap.get(fieldName);

                    if (field != null) {
                        field.setAccessible(true);
                        try {
                            Object o = field.get(t);

                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            cell.setCellValue(o.toString());

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        result = workbook.getBytes();

        return result;
    }

}
