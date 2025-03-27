package com.pange.genfee.service;

import com.pange.genfee.dto.PmsProductAttributeCategoryItem;
import com.pange.genfee.model.PmsProductAttributeCategory;

import java.util.List;

public interface PmsProductAttributeCategoryService {

    List<PmsProductAttributeCategory> getList(int pageNum, int pangeSize);

    PmsProductAttributeCategory getItem(Long id);

    int create(String name);

    int update(Long id, String name);

    int delete(Long id);

    List<PmsProductAttributeCategoryItem> getListWithAttribute();
}
