package com.hjw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjw.pojo.Category;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService extends IService<Category>
{
    public void remove(Long id);
}
