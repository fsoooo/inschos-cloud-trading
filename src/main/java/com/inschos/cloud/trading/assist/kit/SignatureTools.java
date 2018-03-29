package com.inschos.cloud.trading.assist.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.*;
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

    private static final String CAR_RSA_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZuErBZ6HSxnCCkBnc7McR/1SFK7ArXAqxBVFiATJDN1HYfByAIuCdZiXJ6Eg4ES9RLbJLSB7BWpRxAASzk2lBRg8iZuSPTgEpVBBgiSEP4Xrd7J7mMoLqf1QcNylZp/EFL8iJzv1P04KZvcxKlDI49kdqaWJbx6aaBtdpbvhIAwIDAQAB";
    private static final String CAR_RSA_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJm4SsFnodLGcIKQGdzsxxH/VIUrsCtcCrEFUWIBMkM3Udh8HIAi4J1mJcnoSDgRL1EtsktIHsFalHEABLOTaUFGDyJm5I9OASlUEGCJIQ/het3snuYygup/VBw3KVmn8QUvyInO/U/Tgpm9zEqUMjj2R2ppYlvHppoG12lu+EgDAgMBAAECgYBzoqL5oo+lEwoH7YbHHocSDtTx6M95jp+sUqfJ5cR9s9up0pNOAO6e+PxsSoQpSSQjGREKeJJKOW15I95lh/qgqCPjOIe+bpcD2xkJ67L5rjVz6/snTb0DsfIPUnyYHzavD4GN+tkzZS6gsD3mUUPwAHyzNsSoWK3M3TW9VYtEwQJBANfGuP4F4jV2F412NECZZTVstFO9ou7WVYSDfbGhnktkbSWUklNh8zoUuZNhjKLsEuP59yn5XRxWqCIsdcw9MXsCQQC2YB1S9kb0KoOcCco4eC9uzofOWUe0ZUS0vvUcuKIwNbKoGW9JYypCY5rr924VR2NtG9nhAe9Kt4qlksg1KWkZAkB4J3KwEXqrpnzrCx2Bs2mGXGf2Ea1/Ld8lEUuW9JZ/CQc2XAs3X13fw9aq2TFFMAw84t7dgrx0oVSy4usdth2tAkBUpwB1hcMhiqpUP2cSFxdNQ6hd66sQ3QCrNQfpMPp3jjVDOasiUVlIP5ulc9AxKFXKS2cyvgcok1FT0XIE2xfhAkEAz4KuQHhxkfKrMV/4RSpAduB2V+6fP8X2UJgMeils8jUpNyRl3Y5ttI5vXs0bfcztAZ0qW+go1zylp05EAcDndg==";

    /**
     * 获取私钥对象
     * @return PrivateKey
     */
    private static PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(CAR_RSA_PRIVATE_KEY);
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
     * @return PublicKey
     */
    private static PublicKey getPublicKey() {
        try {
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(CAR_RSA_PUBLIC_KEY);
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
     * @param content 签名内容，即请求报文中的data节点
     * @return 签名字符串
     */
    public static String sign(String content) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(getPrivateKey());
            signature.update(content.getBytes(CHARSET));
            return new BASE64Encoder().encodeBuffer(signature.sign());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 验签
     * @param content 签名内容，即返回报文中的data节点
     * @param sign 签名字符串
     * @return true-验签通过
     */
    public static boolean verify(String content, String sign) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(getPublicKey());
            signature.update(content.getBytes(CHARSET));
            return signature.verify(new BASE64Decoder().decodeBuffer(sign));
        } catch (Exception ex) {
            logger.error("签名校验异常", ex);
            return false;
        }
    }

}
