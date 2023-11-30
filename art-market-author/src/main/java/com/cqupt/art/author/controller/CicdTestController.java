package com.cqupt.art.author.controller;

import com.cqupt.art.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CicdTestController {

    @GetMapping("version")
    public R version(){
        return R.ok().put("version",1.0);
    }

    @GetMapping("/hello")
    public R hello(){
        return R.ok("HelloWorld");
    }
}
