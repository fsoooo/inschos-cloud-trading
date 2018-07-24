package com.inschos.cloud.trading.assist.kit;

import com.inschos.common.assist.kit.L;
import com.inschos.common.assist.kit.StringKit;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 创建日期：2018/5/29 on 19:30
 * 描述：
 * 作者：zhangyunhe
 */
public class ExcelModelKit {

    public final static String TYPE_STRING = "TYPE_STRING";
    public final static String TYPE_DOUBLE = "TYPE_DOUBLE";

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

        T t;
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
                    String value = "";
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                //注：format格式 yyyy-MM-dd hh:mm:ss 中小时为12小时制，若要24小时制，则把小h变为H即可，yyyy-MM-dd HH:mm:ss
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                                break;
                            } else {
                                value = formatter.formatCellValue(cell);
                            }
                            break;
                        default:
                            value = formatter.formatCellValue(cell);
                            break;
                    }
                    field.set(t, value);
                }
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            t = null;
        }

        return t;
    }

    public static <T> byte[] createExcelByteArray(List<ExcelModel<T>> data, Map<String, String> map, Map<String, String> typeMap, String sheetName) {

        if (data == null || data.isEmpty()) {
            return null;
        }

        if (typeMap == null) {
            typeMap = new HashMap<>();
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        writeRank(sheet, data, map, 0, getCellStyleMap(), typeMap);
        autoSizeColumn(sheet, map.size());

        return getWorkbookByteArray(workbook);
    }


    /**
     * 逐段写入行
     *
     * @param sheet          逐段写入行
     * @param data           写入数据
     * @param map            数据与excel对应表
     * @param startRow       开始行数
     * @param CELL_STYLE_MAP 单元格样式map
     * @param <T>            数据
     * @return 下次写入的开始行数
     */
    public static <T> int writeRank(Sheet sheet, List<ExcelModel<T>> data, Map<String, String> map, int startRow, Map<String, CellStyle> CELL_STYLE_MAP, Map<String, String> typeMap) {

        if (data == null || data.isEmpty()) {
            return 0;
        }

        if (typeMap == null) {
            typeMap = new HashMap<>();
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
        // int length = declaredFields.length;
        Map<String, Field> fieldMap = new HashMap<>();

        for (int i = startRow; i < size + startRow; i++) {
            ExcelModel<T> tExcelModel = data.get(i - startRow);

            T t = tExcelModel.getData();

            if (t == null) {
                continue;
            }

            L.log.debug("writeRank ===========> data = " + t.toString());

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

                        saveStyleMap(workbook, cell, tExcelModel, CELL_STYLE_MAP);

                        L.log.debug("writeRank ===========> row = " + i + " column = " + j + " value = " + o);
                        if (o == null) {
                            cell.setCellValue("");
                        } else {
                            String s = typeMap.get(fieldName);

                            if (StringKit.isEmpty(s)) {
                                s = "";
                            }

                            switch (s) {
                                case TYPE_DOUBLE:
                                    String string = o.toString();
                                    if (StringKit.isEmpty(string)) {
                                        cell.setCellValue(0.0D);
                                    } else if (StringKit.isNumeric(string)) {
                                        cell.setCellValue(Double.valueOf(string));
                                    } else {
                                        cell.setCellValue(string);
                                    }
                                    break;
                                case TYPE_STRING:
                                default:
                                    cell.setCellValue(o.toString());
                                    break;
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return count;
    }

    /**
     * 写入列
     *
     * @param sheet          写入列
     * @param data           写入数据
     * @param rowIndex       第几列
     * @param startRow       开始行
     * @param CELL_STYLE_MAP 单元格样式map
     */
    public static void writeRow(Sheet sheet, List<ExcelModel<String>> data, int rowIndex, int startRow, Map<String, CellStyle> CELL_STYLE_MAP) {
        if (sheet == null || data == null || data.isEmpty()) {
            return;
        }

        Workbook workbook = sheet.getWorkbook();
        CellStyle defaultStyle = CELL_STYLE_MAP.get("defaultStyle");
        if (defaultStyle == null) {
            defaultStyle = workbook.createCellStyle();
            CELL_STYLE_MAP.put("defaultStyle", defaultStyle);
        }

        int maxRow = data.size() + startRow;

        for (int i = startRow; i < maxRow; i++) {
            Row row1 = sheet.createRow(i);
            Cell cell = row1.createCell(rowIndex);

            ExcelModel<String> stringExcelModel = data.get(i - startRow);
            saveStyleMap(workbook, cell, stringExcelModel, CELL_STYLE_MAP);
            cell.setCellValue(stringExcelModel.t);
        }
    }

    private static <T> void saveStyleMap(Workbook workbook, Cell cell, ExcelModel<T> data, Map<String, CellStyle> CELL_STYLE_MAP) {
        if (data.hasStyle && !StringKit.isEmpty(data.styleName)) {
            CellStyle cellStyle = CELL_STYLE_MAP.get(data.styleName);

            if (cellStyle == null) {
                cellStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBoldweight(data.boldWeight);
                cellStyle.setFont(font);
                CELL_STYLE_MAP.put(data.styleName, cellStyle);
            }

            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * 设置行宽
     *
     * @param sheet       工作薄
     * @param columnWidth 行宽大小
     */
    public static void setColumnWidth(Sheet sheet, List<Integer> columnWidth) {
        if (sheet == null || columnWidth == null || columnWidth.isEmpty()) {
            return;
        }

        for (Integer aColumnWidth : columnWidth) {
            sheet.setColumnWidth(0, aColumnWidth);
        }
    }

    /**
     * 设置自适应行宽，在生成完成后调用一次。
     *
     * @param sheet     工作薄
     * @param columnNum 一共多少列
     */
    public static void autoSizeColumn(Sheet sheet, int columnNum) {
        if (sheet == null || columnNum == 0) {
            return;
        }

        for (int i = 0; i < columnNum; i++) {
            sheet.autoSizeColumn(i);
            // sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
        }
    }

    /**
     * 设置自适应行宽并加倍，在生成完成后调用一次。
     *
     * @param sheet     工作薄
     * @param columnNum 一共多少列
     */
    public static void autoSizeColumnAndMultiplier(Sheet sheet, int columnNum, float multiplier) {
        if (sheet == null || columnNum == 0) {
            return;
        }

        for (int i = 0; i < columnNum; i++) {
            sheet.autoSizeColumn(i);
            float f = multiplier * sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, (int) f);
        }
    }

    /**
     * 设置指定行宽，在生成完成后调用一次。
     *
     * @param sheet     工作薄
     * @param columnNum 一共多少列
     */
    public static void setSizeColumn(Sheet sheet, List<Integer> columnNum) {
        if (sheet == null || columnNum == null || columnNum.isEmpty()) {
            return;
        }

        for (int i = 0; i < columnNum.size(); i++) {
            sheet.setColumnWidth(i, columnNum.get(i));
        }
    }

    /**
     * 获取Excel二进制文件
     *
     * @param workbook Excel
     * @return Excel二进制文件
     */
    public static byte[] getWorkbookByteArray(Workbook workbook) {
        byte[] result;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            workbook.write(os);
            result = os.toByteArray();
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

    /**
     * 获取单元格样式map
     *
     * @return 单元格样式map
     */
    public static Map<String, CellStyle> getCellStyleMap() {
        return new HashMap<>();
    }

    private final static String[] letterArray = {"", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * 获取数据与Excel对应表
     *
     * @param title          数据
     * @param startIndexName 开始列标
     * @return 数据与Excel对应表
     */
    public static Map<String, String> getColumnFieldMap(List<String> title, String startIndexName) {
        return getColumnFieldMap(title, getColumnIndexByName(startIndexName));
    }

    /**
     * 获取数据与Excel对应表
     *
     * @param title      数据
     * @param startIndex 开始列标索引
     * @return 数据与Excel对应表
     */
    public static Map<String, String> getColumnFieldMap(List<String> title, int startIndex) {
        if (title == null || title.isEmpty()) {
            return new HashMap<>();
        }

        List<String> columnList = createColumnList(title.size() + startIndex);

        if (startIndex > 0) {
            for (int i = 0; i < startIndex; i++) {
                columnList.remove(0);
            }
        }

        Map<String, String> result = new HashMap<>();

        for (int i = 0; i < columnList.size(); i++) {
            result.put(columnList.get(i), title.get(i));
        }

        return result;
    }

    private static List<String> createColumnList(int size) {
        List<String> list = new ArrayList<>();

        for (int j = 0; j < size; j++) {
            int offset = j;
            int result;

            List<Integer> unit = new ArrayList<>();
            do {
                result = offset / letterArray.length;
                int i = offset % letterArray.length;

                if (i != 0) {
                    unit.add(0, i);
                } else {
                    size++;
                    break;
                }

                offset = result;

            } while (offset != 0);

            if (!unit.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Integer integer : unit) {
                    sb.append(letterArray[integer]);
                }
                list.add(sb.toString());
            }

//            System.out.println("count = " + count);
//            Integer[] integers = unit.toArray(new Integer[0]);
//            System.out.println("integers = " + Arrays.toString(integers));

        }

        return list;
    }

    private static int getColumnIndexByName(String name) {
        if (StringKit.isEmpty(name)) {
            return 0;
        }

        int length = name.length();

        List<String> strings = Arrays.asList(letterArray);
        int index = 0;
        int unit = length;

        for (int i = 0; i < length; i++) {
            unit--;
            char c = name.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                int i1 = strings.indexOf(String.valueOf(c));

                if (i1 == -1 || i1 == 0) {
                    index = 0;
                    break;
                }

                index = index + (i1 * ((int) Math.pow(letterArray.length - 1, unit)));

            } else {
                index = 0;
                break;
            }
        }

        return index;
    }
}
