package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.AgentBean;

/**
 * 创建日期：2018/4/26 on 14:14
 * 描述：
 * 作者：zhangyunhe
 */
public interface PersonService {

    AgentBean getAgentInfoByPersonIdManagerUuid(String managerUuid, String personId);

}
