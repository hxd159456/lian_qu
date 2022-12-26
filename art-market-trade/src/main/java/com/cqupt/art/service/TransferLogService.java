package com.cqupt.art.service;

import com.cqupt.art.entity.TransferLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
public interface TransferLogService extends IService<TransferLog> {

    List<TransferLog> getByNftId(Long nftId);

    void updateStatus(String nftId, String fromUid, String toUid, Integer localId, String txHash);
}
