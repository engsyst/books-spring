package ua.nure.demo.books.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.nure.demo.books.service.OrderStatusFlow;
import ua.nure.demo.books.web.Const;
import ua.nure.demo.books.web.common.BookViewConfig;
import ua.nure.demo.books.web.common.OrderViewConfig;
import ua.nure.demo.books.web.common.ShoppingCart;
import ua.nure.demo.books.web.dto.PageSortFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class SessionFilter extends HttpFilter {
    @Value("${app.web.filter.session-filter.static-resources}")
    private String staticConfigResources;
    @Value("${app.web.filter.session-filter.client-resources}")
    private String openedConfigResources;
    @Value("${app.web.filter.session-filter.admin-resources}")
    private String closedConfigResources;
    private List<String> staticResources;
    private List<String> clientResources;
    private List<String> adminResources;

    @Autowired
    OrderStatusFlow orderFlow;
    @Autowired
    OrderViewConfig orderViewConfig;
    @Autowired
    BookViewConfig bookViewConfig;
    PageSortFilter bookPageSortFilter;
    PageSortFilter orderPageSortFilter;

    @Override
    public void init(FilterConfig filterConfig) {
        staticResources = List.of(staticConfigResources.split(",\\s*"));
        clientResources = List.of(openedConfigResources.split(",\\s*"));
        adminResources = List.of(closedConfigResources.split(",\\s*"));
        filterConfig.getServletContext().setAttribute("statuses", orderFlow.getStatusList());
        filterConfig.getServletContext().setAttribute("statusFlow", orderFlow.getStatusFlow());
        filterConfig.getServletContext().setAttribute("statusLabels", orderViewConfig.getStatusLabels());
        filterConfig.getServletContext().setAttribute("statusIcons", orderViewConfig.getStatusIcons());
        filterConfig.getServletContext().setAttribute("statusStyles", orderViewConfig.getStatusStyles());
        log.debug("OrderFlow: {}", orderFlow);
        log.debug("OrderView: {}", orderViewConfig);
        bookPageSortFilter = new PageSortFilter(null, bookViewConfig.getSortField(), bookViewConfig.getSortOrder(),
                bookViewConfig.getItemsPerPage(), 0, null);
        log.debug("bookPageSortFilter: {}", bookPageSortFilter);
        orderPageSortFilter = new PageSortFilter(orderViewConfig.getFilter(), orderViewConfig.getSortField(),
                orderViewConfig.getSortOrder(),
                orderViewConfig.getItemsPerPage(), 0, null);
        log.debug("orderPageSortFilter: {}", orderPageSortFilter);
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        log.debug("Start");
        String path = req.getServletPath();
        log.debug("Request path: '{}'", path);

        // bypass for static resources
        if (pathIn(path, staticResources)) {
            chain.doFilter(req, resp);
            log.debug("Finish");
            return;
        }

        HttpSession session = req.getSession();
        log.debug("Get session: {}, isNew: {}", session.getId(), session.isNew());

        // call to the context path
        if (path.equals("/")) {
            log.debug("Requested app context path");
            if (!session.isNew()) {
                // each time when user access the application root
                // then session is invalidated
                // to get different or the same role.
                // Warn! ShoppingCart is also removed.
                log.debug("Invalidate session id: {}", session.getId());
                session.invalidate();
                session = req.getSession();
                log.debug("New session created: {}", session.getId());
            }
            // session.setAttribute("paging", paging.copy());
            session.setAttribute("cart", new ShoppingCart<>());
            session.setAttribute("bookPaging", bookPageSortFilter);
            session.setAttribute("orderPaging", orderPageSortFilter);
            log.debug("Pass to next chain step");
            chain.doFilter(req, resp);
            log.debug("Back trace of chain steps.\nFinish");
            return;
        }

        String role = (String) session.getAttribute("role");
        // link on index.html pressed or direct url
        // the request has 'role' parameter
        if (role != null) {
            // accesses according to the role
            if (canAccess(role, path)) {
                log.debug("Accept access for 'role': {} to resource '{}'", role, path);
                chain.doFilter(req, resp);
                log.debug("Back trace of chain steps.\nFinish");
            } else {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "You don't have access for the resource: " + path);
                log.debug("Access forbidden for 'role': {} to resource '{}'\nFinish", role, path);
            }
            return;
        }

        // session does not have role and
        // the request has 'role' parameter
        String requestRole = req.getParameter("role");
        if (requestRole != null) {
            session.setAttribute("role", requestRole);
            chain.doFilter(req, resp);
            log.debug("Back trace of chain steps.\nFinish");
            return;
        }

        resp.sendRedirect("/");
        log.debug("The 'role' have not found anywhere, redirect to context root.\nFinish");
    }

    boolean canAccess(String role, String path) {
        return (Const.ROLE_CLIENT.equals(role) && pathIn(path, clientResources)) ||
                (Const.ROLE_ADMIN.equals(role) && pathIn(path, adminResources));
    }

    boolean pathIn(String path, List<String> resources) {
        return resources.stream().anyMatch(path::startsWith);
    }

}
