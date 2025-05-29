package com.gestiondesannotateurs.dtos;

import java.util.List;
public class PagedCoupletextDto {
    private long totalCount;
    private int totalPages;
    private List<CoupletextDto> couples;
    private int currentPage;

    public PagedCoupletextDto( long totalCount, int totalPages, List<CoupletextDto> couples,int currentPage) {
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.couples = couples;
        this.currentPage = currentPage;
    }

    public List<CoupletextDto> getCouples() {
        return couples;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}


