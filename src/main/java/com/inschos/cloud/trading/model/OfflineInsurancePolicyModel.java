package com.inschos.cloud.trading.model;

import com.inschos.cloud.trading.assist.kit.StringKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建日期：2018/5/29 on 18:11
 * 描述：
 * 作者：zhangyunhe
 */
public class OfflineInsurancePolicyModel {

    public final static Map<String, String> COLUMN_FIELD_MAP;

    static {
        COLUMN_FIELD_MAP = new HashMap<>();
        COLUMN_FIELD_MAP.put("A", "warranty_code");
        COLUMN_FIELD_MAP.put("B", "reason");
//        COLUMN_FIELD_MAP.put("C", "");
//        COLUMN_FIELD_MAP.put("D", "");
//        COLUMN_FIELD_MAP.put("E", "");
//        COLUMN_FIELD_MAP.put("F", "");
//        COLUMN_FIELD_MAP.put("G", "");
//        COLUMN_FIELD_MAP.put("H", "");
//        COLUMN_FIELD_MAP.put("I", "");
//        COLUMN_FIELD_MAP.put("J", "");
//        COLUMN_FIELD_MAP.put("K", "");
//        COLUMN_FIELD_MAP.put("L", "");
//        COLUMN_FIELD_MAP.put("M", "");
//        COLUMN_FIELD_MAP.put("N", "");
//        COLUMN_FIELD_MAP.put("O", "");
//        COLUMN_FIELD_MAP.put("P", "");
//        COLUMN_FIELD_MAP.put("Q", "");
//        COLUMN_FIELD_MAP.put("R", "");
//        COLUMN_FIELD_MAP.put("S", "");
//        COLUMN_FIELD_MAP.put("T", "");
    }

    public final static List<String> TITLE_NAME_LIST;

    static {
        TITLE_NAME_LIST = new ArrayList<>();
        TITLE_NAME_LIST.add("保单号");
    }

    /**
     * 主键id
     */
    public String id;

    /**
     * 保单唯一标识
     */
    public String warranty_uuid;

    /**
     * 保单号
     */
    public String warranty_code;

    /**
     * 创建时间
     */
    public String created_at;

    /**
     * 结束时间
     */
    public String updated_at;

    /**
     * 删除标识 0删除 1可用
     */
    public String state;

    public String reason;


    public boolean isEnable() {
        return !StringKit.isEmpty(warranty_code);
    }

    public boolean isEmptyLine() {
        return StringKit.isEmpty(warranty_code);
    }

    public boolean isTitle() {
        return StringKit.equals(this.warranty_code, "保单号");
    }

    public static OfflineInsurancePolicyModel getTitleModel() {
        OfflineInsurancePolicyModel title = new OfflineInsurancePolicyModel();
        title.warranty_code = "保单号";
        title.reason = "导入失败原因";
        return title;
    }

}
