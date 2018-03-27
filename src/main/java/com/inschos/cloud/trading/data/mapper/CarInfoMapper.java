package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CarInfoModel;

/**
 * 创建日期：2018/3/26 on 12:00
 * 描述：
 * 作者：zhangyunhe
 */
public interface CarInfoMapper {

    int addCarInfo (CarInfoModel carInfoModel);

    CarInfoModel findOneByCarCode(String carCode);

}
