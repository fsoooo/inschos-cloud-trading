package com.inschos.cloud.trading.access.rpc.bean;

import com.inschos.cloud.trading.model.PolicyListCount;

/**
 * 创建日期：2018/4/26 on 15:52
 * 描述：
 * 作者：zhangyunhe
 */
public class PolicyholderCountBean {

    public String productId;
    public long count;

    public PolicyholderCountBean() {

    }

    public PolicyholderCountBean(PolicyListCount PolicyListCount) {
        this.productId = PolicyListCount.product_id;
        this.count = PolicyListCount.product_count;
    }

    public PolicyholderCountBean(String productId, long count) {
        this.productId = productId;
        this.count = count;
    }

}
