package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.CarRecordMapper;
import com.inschos.cloud.trading.model.CarRecordModel;
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

    public int addCarRecord(CarRecordModel carRecordModel) {
        return carRecordMapper.addCarRecord(carRecordModel);
    }

    public CarRecordModel findOneByResponseNo(String responseNo) {
        return carRecordMapper.findOneByResponseNo(responseNo);
    }

    public CarRecordModel findOneByCarCode(String carCode) {
        return carRecordMapper.findOneByCarCode(carCode);
    }

    public int updateCarRecordByResponseNo(CarRecordModel carRecordModel) {
        return carRecordMapper.updateCarRecordByResponseNo(carRecordModel);
    }

    public int updateCarRecordByCarCode(CarRecordModel carRecordModel) {
        return carRecordMapper.updateCarRecordByCarCode(carRecordModel);
    }

}
