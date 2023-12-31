package com.cqupt.art.author.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.constant.SeckillConstant;
import com.cqupt.art.author.entity.AuthorEntity;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.NftInfoEntity;
import com.cqupt.art.author.entity.NftMetadata;
import com.cqupt.art.author.entity.to.CreateNftBatchInfoTo;
import com.cqupt.art.author.entity.to.CreateNftBatchResultTo;
import com.cqupt.art.author.entity.to.NftDetailRedisTo;
import com.cqupt.art.author.entity.to.WorkQuery;
import com.cqupt.art.author.entity.vo.NftDetailVo;
import com.cqupt.art.author.entity.vo.SnapUpNftInfoVo;
import com.cqupt.art.author.feign.ChainFeignService;
import com.cqupt.art.author.dao.NftBatchInfoMapper;
import com.cqupt.art.author.service.AuthorService;
import com.cqupt.art.author.service.NftBatchInfoService;
import com.cqupt.art.author.service.NftInfoService;
import com.cqupt.art.utils.AliOssUtil;
import com.cqupt.art.utils.PageUtils;
import com.cqupt.art.utils.Query;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("nftBatchInfoService")
@Slf4j
public class NftBatchInfoServiceImpl extends ServiceImpl<NftBatchInfoMapper, NftBatchInfoEntity> implements NftBatchInfoService {
    @Autowired
    ChainFeignService chainFeignService;
    @Autowired
    AuthorService authorService;
    @Autowired
    NftInfoService nftInfoService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    NftBatchInfoMapper batchInfoMapper;

    private static String CACHE_PREFIX = "nft:info:";


