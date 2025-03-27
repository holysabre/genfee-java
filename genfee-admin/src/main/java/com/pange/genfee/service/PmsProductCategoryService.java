package com.pange.genfee.service;

import com.pange.genfee.dto.PmsProductCategoryParam;
import com.pange.genfee.dto.PmsProductCategoryWithChildrenItem;
import com.pange.genfee.model.PmsProductCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PmsProductCategoryService {
    List<PmsProductCategory> getList(Long parentId,int pageNum,int pageSize);

    PmsProductCategory getItem(Long id);

    @Transactional
    int create(PmsProductCategoryParam productCategoryParam);

    @Transactional
    int update(Long id, PmsProductCategoryParam productCategoryParam);

    int delete(Long id);

    int updateNavStatus(List<Long> ids, Integer navStatus);

    int updateShowStatus(List<Long> ids, Integer showStatus);

    List<PmsProductCategoryWithChildrenItem> listWithChildren();
}
