package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CarInfoModel;

import java.util.List;

/**
 * 创建日期：2018/3/26 on 12:00
 * 描述：
 * 作者：zhangyunhe
 */
public interface CarInfoMapper {

    int addCarInfo(CarInfoModel carInfoModel);

    CarInfoModel findOneByWarrantyUuid(String warrantyUuid);

    CarInfoModel findCarInfoCarCodeAndFrameNoByWarrantyUuid(String warrantyUuid);

    List<CarInfoModel> findByBizId(String bizId);

    List<CarInfoModel> findWarrantyUuidByBizId(String bizId);

    List<CarInfoModel> findWarrantyUuidByThpBizID(String thpBizID);
}
