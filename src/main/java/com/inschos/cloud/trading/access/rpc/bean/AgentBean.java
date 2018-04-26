package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/4/26 on 14:15
 * 描述：
 * 作者：zhangyunhe
 */
public class AgentBean {

    /**
     * 返回代理人id
     */
    public int id;

    /**
     * 返回代理人的业管账号idmanager_uuid
     */
    public int manager_uuid;
    /**
     * 返回代理人渠道id
     */
    public int channel_id;
    /**
     * 返回代理人个人信息表id
     */
    public int person_id;
    /**
     * 返回代理人手机号
     */
    public int phone;
    /**
     * 返回代理人工号
     */
    public int job_num;
    /**
     * 返回代理人职位id
     */
    public int position_id;
    /**
     * 返回代理人备注
     */
    public String note;
    /**
     * 返回代理人入职时间
     */
    public String start_time;
    /**
     * 返回代理人离职时间
     */
    public String end_time;

}
