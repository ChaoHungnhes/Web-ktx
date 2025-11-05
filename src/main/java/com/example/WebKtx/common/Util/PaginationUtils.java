package com.example.WebKtx.common.Util;

import com.example.WebKtx.dto.ResultPaginationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public final class PaginationUtils {
    private PaginationUtils(){}

    public static ResultPaginationDTO wrap(Page<?> page) {
        ResultPaginationDTO dto = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Pageable p = page.getPageable();

        meta.setPage(p.getPageNumber() + 1);   // chuyển về 1-based
        meta.setPageSize(p.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        dto.setMeta(meta);
        dto.setResult(page.getContent());
        return dto;
    }
}
