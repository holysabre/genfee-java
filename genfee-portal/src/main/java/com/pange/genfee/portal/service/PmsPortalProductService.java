package com.pange.genfee.portal.service;

import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.portal.domain.PmsPortalProductDetail;
import com.pange.genfee.portal.domain.PmsProductCategoryNode;

import java.util.List;

public interface PmsPortalProductService {

    List<PmsProduct> search(String keyword,Long brandId,Long productCategoryId, Integer pageNum,Integer pageSize, Integer sort);

    List<PmsProductCategoryNode> categoryTreeList();

    PmsPortalProductDetail detail(Long id);
}
