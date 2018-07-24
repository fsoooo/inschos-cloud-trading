package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustCar;

/**
 * 创建日期：2018/4/9 on 14:00
 * 描述：
 * 作者：zhangyunhe
 */
public interface CarRecordMapper {

    int addCarRecord(CustCar custCar);

    int updateCarRecordByResponseNo(CustCar custCar);

    int updateCarRecordByCarCode(CustCar custCar);

    CustCar findOneByResponseNo(String responseNo);

    CustCar findOneByCarCode(String carCode);

}
