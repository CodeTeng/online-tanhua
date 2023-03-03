package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import lombok.Data;

/**
 * @description: 自定义异常
 * @author: ~Teng~
 * @date: 2023/3/2 10:28
 */
@Data
public class BusinessException extends RuntimeException {
    private ErrorResult errorResult;

    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}