    @Override
    public NftBatchInfoEntity upToChain(Long id) {
        NftBatchInfoEntity byId = this.getById(id);
        CreateNftBatchInfoTo createNftBatchInfoTo = new CreateNftBatchInfoTo();
        createNftBatchInfoTo.setNum(byId.getTotalSupply());
        String authorName = authorService.getById(byId.getAuthorId()).getAuthorName();
        createNftBatchInfoTo.setAuthorName(authorName);
        NftMetadata nftMetadata = new NftMetadata();
        nftMetadata.setImage(byId.getImageUrl());

        if (byId.getDescription() == null) {
            log.error("请先添加图片描述");
        } else {
            nftMetadata.setDescription(byId.getDescription());
        }
        nftMetadata.setName(byId.getName());
        createNftBatchInfoTo.setMetadata(nftMetadata);

        R r = chainFeignService.createNftBatchOnce(createNftBatchInfoTo);
        CreateNftBatchResultTo createNftBatchResultTo = null;
        if (r.getCode() != 0) {
            log.error("上链失败");
        } else {
            createNftBatchResultTo = r.getData("data", new TypeReference<CreateNftBatchResultTo>() {
            });
        }
        byId.setTokenUri(createNftBatchResultTo.getTokenUri());
        byId.setTxHash(createNftBatchResultTo.getTxHash());
        //上链生成的全部藏品ID
        List<BigInteger> tokenIds = createNftBatchResultTo.getTokenIds();
        for (int i = 0; i < tokenIds.size(); i++) {
            NftInfoEntity nftInfoEntity = new NftInfoEntity();
            nftInfoEntity.setTotalSupply(byId.getTotalSupply());
            nftInfoEntity.setAuthorId(byId.getAuthorId());
            nftInfoEntity.setType(byId.getType());
            nftInfoEntity.setContractAddress(byId.getContractAddress());
            nftInfoEntity.setLocalId(i + 1);
            nftInfoEntity.setTokenName(nftMetadata.getName());
            nftInfoEntity.setImageUrl(nftMetadata.getImage());
            nftInfoEntity.setDescription(nftMetadata.getDescription());
            nftInfoEntity.setTxHash(byId.getTxHash());
            nftInfoEntity.setTokenId(tokenIds.get(i).longValue());
            nftInfoEntity.setUserId("");
            nftInfoEntity.setArtId(byId.getId() + "");
            nftInfoService.save(nftInfoEntity);
        }
        this.baseMapper.updateById(byId);
        return byId;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<NftBatchInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (key != null && key.length() != 0) {
            queryWrapper.like("name", key);
        }
        String type = (String) params.get("type");
        if (type != null && type.length() != 0) {
            queryWrapper.and(q -> {
                q.eq("type", type);
            });
        }
        IPage<NftBatchInfoEntity> page = this.page(
                new Query<NftBatchInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void updateDes(NftBatchInfoEntity nftBatchInfo) {
        // 更新批量这个表
        this.updateById(nftBatchInfo);
        // TODO：删除缓存
        // 更新藏品的单个表
        UpdateWrapper<NftInfoEntity> up = new UpdateWrapper<>();
        up.set("description", nftBatchInfo.getDescription());
        up.eq("art_id", nftBatchInfo.getId() + "");
        nftInfoService.update(up);
    }


    @Override
    public void deleteBatch(Long id) {
        this.baseMapper.deleteById(id);
        //再去删除一批中的每一个藏品
        Map<String, Object> param = new HashMap<>();
        param.put("art_id", id + "");
        nftInfoService.getBaseMapper().deleteByMap(param);
    }

    @Override
    public PageUtils listQuery(WorkQuery query) {
        QueryWrapper<NftBatchInfoEntity> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(query.getKeyword())) {
            String keyword = query.getKeyword();
            if ("0x".equals(keyword.substring(0, 2))) {
                wrapper.eq("tx_hash", keyword);
            } else {
                wrapper.like("name", keyword);
            }
        }
        if (query.getAuthorId() != null) {
            wrapper.eq("author_id", query.getAuthorId());
        }
        Page<NftBatchInfoEntity> ipage = new Page<>(query.getCurPage(), query.getLimit());
        ipage.addOrder(OrderItem.desc("issue_time"));
        IPage<NftBatchInfoEntity> page = this.page(ipage, wrapper);
        return new PageUtils(page);
    }

    @Override
    public String uploadNftImage(MultipartFile file) throws IOException {
        String originName = file.getOriginalFilename();
        InputStream is = file.getInputStream();
        String objectName = "img/nft/" + originName;
        log.info(objectName);
        String imgUrl = AliOssUtil.uploadFile(is, objectName);
        log.info("上传NFT图片链接为：{}", imgUrl);
        return imgUrl;
    }

    @Override
    public void launch(Long workId) {
        NftBatchInfoEntity entity = batchInfoMapper.selectByIdLock(workId);
//         = baseMapper.selectById(workId);
        if (entity.getLanuchStatus() == 1) { //秒杀未上架
            String key = SeckillConstant.SECKILL_DETAIL_PREFIX + ":" + entity.getName() + "-" + entity.getId();
            BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps(key);
            String s = ops.get();
            if (!StringUtils.isNotBlank(s)) {
                AuthorEntity author = authorService.getById(entity.getAuthorId());
                NftDetailRedisTo to = new NftDetailRedisTo();
                BeanUtils.copyProperties(entity, to);
                to.setAuthorName(author.getAuthorName());
                to.setAuthorDesc(author.getAuthorDesc());
                to.setAvatarUrl(author.getAvatarUrl());
                Date startTime = entity.getIssueTime();
                to.setStartTime(startTime);
                //秒杀时间为30分钟
                //测试时设置3天过期
//                to.setEndTime(new Date(startTime.getTime() + 30 * 60 * 1000));
                LocalDateTime start = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime end = start.plus(3, ChronoUnit.DAYS);
                to.setEndTime(Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));

                String token = UUID.randomUUID().toString().replace("-", "");
                to.setToken(token);
                String redisJson = JSON.toJSONString(to);
                ops.set(redisJson, 3, TimeUnit.DAYS);
                //使用信号量设置库存
                RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_SEMAPHORE + token);
                semaphore.trySetPermits(entity.getInventory());

                entity.setLanuchStatus(3);
                baseMapper.updateById(entity);
            } else {
                log.info("redis中已存在！请检查！");
            }
        } else {
            //普通上架
            entity.setLanuchStatus(2);
            this.updateById(entity);
        }

    }

    @Override
    public List<SnapUpNftInfoVo> snapUpListInfo(Integer curPage, Integer limit) {
        int start = (curPage - 1) * limit;
        List<SnapUpNftInfoVo> snapUpNftInfos = baseMapper.getSnapUpList(start, limit);
        return snapUpNftInfos;
    }

    @Override
    public NftDetailRedisTo secKillDetail(String id, String nftName) {
        String key = SeckillConstant.SECKILL_DETAIL_PREFIX + ":" + nftName + "-" + id;
//        String json = (String) redisTemplate.opsForHash().get(SeckillConstant.SECKILL_DETAIL_PREFIX, key);
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)) {
            log.info("秒杀商品 redis中查询到的数据：{}", json);
            NftDetailRedisTo to = JSON.parseObject(json, NftDetailRedisTo.class);
            String s = stringRedisTemplate.opsForValue().get(SeckillConstant.SECKILL_SEMAPHORE + to.getToken());
            assert s != null;
            to.setStock(Long.parseLong(s));
            log.info("库存：{}", s);
            //秒杀需要验证token，若未到开始时间，不能把token给出去
            long startTime = to.getStartTime().getTime();
            if (System.currentTimeMillis() < startTime) {
                to.setToken(null);
            }
            return to;
        }
        return null;
    }

    //    sync：解决缓存击穿
    @Override
//    @Cacheable(cacheNames = "nft:nftInfo:",key = "#id",sync = true)
    public NftDetailVo nftDetail(String id) {

        String key = CACHE_PREFIX + id;
        NftDetailVo vo = null;
        String cacheJson = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(cacheJson)) {
            vo = JSON.parseObject(cacheJson, NftDetailVo.class);
        } else {
            RLock lock = redissonClient.getLock("cacheLock");
            try {
                lock.lock();
                vo = baseMapper.getNftDetail(id);
                long time = vo.getStartTime().getTime();
                //结束时间
                time += 30 * 60 * 1000 * 48;
                vo.setEndTime(new Date(time));
                stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(vo), time, TimeUnit.SECONDS);
            } finally {
                lock.unlock();
            }
        }
        return vo;
    }

    @Override
    public void mintNft(NftBatchInfoEntity batchInfoEntity) {
        Integer isOpen = batchInfoEntity.getIsOpen();
        if (isOpen == 1) {
            batchInfoEntity.setLanuchStatus(1);
        } else {
            batchInfoEntity.setLanuchStatus(0);
        }
        this.save(batchInfoEntity);
    }

    @Transactional
    @Override
    public void updateInventory(Long artId, Long localId) {
        batchInfoMapper.updateInventory(artId);
    }
}
