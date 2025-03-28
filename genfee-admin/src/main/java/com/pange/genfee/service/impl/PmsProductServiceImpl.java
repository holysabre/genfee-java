package com.pange.genfee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.pange.genfee.dao.PmsProductAttributeValueDao;
import com.pange.genfee.dao.PmsProductDao;
import com.pange.genfee.dao.PmsSkuStockDao;
import com.pange.genfee.dto.PmsProductParam;
import com.pange.genfee.dto.PmsProductQueryParam;
import com.pange.genfee.dto.PmsProductResult;
import com.pange.genfee.mapper.PmsProductAttributeValueMapper;
import com.pange.genfee.mapper.PmsProductMapper;
import com.pange.genfee.mapper.PmsSkuStockMapper;
import com.pange.genfee.model.*;
import com.pange.genfee.service.PmsProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/3/27}
 */
@Service
public class PmsProductServiceImpl implements PmsProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductServiceImpl.class);

    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsProductAttributeValueDao productAttributeValueDao;
    @Autowired
    private PmsSkuStockDao skuStockDao;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductDao productDao;

    @Override
    public List<PmsProduct> getList(PmsProductQueryParam productQueryParam, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public List<PmsProduct> getList(String keyword) {
        return null;
    }

    @Override
    public int create(PmsProductParam productParam) {
        PmsProduct product = new PmsProduct();
        BeanUtil.copyProperties(productParam,product);
        product.setId((Long) null);

        int count = productMapper.insert(product);
        Long productId = product.getId();

        //sku处理
        handleSkuStockCode(productParam.getSkuStockList(),productId);
        relateAndInsertList(skuStockDao,productParam.getSkuStockList(),productId);

        //插入商品属性关联
        relateAndInsertList(productAttributeValueDao,productParam.getProductAttributeValueList(),productId);

        return count;
    }

    // 处理sku编码
    private void handleSkuStockCode(List<PmsSkuStock> skuStockList, Long productId){
        if(CollectionUtil.isEmpty(skuStockList)){
            return;
        }
        for (int i=0;i< skuStockList.size();i++){
            PmsSkuStock skuStock = skuStockList.get(i);
            if(StrUtil.isNotEmpty(skuStock.getSkuCode())){
                continue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            StringBuilder sb = new StringBuilder();
            sb.append(sdf.format(new Date()));
            sb.append(String.format("%04d",productId));
            sb.append(String.format("%03d",i));
            skuStock.setSkuCode(sb.toString());
        }
    }

    /**
     * 插入关联关系表
     * @param dao 操作的dao
     * @param dataList 插入的数据
     * @param productId 产品ID
     */
    private void relateAndInsertList(Object dao, List dataList, Long productId){
        try {
            if (CollectionUtil.isEmpty(dataList)) {
                return;
            }
            for (Object item : dataList) {
                Method setId = item.getClass().getMethod("setId", Long.class);
                setId.invoke(item,(Long) null);
                Method setProductId = item.getClass().getMethod("setProductId", Long.class);
                setProductId.invoke(item,productId);
            }
            Method insertList = dao.getClass().getMethod("insertList",List.class);
            insertList.invoke(dao,dataList);
        }catch (Exception e){
            LOGGER.warn("新建产品错误: {}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PmsProductResult getUpdateInfo(Long id) {
        return productDao.getUpdateInfo(id);
    }

    @Override
    public int update(Long id,PmsProductParam productParam) {
        PmsProduct product = new PmsProduct();
        BeanUtil.copyProperties(productParam,product);
        product.setId(id);
        int count = productMapper.updateByPrimaryKeySelective(product);

        handleUpdateSkuStock(id,productParam.getSkuStockList());

        PmsProductAttributeValueExample productAttributeValueExample = new PmsProductAttributeValueExample();
        productAttributeValueExample.createCriteria().andProductIdEqualTo(product.getId());
        productAttributeValueMapper.deleteByExample(productAttributeValueExample);
        relateAndInsertList(productAttributeValueDao,productParam.getProductAttributeValueList(),id);

        return count;
    }

    private void handleUpdateSkuStock(Long productId, List<PmsSkuStock> currSkuStockList){
        //没有sku时 直接删除
        if(CollectionUtil.isEmpty(currSkuStockList)){
            PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
            skuStockExample.createCriteria().andProductIdEqualTo(productId);
            skuStockMapper.deleteByExample(skuStockExample);
        }

        //获取原始数据
        PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
        skuStockExample.createCriteria().andProductIdEqualTo(productId);
        List<PmsSkuStock> oriSkuStockList = skuStockMapper.selectByExample(skuStockExample);

        //新增的列表
        List<PmsSkuStock> inertingList = currSkuStockList.stream().filter(item -> item.getId() == null).collect(Collectors.toList());
        //修改的列表
        List<PmsSkuStock> updatingList = currSkuStockList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());
        List<Long> updatingIdList = currSkuStockList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
        //删除的列表
        List<PmsSkuStock> deletingList = oriSkuStockList.stream().filter(item -> !updatingIdList.contains(item.getId())).collect(Collectors.toList());
        handleSkuStockCode(inertingList,productId);
        handleSkuStockCode(updatingList,productId);
        //新增sku
        if(CollectionUtil.isNotEmpty(inertingList)){
            relateAndInsertList(skuStockDao,inertingList,productId);
        }
        //修改sku
        if(CollectionUtil.isNotEmpty(updatingIdList)){
            for (PmsSkuStock skuStock : updatingList){
                skuStockMapper.updateByPrimaryKeySelective(skuStock);
            }
        }
        //删除sku
        if(CollectionUtil.isNotEmpty(deletingList)){
            List<Long> deleteIds = deletingList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
            PmsSkuStockExample delSkuStockExample = new PmsSkuStockExample();
            delSkuStockExample.createCriteria().andIdIn(deleteIds);
            skuStockMapper.deleteByExample(delSkuStockExample);
        }
    }

    @Override
    public int delete(Long id) {
        PmsProduct product = new PmsProduct();
        product.setId(id);
        product.setDeleteStatus(1);
        return productMapper.updateByPrimaryKeySelective(product);
    }
}
