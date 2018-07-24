package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CarRecordMapper;
import com.inschos.cloud.trading.model.CustCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/4/9 on 14:01
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class CarRecordDao extends BaseDao {

    @Autowired
    public CarRecordMapper carRecordMapper;

    public int addCarRecord(CustCar custCar) {
        return carRecordMapper.addCarRecord(custCar);
    }

    public CustCar findOneByResponseNo(String responseNo) {
        return carRecordMapper.findOneByResponseNo(responseNo);
    }

    public CustCar findOneByCarCode(String carCode) {
        return carRecordMapper.findOneByCarCode(carCode);
    }

    public int updateCarRecordByResponseNo(CustCar custCar) {
        return carRecordMapper.updateCarRecordByResponseNo(custCar);
    }

    public int updateCarRecordByCarCode(CustCar custCar) {
        return carRecordMapper.updateCarRecordByCarCode(custCar);
    }

}
