package com.pange.genfee.service;

import com.pange.genfee.dto.PmsBrandParam;
import com.pange.genfee.model.PmsBrand;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PmsBrandService {

    List<PmsBrand> listAll();

    List<PmsBrand> list(String keyword, Integer showStatus, int pageNum,int pageSize);

    int create(PmsBrandParam brandParam);

    @Transactional
    int update(Long id, PmsBrandParam brandParam);

    int delete(Long id);

    int delete(List<Long> ids);

    PmsBrand getItem(Long id);

    int updateShowStatus(List<Long> ids, int showStatus);

    int updateFactoryStatus(List<Long> ids, int factoryStatus);
}
