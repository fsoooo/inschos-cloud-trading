package com.inschos.cloud.trading.assist.kit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 创建日期：2018/6/4 on 15:36
 * 描述：
 * 作者：zhangyunhe
 */
public class ExcelModel<T> {

    public T t;
    public boolean hasStyle = false;
    public CellStyle cellStyle;

    public ExcelModel(T t) {
        if (t == null) {
            return;
        }
        this.t = t;
    }

    public ExcelModel(T t, boolean hasStyle) {
        this(t);
        this.hasStyle = hasStyle;
    }

    public T getData() {
        return t;
    }

    public void setCellStyle(CellStyle cellStyle) {
        if (hasStyle) {
            this.cellStyle = cellStyle;
        }
    }

}
