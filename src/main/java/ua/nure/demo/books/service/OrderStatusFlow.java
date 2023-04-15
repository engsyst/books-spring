package ua.nure.demo.books.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
//@ConfigurationProperties(prefix = "app.order")
@Getter @Setter @ToString
public class OrderStatusFlow {
    @Value("#{${app.order.status-list}}")
    private List<String> statusList;
    @Value("#{${app.order.status-flow}}")
    private Map<String, List<String>> statusFlow;
}
