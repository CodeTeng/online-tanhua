package com.tanhua.admin.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理
 */
@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleExcepting(Exception e) {
        e.printStackTrace();
        String message = "服务器内部错误";
        if (e instanceof BusinessException) {
            message = ((BusinessException) e).getMessage();
        }
        Map map = new HashMap();
        map.put("message", message);
        return ResponseEntity.status(500).body(map);
    }
}
