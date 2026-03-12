package com.competition.system.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.competition.system.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_college")
public class SysCollege extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String collegeCode;
    private String collegeName;
    private Integer sort;
    private Integer status;
}
