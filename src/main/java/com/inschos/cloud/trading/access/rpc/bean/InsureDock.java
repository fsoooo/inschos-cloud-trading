package com.inschos.cloud.trading.access.rpc.bean;

import com.inschos.common.assist.kit.StringKit;

/**
 * author   meiming_mm@163.com
 * date     2018/7/27
 * version  v1.0.0
 */
public class InsureDock {

    public static boolean isNewProduct(String productKey) {

        boolean isNew = false;

        if (!StringKit.isEmpty(productKey)) {

            switch (productKey) {
                case "Qx":
                    isNew = false;
                    break;
                case "Tk":
                    isNew = false;
                    break;
                case "Hg":
                    isNew = false;
                    break;
                case "Ya":
                    isNew = false;
                    break;
                case "Axc":
                    isNew = false;
                    break;
                case "Za":
                    isNew = false;
                    break;
                case "Ygc":
                    isNew = true;
                    break;
                case "Yd":
                    isNew = true;
                    break;
                case "Zhf":
                    isNew = true;
                    break;
                case "Tkks":
                    isNew = true;
                    break;

            }
        }
        return isNew;
    }
}
