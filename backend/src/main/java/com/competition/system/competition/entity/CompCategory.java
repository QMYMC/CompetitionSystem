package com.competition.system.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.competition.system.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comp_category")
public class CompCategory extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String categoryName;
    private String categoryCode;
    private String description;
    private Integer sort;
    private Integer status;
}
