package com.cqupt.art.controller;


import com.cqupt.art.entity.TransferLog;
import com.cqupt.art.service.TransferLogService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class TransferLogController {

    @Autowired
    private TransferLogService transferLogService;

    @PostMapping("/transferLog")
    public R saveTransferLog(@RequestBody TransferLog transferLog){
        transferLogService.save(transferLog);
        return R.ok();
    }

    @GetMapping("updateStatus")
    public R updateLogStatus(@RequestParam String nftId,
                             @RequestParam String fromUid,
                             @RequestParam String toUid,
                             @RequestParam String txHash,
                             @RequestParam Integer localId){
//        TransferLog byId = transferLogService.getById(transferLogId);
//        byId.setTxHash(txHash);
        transferLogService.updateStatus(nftId,fromUid,toUid,localId,txHash);
        log.info("更新交易hash成功！交易hash为：{}",txHash);
        return R.ok("更新交易hash成功！");
    }

}
