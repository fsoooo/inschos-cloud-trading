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
    public long id;

    public String name;

    /**
     * 返回代理人的业管账号idmanager_uuid
     */
    public String manager_uuid;
    /**
     * 返回代理人渠道id
     */
    public long channel_id;
    /**
     * 返回代理人个人信息表id
     */
    public long person_id;
    /**
     * 返回代理人手机号
     */
    public String phone;
    /**
     * 返回代理人工号
     */
    public String job_num;
    /**
     * 返回代理人职位id
     */
    public long position_id;
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

    @Override
    public String toString() {
        return "AgentBean{" +
                "id=" + id +
                ", manager_uuid=" + manager_uuid +
                ", channel_id=" + channel_id +
                ", person_id=" + person_id +
                ", phone=" + phone +
                ", job_num=" + job_num +
                ", position_id=" + position_id +
                ", note='" + note + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                '}';
    }
}
