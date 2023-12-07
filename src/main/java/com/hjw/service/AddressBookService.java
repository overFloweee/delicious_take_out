package com.hjw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hjw.pojo.AddressBook;
import org.springframework.stereotype.Service;

@Service
public interface AddressBookService extends IService<AddressBook>
{
    void updateDefault(AddressBook addressBook);

    void updateAddress(AddressBook addressBook);
}
