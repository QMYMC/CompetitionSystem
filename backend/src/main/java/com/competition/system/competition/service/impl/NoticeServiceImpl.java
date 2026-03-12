package com.competition.system.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.competition.system.common.exception.BusinessException;
import com.competition.system.common.model.PageResult;
import com.competition.system.competition.constant.WorkflowConstants;
import com.competition.system.competition.dto.NoticeCreateRequest;
import com.competition.system.competition.dto.NoticeQueryRequest;
import com.competition.system.competition.dto.NoticeUpdateRequest;
import com.competition.system.competition.entity.NoticeInfo;
import com.competition.system.competition.mapper.NoticeInfoMapper;
import com.competition.system.competition.service.NoticeService;
import com.competition.system.competition.vo.NoticeDetailView;
import com.competition.system.competition.vo.NoticeListItem;
import com.competition.system.security.LoginUser;
import com.competition.system.user.entity.SysUser;
import com.competition.system.user.mapper.SysUserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeInfoMapper noticeInfoMapper;
    private final SysUserMapper sysUserMapper;

    public NoticeServiceImpl(NoticeInfoMapper noticeInfoMapper, SysUserMapper sysUserMapper) {
        this.noticeInfoMapper = noticeInfoMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<NoticeListItem> pageNotices(NoticeQueryRequest request) {
        Page<NoticeInfo> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<NoticeInfo> wrapper = new LambdaQueryWrapper<NoticeInfo>()
                .like(StringUtils.hasText(request.getKeyword()), NoticeInfo::getTitle, request.getKeyword())
                .eq(StringUtils.hasText(request.getPublishStatus()), NoticeInfo::getPublishStatus, request.getPublishStatus())
                .orderByDesc(NoticeInfo::getTopFlag)
                .orderByDesc(NoticeInfo::getPublishTime)
                .orderByDesc(NoticeInfo::getId);
        if (!isAdmin()) {
            wrapper.eq(NoticeInfo::getPublishStatus, WorkflowConstants.NOTICE_STATUS_PUBLISHED);
        }
        Page<NoticeInfo> result = noticeInfoMapper.selectPage(page, wrapper);
        List<NoticeListItem> records = result.getRecords().stream().map(this::toNoticeListItem).toList();
        return PageResult.<NoticeListItem>builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(records)
                .build();
    }

    @Override
    public NoticeDetailView getNoticeDetail(Long noticeId) {
        NoticeInfo noticeInfo = requireNotice(noticeId);
        if (!isAdmin() && !WorkflowConstants.NOTICE_STATUS_PUBLISHED.equals(noticeInfo.getPublishStatus())) {
            throw new BusinessException("当前公告暂未发布");
        }
        return toNoticeDetailView(noticeInfo);
    }

    @Override
    @Transactional
    public Long createNotice(NoticeCreateRequest request) {
        NoticeInfo noticeInfo = new NoticeInfo();
        fillNotice(noticeInfo, request.getTitle(), request.getContent(), request.getPublishStatus(), request.getTopFlag());
        noticeInfo.setPublisherId(currentUserId());
        noticeInfoMapper.insert(noticeInfo);
        return noticeInfo.getId();
    }

    @Override
    @Transactional
    public void updateNotice(Long noticeId, NoticeUpdateRequest request) {
        NoticeInfo noticeInfo = requireNotice(noticeId);
        fillNotice(noticeInfo, request.getTitle(), request.getContent(), request.getPublishStatus(), request.getTopFlag());
        noticeInfoMapper.updateById(noticeInfo);
    }

    @Override
    @Transactional
    public void deleteNotice(Long noticeId) {
        requireNotice(noticeId);
        noticeInfoMapper.deleteById(noticeId);
    }

    private void fillNotice(NoticeInfo noticeInfo, String title, String content, String publishStatus, Integer topFlag) {
        noticeInfo.setTitle(title);
        noticeInfo.setContent(content);
        noticeInfo.setPublishStatus(publishStatus);
        noticeInfo.setTopFlag(topFlag);
        if (WorkflowConstants.NOTICE_STATUS_PUBLISHED.equals(publishStatus)) {
            noticeInfo.setPublishTime(noticeInfo.getPublishTime() == null ? LocalDateTime.now() : noticeInfo.getPublishTime());
        } else {
            noticeInfo.setPublishTime(null);
        }
    }

    private NoticeInfo requireNotice(Long noticeId) {
        NoticeInfo noticeInfo = noticeInfoMapper.selectById(noticeId);
        if (noticeInfo == null) {
            throw new BusinessException("公告不存在");
        }
        return noticeInfo;
    }

    private NoticeListItem toNoticeListItem(NoticeInfo noticeInfo) {
        SysUser publisher = sysUserMapper.selectById(noticeInfo.getPublisherId());
        String content = noticeInfo.getContent() == null ? "" : noticeInfo.getContent();
        String preview = content.length() > 60 ? content.substring(0, 60) + "..." : content;
        return NoticeListItem.builder()
                .id(noticeInfo.getId())
                .title(noticeInfo.getTitle())
                .contentPreview(preview)
                .publishStatus(noticeInfo.getPublishStatus())
                .publisherName(publisher == null ? null : publisher.getRealName())
                .publishTime(noticeInfo.getPublishTime())
                .topFlag(noticeInfo.getTopFlag())
                .createTime(noticeInfo.getCreateTime())
                .build();
    }

    private NoticeDetailView toNoticeDetailView(NoticeInfo noticeInfo) {
        SysUser publisher = sysUserMapper.selectById(noticeInfo.getPublisherId());
        return NoticeDetailView.builder()
                .id(noticeInfo.getId())
                .title(noticeInfo.getTitle())
                .content(noticeInfo.getContent())
                .publishStatus(noticeInfo.getPublishStatus())
                .publisherName(publisher == null ? null : publisher.getRealName())
                .publishTime(noticeInfo.getPublishTime())
                .topFlag(noticeInfo.getTopFlag())
                .createTime(noticeInfo.getCreateTime())
                .updateTime(noticeInfo.getUpdateTime())
                .build();
    }

    private boolean isAdmin() {
        return currentLoginUser().getRoles().contains("ADMIN");
    }

    private Long currentUserId() {
        return currentLoginUser().getUser().getId();
    }

    private LoginUser currentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException("当前登录状态无效");
        }
        return loginUser;
    }
}
