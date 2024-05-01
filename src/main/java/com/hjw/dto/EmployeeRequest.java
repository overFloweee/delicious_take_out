package com.hjw.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeRequest implements Serializable
{

    private Long id;


    private Integer role;

    private Integer status;

}
