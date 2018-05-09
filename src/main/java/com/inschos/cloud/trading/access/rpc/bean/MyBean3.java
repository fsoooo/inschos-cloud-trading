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
    public String name;

    /**
     * 上级分类id
     */
    public String pid;

    /**
     * 层级
     */
    public String level;


    public String state = "1";

    public MyBean3(String category_name, String pid, String level,String type) {
        this.name = category_name;
        this.pid = pid;
        this.level = level;
    }

}
