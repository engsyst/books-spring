package ua.nure.demo.books.controller.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter @AllArgsConstructor @NoArgsConstructor @ToString
public class PageSortFilter {
    String filter;
    String sortField;
    String sortOrder = "ASC";
    int pageNumber;

    @Value("${app.web.items.per.page}")
    int pageItemsCount;
    int totalPages;

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder == null || sortField.isBlank() ? this.sortField : sortOrder;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageItemsCount(int pageItemsCount) {
        this.pageItemsCount = pageItemsCount == 0 ? this.pageItemsCount : pageItemsCount;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
