package com.competition.system.competition.service;

import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.NoticeCreateRequest;
import com.competition.system.competition.dto.NoticeQueryRequest;
import com.competition.system.competition.dto.NoticeUpdateRequest;
import com.competition.system.competition.vo.NoticeDetailView;
import com.competition.system.competition.vo.NoticeListItem;

public interface NoticeService {

    PageResult<NoticeListItem> pageNotices(NoticeQueryRequest request);

    NoticeDetailView getNoticeDetail(Long noticeId);

    Long createNotice(NoticeCreateRequest request);

    void updateNotice(Long noticeId, NoticeUpdateRequest request);

    void deleteNotice(Long noticeId);
}
