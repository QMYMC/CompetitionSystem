package com.competition.system.competition.service;

import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.AwardReviewQueryRequest;
import com.competition.system.competition.dto.AwardReviewRequest;
import com.competition.system.competition.dto.StudentAwardCreateRequest;
import com.competition.system.competition.dto.StudentAwardUpdateRequest;
import com.competition.system.competition.vo.AwardOptionItem;
import com.competition.system.competition.vo.AwardReviewItem;
import com.competition.system.competition.vo.StudentAwardItem;

import java.util.List;

public interface AwardService {

    List<AwardOptionItem> getStudentAwardOptions();

    List<StudentAwardItem> listMyAwards();

    Long createAward(StudentAwardCreateRequest request);

    void updateAward(Long awardId, StudentAwardUpdateRequest request);

    PageResult<AwardReviewItem> pageAwardsForReview(AwardReviewQueryRequest request);

    void approveAward(Long awardId, AwardReviewRequest request);

    void rejectAward(Long awardId, AwardReviewRequest request);
}
