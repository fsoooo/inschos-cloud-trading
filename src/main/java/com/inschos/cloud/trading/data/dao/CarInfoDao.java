package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CarInfoMapper;
import com.inschos.cloud.trading.model.CarInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/3/26 on 12:00
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CarInfoDao {

    @Autowired
    private CarInfoMapper carInfoMapper;

    public int addCarInfo(CarInfoModel carInfoModel) {
        return carInfoMapper.addCarInfo(carInfoModel);
    }

    public CarInfoModel findOneByWarrantyCode(String warrantyCode) {
        return carInfoMapper.findOneByWarrantyCode(warrantyCode);
    }
}
