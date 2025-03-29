package com.pange.genfee.portal.service;

import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.model.PmsBrand;
import com.pange.genfee.model.PmsProduct;

import java.util.List;

public interface PmsPortalBrandService {

    List<PmsBrand> recommandList(Integer pageNum,Integer pageSize);

    PmsBrand detail(Long brandId);

    CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize);
}
