package com.hjw.dto;

import lombok.Getter;

/**
 * @author qifei
 * @since 2024-04-16
 */
@Getter
public enum EmpRoleEnum
{

    EMPLOYEE("员工", 0),
    MERCHANT("商家", 1),
    ADMIN("管理员", 2);

    private String text;
    private int value;

    EmpRoleEnum(String text, int value)
    {
        this.text = text;
        this.value = value;
    }
}
