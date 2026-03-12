package com.competition.system.competition.controller;

import com.competition.system.common.model.ApiResponse;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.dto.NoticeCreateRequest;
import com.competition.system.competition.dto.NoticeQueryRequest;
import com.competition.system.competition.dto.NoticeUpdateRequest;
import com.competition.system.competition.service.NoticeService;
import com.competition.system.competition.vo.NoticeDetailView;
import com.competition.system.competition.vo.NoticeListItem;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ApiResponse<PageResult<NoticeListItem>> page(NoticeQueryRequest request) {
        return ApiResponse.success(noticeService.pageNotices(request));
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<NoticeDetailView> detail(@PathVariable Long noticeId) {
        return ApiResponse.success(noticeService.getNoticeDetail(noticeId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> create(@Valid @RequestBody NoticeCreateRequest request) {
        return ApiResponse.success(noticeService.createNotice(request));
    }

    @PutMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> update(@PathVariable Long noticeId, @Valid @RequestBody NoticeUpdateRequest request) {
        noticeService.updateNotice(noticeId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ApiResponse.success();
    }
}
