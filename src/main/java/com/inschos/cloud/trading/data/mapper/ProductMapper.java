package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.access.rpc.bean.MyBean;
import com.inschos.cloud.trading.access.rpc.bean.MyBean2;
import com.inschos.cloud.trading.access.rpc.bean.MyBean3;

/**
 * 创建日期：2018/4/26 on 20:18
 * 描述：
 * 作者：zhangyunhe
 */
public interface ProductMapper {

    long addCompany(MyBean myBean);

    long addProduct(MyBean2 myBean);

    long addCategory(MyBean3 myBean);

}
