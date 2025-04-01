package com.pange.genfee.portal.dao;

import com.pange.genfee.portal.domain.CartProduct;
import com.pange.genfee.portal.domain.PromotionProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PortalProductDao {
    /**
     * 获取购物车商品信息
     * @param productId 商品id
     */
    CartProduct getCartProduct(@Param("productId") Long productId);

    /**
     * 获取促销商品信息列表
     * @param ids 商品ids列表
     */
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);
}
