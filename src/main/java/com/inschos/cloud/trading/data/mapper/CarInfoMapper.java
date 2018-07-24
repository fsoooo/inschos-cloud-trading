package com.inschos.cloud.trading.data.mapper;

import com.inschos.cloud.trading.model.CustWarrantyCar;

import java.util.List;

/**
 * 创建日期：2018/3/26 on 12:00
 * 描述：
 * 作者：zhangyunhe
 */
public interface CarInfoMapper {

    int addCarInfo(CustWarrantyCar custWarrantyCar);

    CustWarrantyCar findOneByWarrantyUuid(String warrantyUuid);

    CustWarrantyCar findCarInfoCarCodeAndFrameNoByWarrantyUuid(String warrantyUuid);

    List<CustWarrantyCar> findByBizId(String bizId);

    List<CustWarrantyCar> findWarrantyUuidByBizId(String bizId);

    List<CustWarrantyCar> findWarrantyUuidByThpBizID(String thpBizID);

    CustWarrantyCar findBjCodeFlagAndBizIdByWarrantyUuid(String warrantyUuid);

    int updateBjCodeFlagByWarrantyUuid(CustWarrantyCar custWarrantyCar);
}
