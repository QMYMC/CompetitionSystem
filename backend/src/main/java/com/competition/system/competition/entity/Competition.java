package com.competition.system.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.competition.system.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("competition")
public class Competition extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;
    private String title;
    private String levelName;
    private String organizer;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private LocalDateTime competitionStart;
    private LocalDateTime competitionEnd;
    private String teamMode;
    private Integer maxTeamSize;
    private String description;
    private String status;
}
