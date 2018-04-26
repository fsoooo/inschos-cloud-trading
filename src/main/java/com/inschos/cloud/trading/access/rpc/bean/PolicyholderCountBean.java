package com.inschos.cloud.trading.access.rpc.bean;

import com.inschos.cloud.trading.model.InsurancePolicyModel;
import com.inschos.cloud.trading.model.PolicyListCountModel;

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

    public PolicyholderCountBean(PolicyListCountModel PolicyListCountModel) {
        this.productId = PolicyListCountModel.product_id;
        this.count = PolicyListCountModel.product_count;
    }

    public PolicyholderCountBean(String productId, long count) {
        this.productId = productId;
        this.count = count;
    }

}
