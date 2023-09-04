package com.cqupt.art.notice.service.impl;

import com.cqupt.art.notice.service.ArticleFileService;
import com.cqupt.art.notice.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class ArticleFileServiceImpl implements ArticleFileService {

    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
        String originName = multipartFile.getOriginalFilename();
        InputStream is = multipartFile.getInputStream();
        String objectName = "file/notice/img/small/" + originName;
        String imgUrl = AliOssUtil.uploadFile(is, objectName);
        log.info("上传的公告小图链接为：{}", imgUrl);
        return imgUrl;
    }
}
