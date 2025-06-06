package com.pange.genfee.portal.service;

import com.pange.genfee.portal.domain.MemberProductCollection;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 会员商品收藏
 * @auther Pange
 * @description
 * @date {2025/4/5}
 */
public interface MemberCollectionService {

    /**
     * 添加收藏
     */
    int add(MemberProductCollection productCollection);

    /**
     * 删除收藏
     */
    int delete(Long productId);

    /**
     * 分页查询收藏
     */
    Page<MemberProductCollection> list(Integer pageNum, Integer pageSize);

    /**
     * 查看详情
     */
    MemberProductCollection detail(Long productId);

    /**
     * 清空收藏
     */
    void clear();
}
