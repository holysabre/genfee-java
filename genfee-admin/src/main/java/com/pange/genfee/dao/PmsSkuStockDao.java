package com.pange.genfee.dao;

import com.pange.genfee.model.PmsSkuStock;

import java.util.List;

public interface PmsSkuStockDao {

    int insertList(List<PmsSkuStock> list);
}
