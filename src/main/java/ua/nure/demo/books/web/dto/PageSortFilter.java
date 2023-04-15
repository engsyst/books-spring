package ua.nure.demo.books.web.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ua.nure.demo.books.web.common.PageViewConfig;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageSortFilter {
    private String filter;
    private String sortField;
    private String sortOrder = "ASC";
    private Integer itemsPerPage;
    private Integer pageNumber = 0;
    private Integer totalPages;

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder == null || sortField.isBlank() ? this.sortOrder : sortOrder;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setItemsPerPage(Integer pageItemsCount) {
        this.itemsPerPage = pageItemsCount == 0 ? this.itemsPerPage : pageItemsCount;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Pageable asPageable() {
        String sort = getSortField();
        return PageRequest.of(getPageNumber(), getItemsPerPage(),
                sort == null ? Sort.unsorted() :
                        Sort.by(Sort.Direction.fromString(getSortOrder()), sort));
    }

    public PageSortFilter setIfNull(PageSortFilter other) {
        if (other != null) {
            setIfNull(other.filter, other.sortField, other.sortOrder,
                    other.itemsPerPage, other.pageNumber, other.totalPages);
        }
        return this;
    }

    public void setIfNull(String filter, String sortField, String sortOrder,
                          Integer itemsPerPage, Integer pageNumber, Integer totalPages) {
        this.filter = this.filter == null ? filter : this.filter;
        this.sortField = this.sortField == null ? sortField : this.sortField;
        this.sortOrder = this.sortOrder == null ? sortOrder : this.sortOrder;
        this.itemsPerPage = this.itemsPerPage == null ? itemsPerPage : this.itemsPerPage;
        this.pageNumber = this.pageNumber == null ? pageNumber : this.pageNumber;
        this.totalPages = this.totalPages == null ? totalPages : this.totalPages;
    }

    public String asRequestParameters() {
        return (filter == null ? "" : "filter=" + filter) +
                (sortField == null ? "" : "&sortField=" + sortField) +
                (sortOrder == null ? "" : "&sortOrder=" + sortOrder) +
                (itemsPerPage == null ? "" : "&itemsPerPage=" + itemsPerPage) +
                (pageNumber == null ? "" : "&pageNumber=" + pageNumber);
    }

    @SneakyThrows
    public PageSortFilter copy() {
        return new PageSortFilter(filter, sortField, sortOrder,
                itemsPerPage, pageNumber, totalPages);
    }
}
