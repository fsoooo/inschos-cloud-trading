package com.inschos.cloud.trading.data.dao;

import com.inschos.cloud.trading.data.mapper.AreaMapper;
import com.inschos.cloud.trading.model.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/20.
 */
@Component
public class DemoDao extends BaseDao {

    @Autowired
    AreaMapper areaMapper;

    public int doTran(){
        Area area = areaMapper.findOne(2);
        area.id=1;
        int insertFlag = areaMapper.insert(area);
        if (insertFlag>0){
            rollBack();
        }
        return insertFlag;
    }

    public int autoTran() throws Exception {
        Area area = areaMapper.findOne(2);
        area.id=1;
        int insertFlag = areaMapper.insert(area);
        if (insertFlag>0){
            area.id=-1;
//            insertFlag = areaMapper.insert(area);
            throw new Exception("test rollback");
        }

        return insertFlag;
    }

}
