package com.cqupt.art.chain.channel;

import com.cqupt.art.chain.entity.AccountInfo;
import com.cqupt.art.chain.entity.NftMetadata;
import com.cqupt.art.chain.entity.to.CreateNftBatchResultTo;
import com.cqupt.art.chain.entity.to.UserTransferTo;

import java.math.BigInteger;
import java.util.List;

/**
 * 桥接模式，依赖抽象
 * @author hxd
 * @create 2023-05-21 21:00
 */
public interface ChainOperation {
    // 批量铸造NFT，一次交易
    CreateNftBatchResultTo createNftBatch(int num, NftMetadata metadata, String authorName) throws Exception;

    //批量铸造NFT，多次交易
    List<String> creatNftBatch(int num, NftMetadata metadata) throws Exception;

    //为用户设置允许转增特定nft
    String approve(String to, BigInteger tokenId) throws Exception;

    //允许用户转增所有的已拥有nft
    String setApproveAll(String to, boolean isApproveAll) throws Exception;

    //链上资产转移（转增、交易完成后转到用户钱包）
    String transfer(String from, String to, BigInteger tokenId) throws Exception;

    BigInteger totalSupply();

    String adminTransfer(String toAddress, BigInteger tokenId);

    String adminTransferBatch(List<String> addressList, List<BigInteger> tokenIds);

    String userTransfer(UserTransferTo to) throws Exception;

    AccountInfo createAccount(String pwd) throws Exception;
}
