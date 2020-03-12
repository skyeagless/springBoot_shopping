package com.skyeagle.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Category {
    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    private Date createTime;

    private Date updateTime;

}