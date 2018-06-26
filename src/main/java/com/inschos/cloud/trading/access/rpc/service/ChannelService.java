package com.inschos.cloud.trading.access.rpc.service;

import com.inschos.cloud.trading.access.rpc.bean.ChannelBean;

import java.util.List;

/**
 * 创建日期：2018/6/19 on 19:35
 * 描述：
 * 作者：zhangyunhe
 */
public interface ChannelService {

    List<ChannelBean> getChannelListByIds(List<String> ids);
}
