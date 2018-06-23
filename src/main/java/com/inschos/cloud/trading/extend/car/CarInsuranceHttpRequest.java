package com.inschos.cloud.trading.extend.car;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inschos.cloud.trading.assist.kit.HttpClientKit;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.cloud.trading.assist.kit.StringKit;
import org.apache.ibatis.jdbc.Null;
import org.springframework.http.RequestEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 创建日期：2018/3/29 on 15:35
 * 描述：
 * 作者：zhangyunhe
 */
public class CarInsuranceHttpRequest<RequestEntity extends CarInsuranceRequest, Response extends CarInsuranceResponse> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private CarInsuranceRequestEntity request;
    private Class<Response> cls;
    private String url;
    public long errorCode = 0L;

    public CarInsuranceHttpRequest(String url, RequestEntity requestEntity, Class<Response> cls) {
        this.url = url;
        this.request = new CarInsuranceRequestEntity<RequestEntity>();
        this.cls = cls;
        if (requestEntity != null) {
            this.request.data = requestEntity;
            this.request.sign = SignatureTools.sign(JsonKit.bean2Json(requestEntity), SignatureTools.CAR_RSA_PRIVATE_KEY);
        }

        this.request.sendTime = sdf.format(new Date(System.currentTimeMillis()));
    }

    public Response post() {
        Response response;
        try {
            L.log.debug("=============================================================================================================================");
            L.log.debug(JsonKit.bean2Json(request));
            String result = HttpClientKit.post(url, JsonKit.bean2Json(request));
            L.log.debug("=============================================================================================================================");
            L.log.debug(result);

            response = JsonKit.json2Bean(result, cls);

            if (response != null) {
                response.verify = verifySignature(result);
            } else {
                try {
                    response = cls.newInstance();
                    response.state = CarInsuranceResponse.RESULT_FAIL;
                    response.msg = "请求失败";
                    response.verify = false;
                } catch (InstantiationException | IllegalAccessException e) {
                    // e.printStackTrace();
                    errorCode = -1;
                    return null;
                }
            }
            return response;
        } catch (IOException e) {
            // e.printStackTrace();
            try {
                response = cls.newInstance();
                response.state = CarInsuranceResponse.RESULT_FAIL;
                response.msg = "请求失败";
                response.verify = false;
                return response;
            } catch (InstantiationException | IllegalAccessException ex) {
                // ex.printStackTrace();
                errorCode = -2;
                return null;
            }
        }
    }

    private boolean verifySignature(String responseJson) {
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(responseJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean flag = false;
        if (jsonNode != null) {
            JsonNode signNode = jsonNode.get("sign");
            String sign = signNode.textValue();
            if (StringKit.isEmpty(sign)) {
                flag = true;
            } else {
                String content = jsonNode.get("data").toString();
                flag = SignatureTools.verify(content, sign, SignatureTools.CAR_RSA_PUBLIC_KEY);
            }
        }
        return flag;
    }

}
