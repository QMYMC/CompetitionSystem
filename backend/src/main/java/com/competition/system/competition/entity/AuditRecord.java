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
@TableName("audit_record")
public class AuditRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String businessType;
    private Long businessId;
    private String auditStage;
    private String auditStatus;
    private Long auditorId;
    private String auditOpinion;
    private LocalDateTime auditTime;
}
