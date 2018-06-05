package com.inschos.cloud.trading.extend.file;

import org.springframework.beans.factory.annotation.Value;

/**
 * 创建日期：2018/4/23 on 15:30
 * 描述：
 * 作者：zhangyunhe
 */
public class FileUploadCommon {

    private static final String SERVER_HOST = "http://122.14.202.146:9200/file";

    public static final String upload_by_base64 = getServerHost() + "/file/upBase";

    public static final String get_file_url = getServerHost() + "/file/getFileUrl";

    public static String getServerHost() {
        return SERVER_HOST;
    }
}
