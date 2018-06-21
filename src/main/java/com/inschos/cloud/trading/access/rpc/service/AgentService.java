package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.AgentBean;

import java.util.List;

/**
 * 创建日期：2018/4/26 on 14:14
 * 描述：
 * 作者：zhangyunhe
 */
public interface AgentService {

    AgentBean getAgentInfoByPersonIdManagerUuid(String managerUuid, long personId);

    AgentBean getAgentById(long agentId);

    List<AgentBean> getAllBySearchName(String managerUuid, String searchName);

    List<AgentBean> getAgentListByIds(List<Long> agentIds);

}
