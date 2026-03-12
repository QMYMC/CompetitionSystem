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
@TableName("notice")
public class NoticeInfo extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private String publishStatus;
    private Long publisherId;
    private LocalDateTime publishTime;
    private Integer topFlag;
}
