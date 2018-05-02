package com.inschos.cloud.trading.access.rpc.bean;

/**
 * 创建日期：2018/4/27 on 19:42
 * 描述：
 * 作者：zhangyunhe
 */
public class MyBean3 {

    public String id;

    /**
     * 分类名称
     */
    public String category_name;
    /**
     * 父ID
     */
    public String pid;
    /**
     * 层级
     */
    public String level;
    /**
     * 类型代码
     */
    public String category_type;
    /**
     * 创建时间
     */
    public String created_at = "1524746356375";
    /**
     * 修改时间
     */
    public String updated_at = "1524746356375";
    /**
     * 删除标识 0删除 1可用
     */
    public String state = "1";

    public MyBean3(String category_name, String pid, String level, String category_type) {
        this.category_name = category_name;
        this.pid = pid;
        this.level = level;
        this.category_type = category_type;
    }

}
