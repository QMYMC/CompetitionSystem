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
@TableName("competition_registration")
public class CompetitionRegistration extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long competitionId;
    private Long userId;
    private Long teamId;
    private String registrationType;
    private String auditStatus;
    private LocalDateTime submitTime;
    private LocalDateTime finalSubmitTime;
    private String remark;
}
