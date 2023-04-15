package ua.nure.demo.books.web.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component // ("bookViewConfig")
@Getter @Setter @ToString
public class BookViewConfig extends PageViewConfig {
    @Value("${app.web.view.book.sort-field}")
    private String sortField;
    @Value("${app.web.view.book.sort-order}")
    private String sortOrder;
    @Value("${app.web.view.book.items-per-page}")
    Integer itemsPerPage;
}
