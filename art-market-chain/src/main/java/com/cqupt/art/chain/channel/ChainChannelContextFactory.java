package com.cqupt.art.chain.channel;

import com.cqupt.art.chain.channel.conflux.ConfluxOperation;
import com.cqupt.art.chain.entity.AccountInfo;
import com.cqupt.art.chain.entity.NftMetadata;
import com.cqupt.art.chain.entity.to.CreateNftBatchResultTo;
import com.cqupt.art.chain.entity.to.UserTransferTo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单工厂
 * @author hxd
 * @create 2023-05-21 21:08
 */

@Component
public class ChainChannelContextFactory {

    private final Map<String,ChainOperation> handlerMap = new ConcurrentHashMap<>();

    @Resource
    ConfluxOperation confluxOperation;


    @PostConstruct
    public void init(){
        handlerMap.put("conflux",confluxOperation);
    }

//    private ApplicationContext applicationContext;
//
//    /**
//     * 这是一种实现，从容器中取实现类
//     * 通过InitializingBean的afterPropertiesSet在完成其他操作后，放入容器中的实现类
//     * 把实现类放到map
//     * @throws Exception
//     */
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Map<String, ChainOperation> beansOfType = applicationContext.getBeansOfType(ChainOperation.class);
//        beansOfType.forEach(handlerMap::put);
//    }


//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }


    public ChainOperation getChainOperation(String type){
        return handlerMap.get(type);
    }
}
