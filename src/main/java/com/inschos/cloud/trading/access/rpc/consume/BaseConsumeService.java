package com.inschos.cloud.trading.access.rpc.consume;

import com.alibaba.dubbo.rpc.service.GenericService;
import com.inschos.cloud.trading.assist.kit.JsonKit;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.cloud.trading.assist.kit.StringKit;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by IceAnt on 2018/3/22.
 */
public abstract class BaseConsumeService {

    private String beanName;

    private static GenericService genericService;


    abstract protected String getBeanName();


    private GenericService initGenericService(){
        if(genericService==null){
            beanName = this.getBeanName();
            if(!StringKit.isEmpty(beanName)){
                try {
                    WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
                    genericService = (GenericService) wac.getBean(beanName);
                }catch (Exception e){
                    L.log.error("consumer init failed ",e);
                }
            }
        }
        return genericService;
    }
    <T> T invoke(String methodName, Class<T> clazz, Object... parameters){

        this.initGenericService();
        L.log.debug("开始 rpc method");
        if(genericService!=null){
            try {
                int len = 0;
                if(parameters!=null){
                    len = parameters.length;
                }

                String[] invokeParamTyeps = new String[len];
                Object[] invokeParams = new Object[len];
                for(int i = 0; i < len; i++){
                    Object o = parameters[i];

                    invokeParamTyeps[i] = o.getClass().getName();
                    invokeParams[i] = o;
                }

                if(L.log.isDebugEnabled()){
                    L.log.debug(JsonKit.bean2Json(genericService));
                    L.log.debug(JsonKit.bean2Json(invokeParamTyeps));
                    L.log.debug(JsonKit.bean2Json(invokeParams));
                }

                Object result = genericService.$invoke(methodName, invokeParamTyeps, invokeParams);
                T t = (T)result;
                return t;
            }catch (Exception e){
                L.log.error("consumer invoke 【"+methodName+"】failed ",e);
            }

        }
        return null;
    }



}
