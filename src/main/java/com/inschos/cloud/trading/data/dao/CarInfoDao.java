package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CarInfoMapper;
import com.inschos.cloud.trading.model.CarInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public CarInfoModel findOneByWarrantyUuid(String warrantyUuid) {
        return carInfoMapper.findOneByWarrantyUuid(warrantyUuid);
    }

    public List<CarInfoModel> findWarrantyUuidByBizId(String bizId) {
        return carInfoMapper.findWarrantyUuidByBizId(bizId);
    }

    public List<CarInfoModel> findWarrantyUuidByThpBizID(String thpBizID) {
        return carInfoMapper.findWarrantyUuidByThpBizID(thpBizID);
    }
}
