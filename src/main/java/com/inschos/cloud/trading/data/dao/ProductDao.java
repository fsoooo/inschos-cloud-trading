package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.access.rpc.bean.MyBean;
import com.inschos.cloud.trading.access.rpc.bean.MyBean2;
import com.inschos.cloud.trading.annotation.CheckParams;
import com.inschos.cloud.trading.data.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/4/26 on 20:18
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class ProductDao extends BaseDao {

    @Autowired
    ProductMapper productMapper;

    public long addCompany(MyBean myBean) {
        return productMapper.addCompany(myBean);
    }

    public long addProduct(MyBean2 myBean) {
        return productMapper.addProduct(myBean);
    }

}
