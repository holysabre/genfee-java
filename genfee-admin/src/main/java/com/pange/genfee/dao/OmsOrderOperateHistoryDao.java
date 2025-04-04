package com.pange.genfee.dao;

import com.pange.genfee.model.OmsOrderOperateHistory;

import java.util.List;

public interface OmsOrderOperateHistoryDao {
    /**
     * 批量插入
     */
    void insertList(List<OmsOrderOperateHistory> list);
}
