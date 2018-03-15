package com.inschos.cloud.trading.dao;


import com.inschos.cloud.trading.model.Area;

import java.util.List;

/**
 * Created by IceAnt on 2017/7/14.
 */
public interface AreaMapper {


    List<Area> findAll();

    List<Area> findChildren(int parentId);

    Area findOne(int id);

    Area findOneByAreaCode(int areaCode);

    Area findByNamePid(Area region);


}
