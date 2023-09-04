package com.cqupt.art.notice.controller;

import com.cqupt.art.notice.service.ArticleFileService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/notice-file")
public class ArticleFileController {

    @Autowired
    private ArticleFileService articleFileService;

    //上传公告预览得小图
    @PostMapping("/uploadNoticeSmallImg")
    public R uploadNoticeSmallImg(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return R.error("公告预览图上传失败！");
        }
        //返回云端的地址
        String url = null;
        try {
            url = articleFileService.upload(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.ok().put("imgUrl", url);
    }
}
