package com.inschos.cloud.trading.extend.other;

import com.inschos.cloud.trading.extend.car.SignatureTools;
import com.inschos.common.assist.kit.HttpClientKit;
import com.inschos.common.assist.kit.JsonKit;

import java.io.IOException;

/**
 * 创建日期：2018/4/3 on 16:03
 * 描述：
 * 作者：zhangyunhe
 */
public class OtherInsuranceHttpRequest<Request extends OtherInsuranceRequest, Response extends OtherInsuranceResponse> {

    private String url;
    private OtherInsuranceRequestEntity<Request> request;
    private Class<Response> cls;


    public OtherInsuranceHttpRequest(String url, Request request, Class<Response> cls) {
        this.url = url;
        this.request = new OtherInsuranceRequestEntity<Request>();
        this.cls = cls;
        if (request != null) {
            this.request.data = request;
            this.request.sign = SignatureTools.sign(JsonKit.bean2Json(request), SignatureTools.CAR_RSA_PRIVATE_KEY);
        }
    }

    public Response post() {
        Response response;
        try {
            String result = HttpClientKit.post(url, JsonKit.bean2Json(request));
            response = JsonKit.json2Bean(result, cls);

            if (response != null) {
                response.code = response.status ? OtherInsuranceResponse.RESULT_OK : OtherInsuranceResponse.RESULT_FAIL;
                if (response.code != OtherInsuranceResponse.RESULT_OK) {
                    response.code = OtherInsuranceResponse.RESULT_FAIL;
                    response.msg = "请求失败";
                }
            } else {
                response = (Response) new OtherInsuranceResponse();
                response.status = false;
                response.code = OtherInsuranceResponse.RESULT_FAIL;
                response.msg = "请求失败";
            }
            return response;
        } catch (IOException e) {
            // e.printStackTrace();
            response = (Response) new OtherInsuranceResponse();
            response.status = false;
            response.code = OtherInsuranceResponse.RESULT_FAIL;
            response.msg = "请求失败";
            return response;
        }
    }

}
