package com.hjw.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hjw.common.Result;
import com.hjw.pojo.AddressBook;
import com.hjw.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController
{

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list()
    {
        List<AddressBook> list = addressBookService.list();
        return Result.success(list);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public Result<String> defaultAddress(@RequestBody AddressBook addressBook)
    {
        addressBookService.updateDefault(addressBook);
        return Result.success("修改成功！");
    }

    /**
     * 新增
     */
    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook, HttpSession session)
    {
        Long userId = (Long) session.getAttribute("user");
        addressBook.setUserId(userId);
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public Result get(@PathVariable Long id)
    {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null)
        {
            return Result.success(addressBook);
        }
        else
        {
            return Result.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public Result<AddressBook> getDefault(HttpSession session)
    {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, session.getAttribute("user"));
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        // SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook)
        {
            return Result.error("没有找到该对象");
        }
        else
        {
            return Result.success(addressBook);
        }
    }



}
