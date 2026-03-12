package com.competition.system.user.vo;

import com.competition.system.common.model.OptionItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserOptionsResponse {

    private List<OptionItem<Long>> roles;
    private List<OptionItem<Long>> colleges;
}
