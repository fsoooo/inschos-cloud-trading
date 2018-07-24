package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CarInfoMapper;
import com.inschos.cloud.trading.model.CustWarrantyCar;
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

    public int addCarInfo(CustWarrantyCar custWarrantyCar) {
        return carInfoMapper.addCarInfo(custWarrantyCar);
    }

    public CustWarrantyCar findOneByWarrantyUuid(String warrantyUuid) {
        return carInfoMapper.findOneByWarrantyUuid(warrantyUuid);
    }

    public CustWarrantyCar findCarInfoCarCodeAndFrameNoByWarrantyUuid(String warrantyUuid) {
        return carInfoMapper.findCarInfoCarCodeAndFrameNoByWarrantyUuid(warrantyUuid);
    }

    public List<CustWarrantyCar> findWarrantyUuidByBizId(String bizId) {
        return carInfoMapper.findWarrantyUuidByBizId(bizId);
    }

    public List<CustWarrantyCar> findWarrantyUuidByThpBizID(String thpBizID) {
        return carInfoMapper.findWarrantyUuidByThpBizID(thpBizID);
    }

    public CustWarrantyCar findBjCodeFlagAndBizIdByWarrantyUuid(String warrantyUuid) {
        return carInfoMapper.findBjCodeFlagAndBizIdByWarrantyUuid(warrantyUuid);
    }

}
