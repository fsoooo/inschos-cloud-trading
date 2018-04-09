package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CarRecordModel;

/**
 * 创建日期：2018/4/9 on 14:00
 * 描述：
 * 作者：zhangyunhe
 */
public interface CarRecordMapper {

    int addCarRecord(CarRecordModel carRecordModel);

    int updateCarRecord(CarRecordModel carRecordModel);

    CarRecordModel findOneByResponseNo(String responseNo);

}
