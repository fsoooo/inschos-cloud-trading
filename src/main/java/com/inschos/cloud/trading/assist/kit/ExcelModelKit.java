package com.inschos.cloud.trading.assist.kit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 创建日期：2018/5/29 on 19:30
 * 描述：
 * 作者：zhangyunhe
 */
public class ExcelModelKit {

    public static final Map<String, CellStyle> CELL_STYLE_MAP;

    static {
        // 每次getWorkbookByteArray的时候，就会清空一次
        CELL_STYLE_MAP = new HashMap<>();
    }

    /**
     * Model的所有字段都必须是String
     *
     * @param cls Model类
     * @param map Model类字段与表列名的字典
     * @param row Excel每行数据
     * @return Model类
     */
    public static <T> T createModel(Class<T> cls, Map<String, String> map, Row row) {
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
                    field.set(t, formatter.formatCellValue(cell).trim());
                }
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            t = null;
        }

        return t;
    }

    public static <T> byte[] createExcelByteArray(List<ExcelModel<T>> data, Map<String, String> map, String sheetName) {

        if (data == null || data.isEmpty()) {
            return null;
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        writeExcel(sheet, data, map, 0);

        return getWorkbookByteArray(workbook);
    }

    public static <T> int writeExcel(Sheet sheet, List<ExcelModel<T>> data, Map<String, String> map, int startRow) {

        if (data == null || data.isEmpty()) {
            return 0;
        }

        Workbook workbook = sheet.getWorkbook();
        CellStyle defaultStyle = CELL_STYLE_MAP.get("defaultStyle");
        if (defaultStyle == null) {
            defaultStyle = workbook.createCellStyle();
            CELL_STYLE_MAP.put("defaultStyle", defaultStyle);
        }

        int size = data.size();
        Field[] declaredFields = getClassFieldArray(data.get(0).getData());

        if (declaredFields == null || declaredFields.length == 0) {
            return 0;
        }

        int count = 0;
        int length = declaredFields.length;
        Map<String, Field> fieldMap = new HashMap<>();

        for (int i = startRow; i < size + startRow; i++) {
            ExcelModel<T> tExcelModel = data.get(i - startRow);

            T t = tExcelModel.getData();

            if (t == null) {
                continue;
            }

            L.log.debug("writeExcel ===========> data = " + t.toString());

            Field[] fArray = getClassFieldArray(t);

            if (fArray == null || fArray.length == 0) {
                continue;
            }

            Row row = sheet.createRow(i);
            count++;

            fieldMap.clear();
            for (Field field : fArray) {
                fieldMap.put(field.getName(), field);
            }

            for (int j = 0; j < map.size(); j++) {
                Cell cell = row.createCell(j);

                String columnName = CellReference.convertNumToColString(cell.getAddress().getColumn());
                String fieldName = map.get(columnName);

                Field field = fieldMap.get(fieldName);

                if (field != null) {
                    field.setAccessible(true);
                    try {
                        Object o = field.get(t);

                        if (tExcelModel.hasStyle && !StringKit.isEmpty(tExcelModel.styleName)) {
                            CellStyle cellStyle = CELL_STYLE_MAP.get(tExcelModel.styleName);

                            if (cellStyle == null) {
                                cellStyle = workbook.createCellStyle();
                                Font font = workbook.createFont();
                                font.setBoldweight(tExcelModel.boldWeight);
                                cellStyle.setFont(font);
                                CELL_STYLE_MAP.put(tExcelModel.styleName, cellStyle);
                            }

                            cell.setCellStyle(cellStyle);
                        }

                        L.log.debug("writeExcel ===========> row = " + i + " column = " + j + " value = " + o);
                        if (o == null) {
                            cell.setCellValue("");
                        } else {
                            cell.setCellValue(o.toString());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return count;
    }

    public static void setColumnWidth(Sheet sheet, List<Integer> columnWidth) {
        if (sheet == null || columnWidth == null || columnWidth.isEmpty()) {
            return;
        }

        for (Integer aColumnWidth : columnWidth) {
            sheet.setColumnWidth(0, aColumnWidth);
        }
    }

    public static void autoSizeColumn(Sheet sheet, int columnNum) {
        if (sheet == null || columnNum == 0) {
            return;
        }

        for (int i = 0; i < columnNum; i++) {
            sheet.autoSizeColumn(i);
        }
    }


//    public static <T> byte[] createExcelByteArray(List<T> data, Map<String, String> map, String sheetName) {
//
//        if (data == null || data.isEmpty()) {
//            return null;
//        }
//
//        List<ExcelModel<T>> list = new ArrayList<>();
//        for (T datum : data) {
//            list.add(new ExcelModel<>(datum));
//        }
//
//        return createExcelByteArray(list, map, sheetName);
//    }
//
//    public static <T> int writeExcel(Sheet sheet, List<T> data, Map<String, String> map, int startRow) {
//
//        if (data == null || data.isEmpty()) {
//            return 0;
//        }
//
//        List<ExcelModel<T>> list = new ArrayList<>();
//        for (T datum : data) {
//            list.add(new ExcelModel<>(datum));
//        }
//
//        return writeExcel(sheet, list, map, startRow);
//    }

    public static byte[] getWorkbookByteArray(Workbook workbook) {
        byte[] result;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            workbook.write(os);
            result = os.toByteArray();
            CELL_STYLE_MAP.clear();
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private static Field[] getClassFieldArray(Object t) {
        Class<?> aClass = t.getClass();

        Field[] declaredFields = aClass.getDeclaredFields();
        ArrayList<Field> fields = new ArrayList<>();
        if (declaredFields != null) {
            fields = new ArrayList<>(Arrays.asList(declaredFields));
        }

        Class<?> superclass = aClass.getSuperclass();
        L.log.debug("getClassFieldArray ===========> name = " + superclass.getName());
        L.log.debug("getClassFieldArray ===========> simpleName = " + superclass.getSimpleName());
        while (!StringKit.equals(superclass.getSimpleName(), "Object")) {
            Field[] declaredFields1 = superclass.getDeclaredFields();

            if (declaredFields1 != null) {
                fields.addAll(Arrays.asList(declaredFields1));
            }

            superclass = superclass.getSuperclass();
        }

        declaredFields = fields.toArray(new Field[0]);
        return declaredFields;
    }

}
