package com.cqupt.art.chain.channel.conflux;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.chain.channel.ChainOperation;
import com.cqupt.art.chain.entity.AccountInfo;
import com.cqupt.art.chain.entity.NftMetadata;
import com.cqupt.art.chain.entity.to.CreateNftBatchResultTo;
import com.cqupt.art.chain.entity.to.UserTransferTo;
import com.cqupt.art.chain.utils.AliOssUtil;
import com.cqupt.art.chain.utils.PingYinUtil;
import conflux.web3j.Account;
import conflux.web3j.AccountManager;
import conflux.web3j.Cfx;
import conflux.web3j.CfxUnit;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hxd
 * @create 2023-05-21 21:03
 */
@Service(value = "conflux")
@Slf4j
public class ConfluxOperation implements ChainOperation {

    @Value("${my.chain.contract}")
    private String contractAddress;

    @Autowired
    private Cfx cfx;

    @Autowired
    private Account account;

    @Autowired
    private ContractCall contractCall;

    @Autowired
    AccountManager accountManager;

    @Override
    public CreateNftBatchResultTo createNftBatch(int num, NftMetadata metadata, String authorName) throws Exception {
//        List<Address> addresses = new ArrayList<>();
//        List<Utf8String> metadataUrls = new ArrayList<>();
        log.info("传入参数：num {},metadata  {},authorName  {}", num, JSON.toJSONString(metadata), authorName);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BigInteger startTokenId = BigInteger.valueOf(timestamp.getTime());
        String jsonString = JSON.toJSONString(metadata);
        if (PingYinUtil.containsChinese(authorName)) {
            authorName = PingYinUtil.toPinyin(authorName);
        }
        String name = metadata.getName();
        if (PingYinUtil.containsChinese(name)) {
            name = PingYinUtil.toPinyin(name);
        }
        String fullPath = "E:\\WorkSpace\\chain\\metadata_json\\" + authorName + "\\" + name + ".json";
        log.info(fullPath);
        File file = new File(fullPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        writer.write(jsonString);
        writer.flush();
        writer.close();
        String objectName = "json/" + authorName + "/" + name + ".json";
        String jsonUrl = AliOssUtil.uploadFile(file, objectName);
        file.delete();
        Utf8String uriEncoded = new Utf8String(jsonUrl);
//        for (int i = 0; i < num; i++) {
//            addresses.add(account.getAddress().getABIAddress());
//            metadataUrls.add(uriEncoded);
//        }
//
//        List<Type> inputParameters = new ArrayList<>();
//        inputParameters.add(new DynamicArray(Address.class,addresses));
//        inputParameters.add(new DynamicArray(Utf8String.class,metadataUrls));
//
//        Function adminCreateNFTBatch = new Function("AdminCreateNFTBatch", inputParameters, Arrays.<TypeReference<?>>asList(new TypeReference<Uint>() {
//        }));
//
//        String data = DefaultFunctionEncoder.encode(adminCreateNFTBatch);
//        account.call(new Account.Option().withGasLimit(new BigInteger("99999999999999")),new Address(contractAddress),)
        //TODO 燃气费限制，这里每次最坏不要发太多
        String txHash = account.call(new Account.Option().withGasLimit(15000000),new Address(contractAddress), "MyAdminCreateNFTBatch",
                account.getAddress().getABIAddress(),
                uriEncoded,
                new Uint256(startTokenId),
                new Uint256(num)
        );
        List<BigInteger> tokenIds = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            startTokenId = startTokenId.add(BigInteger.valueOf(i));
            tokenIds.add(startTokenId);
        }
        CreateNftBatchResultTo to = new CreateNftBatchResultTo();
        to.setTxHash(txHash);
        to.setTokenIds(tokenIds);
        to.setTokenUri(jsonUrl);
//        String txHash = account.callWithData(new conflux.web3j.types.Address(contractAddress), data);
//        Account.Option option = new Account.Option();
//        RawTransaction rtx = option.buildTx(cfx, account.getAddress(), account.getPoolNonce(), new conflux.web3j.types.Address(contractAddress), data);
//        String sign = account.sign(rtx);
//        SendTransactionResult result = account.send(rtx);
        log.info("创建{}个Nft,metadata:{},交易hash为：{},id为: {}", num, metadata.toString(), txHash, tokenIds.toString());
        return to;
    }

