package com.pange.genfee.service;

import com.pange.genfee.dto.PmsProductParam;
import com.pange.genfee.dto.PmsProductQueryParam;
import com.pange.genfee.dto.PmsProductResult;
import com.pange.genfee.model.PmsProduct;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PmsProductService {

    List<PmsProduct> getList(PmsProductQueryParam productQueryParam, Integer pageNum, Integer pageSize);

    List<PmsProduct> getList(String keyword);

    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED)
    int create(PmsProductParam productParam);

    /**
     * 根据商品id获取更新信息
     */
    PmsProductResult getUpdateInfo(Long id);

    @Transactional
    int update(Long id,PmsProductParam productParam);

    int delete(Long id);
}
