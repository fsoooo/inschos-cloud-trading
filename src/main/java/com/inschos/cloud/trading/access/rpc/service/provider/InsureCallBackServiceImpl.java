package com.inschos.cloud.trading.access.rpc.service.provider;

import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.dock.api.CallBackService;
import com.inschos.dock.bean.RpcResponse;
import com.inschos.dock.bean.RspPayBean;
import org.springframework.stereotype.Service;

/**
 * Created by IceAnt on 2018/6/25.
 */
@Service
public class InsureCallBackServiceImpl implements CallBackService {

    @Override
    public void outPolicy(RpcResponse<RspPayBean> payBean) {
        L.log.info(JsonKit.bean2Json(payBean));
    }
}
