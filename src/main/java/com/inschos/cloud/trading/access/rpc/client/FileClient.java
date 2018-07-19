package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.service.FileService;
import com.inschos.common.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 创建日期：2018/5/15 on 14:37
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class FileClient {

    @Value("${rpc.remote.file.host}")
    private String host;

    private final String uri = "/rpc/file";


    private FileService getService() {
        return new HproseHttpClient(host + uri).useService(FileService.class);
    }


    public String getFileUrl(String fileKey) {
        try {
            FileService service = getService();
            return service != null ? service.getFileUrl(fileKey) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean upload(String fileKey, String fileName, byte[] bytes) {
        try {
            FileService service = getService();
            return service != null && service.upload(fileKey, fileName, bytes);

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return false;
        }
    }

}
