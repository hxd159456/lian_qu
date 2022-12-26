package com.cqupt.art.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.entity.TransferLog;
import com.cqupt.art.mapper.PmTransferLogMapper;
import com.cqupt.art.service.TransferLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
@Service
@Slf4j
public class TransferLogServiceImpl extends ServiceImpl<PmTransferLogMapper, TransferLog> implements TransferLogService {

    @Override
    public List<TransferLog> getByNftId(Long nftId) {
        Map<String, Object> map = new HashMap<>();
        map.put("nft_id", nftId);
        List<TransferLog> pmTransferLogs = this.baseMapper.selectByMap(map);
        return pmTransferLogs;
    }

    @Override
    public void updateStatus(String nftId, String fromUid, String toUid, Integer localId, String txHash) {
        log.info("更新状态：txHash==={}",txHash);
        TransferLog one = this.getOne(new QueryWrapper<TransferLog>().eq("nft_id", nftId).eq("from_uid", fromUid).eq("to_uid", toUid).eq("local_id", localId));
        one.setTxHash(txHash);
        this.updateById(one);
    }
}
