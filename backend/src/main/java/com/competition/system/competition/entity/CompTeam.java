package com.competition.system.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.competition.system.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comp_team")
public class CompTeam extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long competitionId;
    private String teamName;
    private Long leaderUserId;
    private Long teacherId;
    private String teamStatus;
    private String remark;
}
