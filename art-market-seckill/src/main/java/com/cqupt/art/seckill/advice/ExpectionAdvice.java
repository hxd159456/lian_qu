package com.cqupt.art.seckill.advice;

import com.cqupt.art.exception.RRException;
import com.cqupt.art.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class ExpectionAdvice {
    @ExceptionHandler(RRException.class)
    public R handle(RRException rrException){
        return R.error().put("code",rrException.getCode()).put("msg",rrException.getMsg());
    }
}
