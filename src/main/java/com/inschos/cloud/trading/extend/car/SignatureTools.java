package com.inschos.cloud.trading.extend.car;

import com.inschos.cloud.trading.assist.kit.ConstantKit;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignatureTools {
    private static Logger logger = LoggerFactory.getLogger(SignatureTools.class);
    //加密算法RSA
    public static final String KEY_ALGORITHM = "RSA";
    //签名算法
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    //签名算法
    public static final String CHARSET = "UTF-8";

    public static final String CAR_RSA_PUBLIC_KEY;
    public static final String CAR_RSA_PRIVATE_KEY;

    public static final String CAR_RSA_PUBLIC_KEY_DEBUG = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZuErBZ6HSxnCCkBnc7McR/1SFK7ArXAqxBVFiATJDN1HYfByAIuCdZiXJ6Eg4ES9RLbJLSB7BWpRxAASzk2lBRg8iZuSPTgEpVBBgiSEP4Xrd7J7mMoLqf1QcNylZp/EFL8iJzv1P04KZvcxKlDI49kdqaWJbx6aaBtdpbvhIAwIDAQAB";
    public static final String CAR_RSA_PUBLIC_KEY_PRODUCT = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBMaFczAHpsSq9MZNwpV9G4KFenKVS/SUpUTqDEgBXEL5rTh5YNgYeLIHczfuvsACHh+nH7Py5wzpofghY9EK/8h2Y92HPDv1hU2R3mlKxkB2mjLjtJH2lWAgqEbB413vHy8gHdRD47csgci5zO9Pimmumli65u/x8iw8f4DYrmQIDAQAB";
    public static final String CAR_RSA_PRIVATE_KEY_DEBUG = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJm4SsFnodLGcIKQGdzsxxH/VIUrsCtcCrEFUWIBMkM3Udh8HIAi4J1mJcnoSDgRL1EtsktIHsFalHEABLOTaUFGDyJm5I9OASlUEGCJIQ/het3snuYygup/VBw3KVmn8QUvyInO/U/Tgpm9zEqUMjj2R2ppYlvHppoG12lu+EgDAgMBAAECgYBzoqL5oo+lEwoH7YbHHocSDtTx6M95jp+sUqfJ5cR9s9up0pNOAO6e+PxsSoQpSSQjGREKeJJKOW15I95lh/qgqCPjOIe+bpcD2xkJ67L5rjVz6/snTb0DsfIPUnyYHzavD4GN+tkzZS6gsD3mUUPwAHyzNsSoWK3M3TW9VYtEwQJBANfGuP4F4jV2F412NECZZTVstFO9ou7WVYSDfbGhnktkbSWUklNh8zoUuZNhjKLsEuP59yn5XRxWqCIsdcw9MXsCQQC2YB1S9kb0KoOcCco4eC9uzofOWUe0ZUS0vvUcuKIwNbKoGW9JYypCY5rr924VR2NtG9nhAe9Kt4qlksg1KWkZAkB4J3KwEXqrpnzrCx2Bs2mGXGf2Ea1/Ld8lEUuW9JZ/CQc2XAs3X13fw9aq2TFFMAw84t7dgrx0oVSy4usdth2tAkBUpwB1hcMhiqpUP2cSFxdNQ6hd66sQ3QCrNQfpMPp3jjVDOasiUVlIP5ulc9AxKFXKS2cyvgcok1FT0XIE2xfhAkEAz4KuQHhxkfKrMV/4RSpAduB2V+6fP8X2UJgMeils8jUpNyRl3Y5ttI5vXs0bfcztAZ0qW+go1zylp05EAcDndg==";
    public static final String CAR_RSA_PRIVATE_KEY_PRODUCT = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIExoVzMAemxKr0xk3ClX0bgoV6cpVL9JSlROoMSAFcQvmtOHlg2Bh4sgdzN+6+wAIeH6cfs/LnDOmh+CFj0Qr/yHZj3Yc8O/WFTZHeaUrGQHaaMuO0kfaVYCCoRsHjXe8fLyAd1EPjtyyByLnM70+Kaa6aWLrm7/HyLDx/gNiuZAgMBAAECgYBvrD/H38vzfaHs4dKJd1jxAjBagNri7lBV85G5RDEpER8Xz/Go3CHTqTMjcqWQynjarh/lIbOeuuNc+Twr8A9VYKMjOVqy/ysO5dBYHjRoFTUKt6PUiwsdYz3zPEPKOVNwUyhBfZeASBJdLNXXDCFGHhEK+KPGHjdWFxD/iqU8gQJBAP+S9VkUNtVwLBSbCE82DrykjsEVzm+PVkO4rt1evvLitMbYXl1KGcSaa+KDBi9Rq5vdQYva9MRKJQfT9T8wCAkCQQCBaMBZKpocH42EPTfAcTfI8WwU8V3X0vZPVjwvZwNhnoDVi/ZzZW6ydCM2wvv9wvL9dRZ6XWQ94nNI8H2rE0sRAkBiB8qYOoq13+rolHbhe0i3zx76rRSb8g0SuNpKCzePDqpswz2e5vdvVBQhtERBDkCW7o1fycKtMAt4LzMY46GRAkAa52U95G5/tEuel3+UwbUAULjQrdF9wj01B0+h5z+7ttFAFEYbfZYDAcQkaWssObB84y/WJr5lY0PmgSEPRQaRAkA8FzM7j/E3T/7JR4luu6MshK3SYkH9s9g+dsAx8wR6KS4LBjfFcQL0Kl/H6ojL8zgzt97ppV/ocVPZ/69PSdma";

    static {
        if (ConstantKit.IS_PRODUCT) {
            CAR_RSA_PUBLIC_KEY = CAR_RSA_PUBLIC_KEY_PRODUCT;
            CAR_RSA_PRIVATE_KEY = CAR_RSA_PRIVATE_KEY_PRODUCT;
        } else {
            CAR_RSA_PUBLIC_KEY = CAR_RSA_PUBLIC_KEY_DEBUG;
            CAR_RSA_PRIVATE_KEY = CAR_RSA_PRIVATE_KEY_DEBUG;
        }
    }

    public static final String SIGN_CAR_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDnL1VkXh2prC6akgL9wYnhx6hDo8tqQ3AuA4k7Tr5qSIakli4vKonNunMO3AZ6J/P4ZmWAo1xHMBLlvBAAN0YoTayeKszHu+o30kUv5HCZdZiVtNCWXux0h0MhV962s91+vshUOg8eFj/RWM41iNd1LpXx0W3isZUK6eMOXftWuwIDAQAB";
    public static final String SIGN_CAR_RSA_PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOcvVWReHamsLpqSAv3BieHHqEOjy2pDcC4DiTtOvmpIhqSWLi8qic26cw7cBnon8/hmZYCjXEcwEuW8EAA3RihNrJ4qzMe76jfSRS/kcJl1mJW00JZe7HSHQyFX3raz3X6+yFQ6Dx4WP9FYzjWI13UulfHRbeKxlQrp4w5d+1a7AgMBAAECgYEAmBq1dS7TF2J42yv8GdbvkARb+fzXhhfOxAeBj+rUL3t+UuWYh2HWfuwQbZNoE5Eb3LAKUmOpABFqLYrZgDrdxcB/Mh4rBBfMMs/N/uR12M1UJVvAM7Jna4cG8We5RdKZsXiMCXwZqoe9bIO0kev6cazbOmnzgaoWPw4kdVUwHRECQQD32gAsk7qoehSvggU4nCladGrys04xPH2ntl1hQPLVRvHYBQ3B/8IQM5hgoxGDz4uBif1JsbEJ0tSblUUhrXyTAkEA7skPj5XBtkcmbkt1p06uLUqKQyu65w82hZOvfo2VWPKiPN7xF+D2q3FMp+qzvBkRJ9ZmvYZ9V+TUjMbGSXU+OQJAedI4y7BAypZWnH03u79lxAP8nRXslN66lDhaZXba5GFedWLmhDgFVplFiBoefb8BPsZoLFSeQ8nQnjgcJpVy6QJBAIOVBCApdA+P8ZuBtZm2f8CxfLM7G8lL1s2Q7nocZxoWylQkvlJcQ1GCI628ZcLXRV7ghMkXnWab0Iyq1IWSN/ECQQDNjQXKhvk7f/zmHR/I8YuK/R2o0MsVjB9AvbECzupdaKIXDwvcxj1ygoqG6CpEkcPedsoM5MuneR35IQ8qZ7Tp";

    public static final String SIGN_TASK_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyF3iNNA6HyAi6SyEjpTSpmHwuBR7nq3sbQ5Pxn4+wTVSJBup+ACHCyu+BsfqU+WBlb2FG4CArYrXh+dzLAO0fd2z2jhbEivpS5XtncUz2ePTFal08bbJd9yYIa225zRcNY8i6njK5IRahx7EjjZw4EKOz1nURCfeXplTwZe8PzwIDAQAB";
    public static final String SIGN_TASK_RSA_PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALIXeI00DofICLpLISOlNKmYfC4FHuerextDk/Gfj7BNVIkG6n4AIcLK74Gx+pT5YGVvYUbgICtiteH53MsA7R93bPaOFsSK+lLle2dxTPZ49MVqXTxtsl33JghrbbnNFw1jyLqeMrkhFqHHsSONnDgQo7PWdREJ95emVPBl7w/PAgMBAAECgYEAp9jVJogEldZi22mJnzXKL3lmuFzdJs2IpkaHvfd7jlcjsE3TXdOz/goTt3HN0PBD+PYDgbAkwZ8z+vnqLW3/DNEuCrBHdrsLH+suM82lNA8VThwpXXLihvCyQFWmWKtIfgEVpzHPjXG10oJ2yxbfYmqG8E0BaNU49jVdW9uWgGkCQQDfwiNvpqBtBD41REmgyvVMuenVmrEjACB4usMvv5Mm7UWbTqrmxwmw6xQPdFssLg3mNAtfrj4WWCEtzhGQE+7dAkEAy8DO6BoDDrr8teSVv2UMJHHgaWnljmC3bFGYLwhB8E9AKB6Dnle5TA6bU0LXAQ/k978C7bYmpB8zsydzziQwmwJAJSS1+y++y7n7lZ4LVty6EY1/Co1OQFAiE8h05DU5+SlD9778UajSrAKp5tLa25+dgQw8oxpqhg0FHPRrJ+6XJQJBALs3bGaEEC8mzTFs2dPFS0TpheQWM6GBaMiUqJr+oWfnyfGTKCkEPfSOr1Xv4pLNQfwKfOxjJFpFeUGyjIvrYOUCQFuwgUOEdrH0ZxQErJUb0pIRWSal6B0H1oF4CqM1+eC7X3KqMCFIhKJmmCqpDNH9ZA2yDhO+FyLHS/keSNPCibY=";

    /**
     * 获取私钥对象
     *
     * @return PrivateKey
     */
    private static PrivateKey getPrivateKey(String key) {
        try {
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取公钥对象
     *
     * @return PublicKey
     */
    private static PublicKey getPublicKey(String key) {
        try {
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 签名
     *
     * @param content 签名内容，即请求报文中的data节点
     * @return 签名字符串
     */
    public static String sign(String content, String key) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(getPrivateKey(key));
            signature.update(content.getBytes(CHARSET));
            return new BASE64Encoder().encodeBuffer(signature.sign());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 验签
     *
     * @param content 签名内容，即返回报文中的data节点
     * @param sign    签名字符串
     * @return true-验签通过
     */
    public static boolean verify(String content, String sign, String key) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(getPublicKey(key));
            signature.update(content.getBytes(CHARSET));
            return signature.verify(new BASE64Decoder().decodeBuffer(sign));
        } catch (Exception ex) {
            logger.error("签名校验异常", ex);
            return false;
        }
    }

}
