package com.pange.genfee.service.impl;

import com.github.pagehelper.PageHelper;
import com.pange.genfee.dao.OmsOrderDao;
import com.pange.genfee.dao.OmsOrderOperateHistoryDao;
import com.pange.genfee.dto.*;
import com.pange.genfee.mapper.OmsOrderMapper;
import com.pange.genfee.mapper.OmsOrderOperateHistoryMapper;
import com.pange.genfee.model.OmsOrder;
import com.pange.genfee.model.OmsOrderExample;
import com.pange.genfee.model.OmsOrderOperateHistory;
import com.pange.genfee.service.OmsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/4/3}
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;

    @Override
    public List<OmsOrder> list(OmsOrderQueryParam orderQueryParam, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return orderDao.getList(orderQueryParam);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        return orderDao.getDetail(id);
    }

    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParams) {
        int count = orderDao.delivery(deliveryParams);
        List<OmsOrderOperateHistory> historyList = deliveryParams.stream()
                .map(item -> {
                    OmsOrderOperateHistory operateHistory = new OmsOrderOperateHistory();
                    operateHistory.setOrderId(item.getOrderId());
                    operateHistory.setOperateMan("管理员");
                    operateHistory.setNote("发货");
                    operateHistory.setOrderStatus(2);
                    operateHistory.setCreateTime(new Date());
                    return operateHistory;
                })
                .collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder order = new OmsOrder();
        order.setStatus(4);
        OmsOrderExample orderExample = new OmsOrderExample();
        orderExample.createCriteria().andIdIn(ids);
        int count = orderMapper.updateByExample(order,orderExample);
        List<OmsOrderOperateHistory> historyList = ids.stream()
                .map(item -> {
                    OmsOrderOperateHistory operateHistory = new OmsOrderOperateHistory();
                    operateHistory.setOrderId(item);
                    operateHistory.setOperateMan("管理员");
                    operateHistory.setNote(note);
                    operateHistory.setOrderStatus(4);
                    operateHistory.setCreateTime(new Date());
                    return operateHistory;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrder order = new OmsOrder();
        order.setDeleteStatus(1);
        OmsOrderExample orderExample = new OmsOrderExample();
        orderExample.createCriteria().andIdIn(ids);
        return orderMapper.updateByExample(order,orderExample);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = orderMapper.selectByPrimaryKey(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setStatus(moneyInfoParam.getStatus());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);

        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);

        return count;
    }

    @Override
    public int updateOrderNote(Long id, String note, Integer status) {
        OmsOrder order = orderMapper.selectByPrimaryKey(id);
        order.setStatus(status);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);

        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status);
        history.setNote("修改备注");
        orderOperateHistoryMapper.insert(history);

        return count;
    }
}
