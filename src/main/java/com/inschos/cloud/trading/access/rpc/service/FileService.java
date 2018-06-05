package com.inschos.cloud.trading.access.rpc.service;

/**
 * 创建日期：2018/5/15 on 14:35
 * 描述：
 * 作者：zhangyunhe
 */
public interface FileService {

    String getFileUrl(String fileKey);

    boolean upload(String fileKey, String fileName, byte[] bytes);
}
