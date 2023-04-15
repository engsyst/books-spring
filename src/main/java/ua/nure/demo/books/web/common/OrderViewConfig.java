package ua.nure.demo.books.web.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component // ("orderViewConfig")
@Getter
@Setter
@ToString
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
public class OrderViewConfig extends PageViewConfig {
    @Value("#{${app.web.view.order.status-style}}")
    private Map<String, String> statusStyles;
    @Value("#{${app.web.view.order.status-icon}}")
    private Map<String, String> statusIcons;
    @Value("#{${app.web.view.order.status-label}}")
    private Map<String, String> statusLabels;
    @Value("${app.web.view.order.filter}")
    private String filter;
    @Value("${app.web.view.order.sort-field}")
    private String sortField;
    @Value("${app.web.view.order.sort-order}")
    private String sortOrder;
    @Value("${app.web.view.order.items-per-page}")
    Integer itemsPerPage;
}
