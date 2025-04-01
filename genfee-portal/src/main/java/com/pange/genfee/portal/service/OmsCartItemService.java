package com.pange.genfee.portal.service;

import com.pange.genfee.model.OmsCartItem;
import com.pange.genfee.portal.domain.CartProduct;
import com.pange.genfee.portal.domain.CartPromotionItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OmsCartItemService {

    //购物车列表
    List<OmsCartItem> list(Long memberId);

    //添加购物车
    @Transactional
    int add(OmsCartItem cartItem);

    //修改购物车中的数量
    int updateQuantity(Long id, Long memberId,Integer quantity);

    //删除购物车
    int delete(Long memberId, List<Long> ids);

    //清空购物车
    int clear(Long memberId);

    //获取购物车商品信息
    CartProduct getCartProduct(Long productId);

    //修改购物车商品的规格
    @Transactional
    int updateAttr(OmsCartItem cartItem);

    //获取包含促销活动信息的购物车列表
    List<CartPromotionItem> listPromotion(Long memberId, List<Long> cartIds);
}
