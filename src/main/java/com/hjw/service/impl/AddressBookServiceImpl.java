package com.hjw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjw.mapper.AddressBookMapper;
import com.hjw.pojo.AddressBook;
import com.hjw.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService
{

    @Autowired
    private HttpSession session;

    @Override
    @Transactional
    public void updateDefault(AddressBook addressBook)
    {
        Long id = addressBook.getId();
        // 找到默认地址，修改为 0
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getIsDefault, 1);
        wrapper.set(AddressBook::getIsDefault, 0);
        this.update(wrapper);

        // 将 指定id修改为默认地址
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getId, id);
        updateWrapper.set(AddressBook::getIsDefault, 1);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public void updateAddress(AddressBook addressBook)
    {

        this.updateById(addressBook);
    }
}
