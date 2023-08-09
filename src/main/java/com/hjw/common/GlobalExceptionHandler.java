package com.hjw.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;


// 全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    // 字段 属性值重复异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex)
    {
        String message = ex.getMessage();
        log.error(message);
        if (message.contains("Duplicate entry"))
        {
            String username = message.split(" ")[2];
            return Result.error(username + "已存在！");
        }
        return Result.error("未知错误！");
    }

    // 自定义异常：当前分类还包含菜品或套餐
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex)
    {
        log.error(ex.getMessage());
        return Result.error(ex.getMessage());
    }



}
