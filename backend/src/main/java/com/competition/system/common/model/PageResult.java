package com.competition.system.common.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private long current;
    private long size;
    private long total;
    private List<T> records;

    public static <T> PageResult<T> from(IPage<T> page) {
        return PageResult.<T>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal())
                .records(page.getRecords())
                .build();
    }
}
