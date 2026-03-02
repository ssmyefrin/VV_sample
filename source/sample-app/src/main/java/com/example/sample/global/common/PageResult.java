package com.example.sample.global.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * PageResponse
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
@Getter
public class PageResult<T> {

    private final List<T> content;
    private final PageInfo pageInfo;

    public PageResult(Page<T> pageData) {
        this.content = pageData.getContent();
        this.pageInfo = new PageInfo(
                pageData.getNumber() + 1,
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages()
        );
    }

    @Getter
    public static class PageInfo {
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;

        public PageInfo(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }
}
