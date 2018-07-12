package com.inschos.cloud.trading.assist.kit;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * author   meiming_mm@163.com
 * date     2018/7/4
 * version  v1.0.0
 */
public class AreaKit {


    private static List<AreaBean> areaBeanList = null;

    public static List<AreaBean> getAreas() {
        return areaBeanList;
    }

    public List<AreaBean> areaBeanList1;

    private static void init() {

        InputStream stream = AreaKit.class.getResourceAsStream("/area.json");
        byte[] bytes = IOKit.readStream(stream);
        String s = new String(bytes);
        areaBeanList = JsonKit.json2Bean(s, new TypeReference<List<AreaBean>>() {
        });
        if (areaBeanList == null) {
            areaBeanList = new ArrayList<>();
        }

        JsonKit.bean2Json(areaBeanList);
    }

    public static class AreaBean {
        public String code;

        public String name;
    }

    static {
        if (areaBeanList == null) {
            init();
        }
    }
}
