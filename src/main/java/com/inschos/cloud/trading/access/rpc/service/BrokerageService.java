package com.inschos.cloud.trading.access.rpc.service;

import org.springframework.stereotype.Service;

/**
 * 创建日期：2018/4/20 on 16:43
 * 描述：
 * 作者：zhangyunhe
 */
public interface BrokerageService {

    String getBrokerageByChannelIdForManagerSystem(String channelId);
}
