package com.inschos.cloud.trading.assist.kit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 创建日期：2018/5/29 on 20:03
 * 描述：
 * 作者：zhangyunhe
 */
public class IOKit {

    public static byte[] readStream(InputStream inStream) {
        if (inStream == null) {
            return null;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outStream.toByteArray();
    }

}


