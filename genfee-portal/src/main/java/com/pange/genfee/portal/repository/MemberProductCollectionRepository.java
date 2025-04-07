package com.pange.genfee.portal.repository;

import com.pange.genfee.portal.domain.MemberProductCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberProductCollectionRepository extends MongoRepository<MemberProductCollection, String> {

    /**
     * 更具会员ID和商品ID查询
     */
    MemberProductCollection findByMemberIdAndProductId(Long memberId, Long productId);

    /**
     * 根据会员ID和商品ID删除
     */
    int deleteByMemberIdAndProductId(Long memberId, Long productId);

    /**
     * 根据会员ID分页查询记录
     */
    Page<MemberProductCollection> findByMemberId(Long memberId, Pageable pageable);

    /**
     * 根据会员ID删除记录
     */
    int deleteByMemberId(Long memberId);
}
