package com.inschos.cloud.trading.assist.kit;

import com.inschos.common.assist.kit.StringKit;
import org.apache.poi.ss.usermodel.Font;

/**
 * 创建日期：2018/6/4 on 15:36
 * 描述：
 * 作者：zhangyunhe
 */
public class ExcelModel<T> {

    public T t;
    public boolean hasStyle = true;
    public String styleName = "";

    public ExcelModel(T t) {
        if (t == null) {
            return;
        }
        this.t = t;
        hasStyle = false;
        styleName = "";
    }

    public ExcelModel(T t, boolean hasStyle, String styleName) {
        this(t);
        this.hasStyle = hasStyle;
        if (hasStyle) {
            if (StringKit.isEmpty(styleName)) {
                hasStyle = false;
            } else {
                this.styleName = styleName;
            }
        }
    }

    public T getData() {
        return t;
    }

    public short boldWeight = Font.BOLDWEIGHT_NORMAL;

}
