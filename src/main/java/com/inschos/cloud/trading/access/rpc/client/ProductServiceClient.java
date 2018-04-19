package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.ProductInfo;
import com.inschos.cloud.trading.access.rpc.service.ProductService;
import com.inschos.cloud.trading.assist.kit.L;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建日期：2018/4/19 on 14:53
 * 描述：
 * 作者：zhangyunhe
 */
@Component
public class ProductServiceClient extends BaseClientService {

    @Value("${rpc.remote.product.host}")
    private String host;

    private final String uri = "/rpc/account";

    private ProductService productService;

    private ProductService getProductService() {
        if (productService == null) {
            productService = getService(host + uri, ProductService.class);
        }
        return productService;
    }

    public List<ProductInfo> product_list() {
        try {
            ProductService service = getProductService();
            return service != null ? service.product_list() : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductInfo product_byId(String product_id) {
        try {
            ProductService service = getProductService();
            return service != null ? service.product_byId(product_id) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

}
