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
@TableName("award_info")
public class AwardInfo extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long competitionId;
    private Long registrationId;
    private String awardName;
    private String awardLevel;
    private String awardRank;
    private LocalDateTime awardTime;
    private Long certificateFileId;
    private String auditStatus;
    private String remark;
}
