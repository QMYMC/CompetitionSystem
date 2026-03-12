package com.competition.system.competition.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AwardOptionItem {

    private Long registrationId;
    private Long competitionId;
    private String competitionTitle;
    private String registrationType;
    private String teamName;
}
