package com.competition.system.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.competition.system.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comp_team_member")
public class CompTeamMember extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId;
    private Long userId;
    private String memberRole;
    private String joinStatus;
}