    @Override
    public List<String> creatNftBatch(int num, NftMetadata metadata) throws Exception {
        String uri = "https://nft-demo.oss-cn-zhangjiakou.aliyuncs.com/json/%E5%90%88%E7%BA%A6%E6%89%B9%E9%87%8F%E7%94%9F%E6%88%90.json";
        Utf8String metadataUrl = new Utf8String(uri);
        List<String> txHashList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            String txHash = account.call(
                    new Account.Option().withGasPrice(CfxUnit.DEFAULT_GAS_PRICE).withGasLimit(CfxUnit.DEFAULT_GAS_LIMIT),
                    new conflux.web3j.types.Address(contractAddress),
                    "AdminCreateNFT",
                    account.getAddress().getABIAddress(),
                    metadataUrl
            );
            txHashList.add(txHash);
        }
        return txHashList;
    }

    /**
     * 为用户to赋予对tokenId的转增权限
     *
     * @param to      用户地址
     * @param tokenId nft链上id
     * @return
     */
    @Override
    public String approve(String to, BigInteger tokenId) throws Exception {
        boolean valid = conflux.web3j.types.Address.isValid(to);
        if (valid) {
            String txHash = account.call(
                    new conflux.web3j.types.Address(contractAddress),
                    "approve",
                    new conflux.web3j.types.Address(to).getABIAddress(),
                    new Uint256(tokenId)
            );
            return txHash;
        } else {
            throw new RuntimeException("地址不合法");
        }
    }

    /**
     * 允许用户转增所有的已拥有nft
     *
     * @param to           用户地址
     * @param isApproveAll 是否允许
     * @return
     */
    @Override
    public String setApproveAll(String to, boolean isApproveAll) throws Exception {
        boolean valid = conflux.web3j.types.Address.isValid(to);
        if (valid) {
            String txHash = account.call(
                    new conflux.web3j.types.Address(contractAddress),
                    "setApprovalForAll",
                    new conflux.web3j.types.Address(to).getABIAddress(),
                    new Bool(isApproveAll)
            );
            return txHash;
        } else {
            throw new RuntimeException("地址不合法！");
        }
    }

    @Override
    public String transfer(String from, String to, BigInteger tokenId) throws Exception {
        boolean fromValid = conflux.web3j.types.Address.isValid(from);
        boolean toValid = conflux.web3j.types.Address.isValid(to);
        if (!fromValid) {
            throw new RuntimeException("from不合法");
        }
        if (!toValid) {
            throw new RuntimeException("to不合法");
        }
        String txHash = account.call(
                new Account.Option().withGasPrice(CfxUnit.DEFAULT_GAS_PRICE).withGasLimit(25000),
                new conflux.web3j.types.Address(contractAddress),
                "safeTransferFrom",
                new conflux.web3j.types.Address(from).getABIAddress(),
                new conflux.web3j.types.Address(to).getABIAddress(),
                new Uint256(tokenId)
        );
        return txHash;
    }

    @Override
    public BigInteger totalSupply() {
        String totalSupply = contractCall.call("totalSupply").sendAndGet();
        BigInteger supply = DecodeUtil.decode(totalSupply, Uint256.class);
        log.info("totalSupply：{}", supply);
        return supply;
    }

    @Override
    public String adminTransfer(String toAddress, BigInteger tokenId) {
        try {
            String txHash = account.call(new Address(contractAddress),
                    "safeTransferFrom",
                    account.getAddress().getABIAddress(),
                    new Address(toAddress).getABIAddress(),
                    new Uint256(tokenId));
            return txHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String adminTransferBatch(List<String> addressList, List<BigInteger> tokenIds) {
        List<org.web3j.abi.datatypes.Address> addresses = addressList.stream().map(address -> new Address(address).getABIAddress()).collect(Collectors.toList());
        List<Uint256> tokenIdsType = tokenIds.stream().map(Uint256::new).collect(Collectors.toList());

        DynamicArray<org.web3j.abi.datatypes.Address> addressArray = new DynamicArray<>(addresses);
        DynamicArray<Uint256> tokenIdArray = new DynamicArray<>(tokenIdsType);

        try {
            String txHash = account.call(new Address(contractAddress),
                    "AdminTransferBatch",
                    addressArray,
                    tokenIdArray);
            return txHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String userTransfer(UserTransferTo to) throws Exception {
        Account account = Account.create(cfx, to.getPrivateKey());
        boolean toValid = Address.isValid(to.getToAddress());
        boolean fromValid = Address.isValid(to.getFromAddress());
        if (!fromValid) {
            throw new RuntimeException("from不合法");
        }
        if (!toValid) {
            throw new RuntimeException("to不合法");
        }
        String txHash = account.call( new Account.Option().withGasPrice(CfxUnit.DEFAULT_GAS_PRICE),
                new conflux.web3j.types.Address(contractAddress),
                "safeTransferFrom",
                new conflux.web3j.types.Address(to.getFromAddress()).getABIAddress(),
                new conflux.web3j.types.Address(to.getToAddress()).getABIAddress(),
                new Uint256(to.getTokenId()));
        return txHash;
    }

    @Override
    public AccountInfo createAccount(String pwd) throws Exception {
        AccountInfo ai = new AccountInfo();
        Address address = accountManager.create(pwd);
        String privateKey = accountManager.exportPrivateKey(address, pwd);
        //解锁账户
        accountManager.unlock(address, pwd);
        ai.setAddress(address.getAddress());
        ai.setPassword(pwd);
        ai.setPrivateKey(privateKey);
        return ai;
    }
}
