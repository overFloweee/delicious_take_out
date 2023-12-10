package com.hjw.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;



// 自定义元数据 处理器
// 将 公共字段createTime、updateTime...在插入、更新操作时，自动生成时间
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler
{
    @Autowired
    private HttpServletRequest request;

    // 进行数据库插入操作时，自动进行字段填充
    @Override
    public void insertFill(MetaObject metaObject)
    {
        Long operateId = (Long) request.getSession().getAttribute("employee");
        if (operateId == null)
        {
            operateId =(Long) request.getSession().getAttribute("user");
        }

        // log.info("自定义元数据 处理器");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", operateId);
        metaObject.setValue("updateUser", operateId);
    }

    @Override
    public void updateFill(MetaObject metaObject)
    {
        Long operateId = (Long) request.getSession().getAttribute("employee");
        if (operateId == null)
        {
            operateId = (Long) request.getSession().getAttribute("user");
        }
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", operateId);

    }
}
