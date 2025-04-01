package com.pange.genfee.portal.service;

import com.pange.genfee.model.OmsCartItem;
import com.pange.genfee.portal.domain.CartPromotionItem;

import java.util.List;

public interface OmsPromotionService {

    /**
     * 计算购物车中的促销活动信息
     * @param cartItemList 购物车信息
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
