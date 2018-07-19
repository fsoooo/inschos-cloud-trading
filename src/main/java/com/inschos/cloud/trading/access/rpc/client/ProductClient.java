package com.inschos.cloud.trading.access.rpc.client;

import com.inschos.cloud.trading.access.rpc.bean.*;
import com.inschos.cloud.trading.access.rpc.service.ProductService;
import com.inschos.common.assist.kit.L;
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

    public PayCategoryBean getOnePayCategory(long pagCategoryId) {
        try {
            ProductService service = getService();
            return service != null ? service.getOnePayCategory(pagCategoryId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<ProductCategory> getCategoryList(String level) {
        try {
            ProductService service = getService();
            return service != null ? service.getCategoryList(level) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<PayCategoryBean> getListPayCategory(long productId) {
        try {
            ProductService service = getService();
            return service != null ? service.getListPayCategory(productId) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<InsuranceCo> getProductCoList(String managerUuid) {
        try {
            ProductService service = getService();
            return service != null ? service.getProductCoList(managerUuid) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    //保费试算的rpc
    public ProductBean getPremium(InsureBean search) {
        try {
            ProductService service = getService();
            return service != null ? service.getPremium(search) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }


    public List<ProductBean> getListProduct(String name, String managerUuid) {
        try {
            ProductService service = getService();
            return service != null ? service.getListProduct(name, managerUuid) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductBean getProductByCode(String code) {
        try {
            ProductService service = getService();
            return service != null ? service.getProductByCode(code) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public ProductPayCategoryBean getProductPayCategory(long id) {
        try {
            ProductService service = getService();
            return service != null ? service.getProductPayCategory(id) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<ProductPayCategoryBean> listProductPayCategory(List<String> id) {
        try {
            ProductService service = getService();
            return service != null ? service.listProductPayCategory(id) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

    public List<ProductBean> getProductByAutomobileList(String managerUuid) {
        try {
            ProductService service = getService();
            return service != null ? service.getProductByAutomobileList(managerUuid) : null;

        } catch (Exception e) {
            L.log.error("remote fail {}", e.getMessage(), e);
            return null;
        }
    }

}
