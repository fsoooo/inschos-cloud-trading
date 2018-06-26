package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.service.ProductService;
import com.inschos.cloud.trading.assist.kit.L;
import com.inschos.dock.bean.InsureBean;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 14:53
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class ProductClient {

    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/api/rpc/product";


    private ProductService getService() {
        return new HproseHttpClient(host + uri).useService(ProductService.class);
    }


    public List<ProductBean> getPlatformProductAll(String managerUuid, int categoryId) {
        try {
            ProductService service = getService();
            return service != null ? service.getPlatformProductAll(managerUuid, categoryId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductBean getProduct(long productId) {
        try {
            ProductService service = getService();
            return service != null ? service.getProduct(productId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductBrokerageInfoBean getBrokerage(ProductBrokerageBean search) {
        try {
            ProductService service = getService();
            return service != null ? service.getBrokerage(search) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<ProductBean> getProductList(List<String> ids) {
        try {
            ProductService service = getService();
            return service != null ? service.getProductList(ids) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public PayCategoryBean getOnePayCategory(long pagCategoryId){
        try {
            ProductService service = getService();
            return service != null ? service.getOnePayCategory(pagCategoryId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<PayCategoryBean> getListPayCategory(long productId){
        try {
            ProductService service = getService();
            return service != null ? service.getListPayCategory(productId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    //保费试算的rpc
    public ProductBean getPremium(InsureBean search){
        try {
            ProductService service = getService();
            return service != null ? service.getPremium(search) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }


}
