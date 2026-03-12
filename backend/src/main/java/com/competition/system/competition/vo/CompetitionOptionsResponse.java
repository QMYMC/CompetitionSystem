package com.competition.system.competition.vo;

import com.competition.system.common.model.OptionItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompetitionOptionsResponse {

    private List<OptionItem<Long>> categories;
    private List<OptionItem<String>> statuses;
    private List<OptionItem<String>> teamModes;
}
