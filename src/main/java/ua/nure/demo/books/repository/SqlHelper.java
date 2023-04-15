package ua.nure.demo.books.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class SqlHelper {
    public static String setPageable(String sql, Pageable pageable) {
        String query = sql; //" ORDER BY ";
        if (pageable.getSort().isSorted()) {
            String sort = pageable.getSort().stream()
                    .map(o -> o.getProperty() + " " + o.getDirection().name())
                    .collect(Collectors.joining(", "));
            query += " ORDER BY " + sort;
        }
        if (pageable.isPaged()) {
            query += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        }
        return query;
    }

    public static <T> void setValueOrNull(PreparedStatement ps, int index, T value, int type)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, type);
            return;
        }
        ps.setObject(index, value, type);
//        if (value instanceof Long) {
//            ps.setLong(index, (Long) value);
//            return;
//        }
//        if (value instanceof Integer) {
//            ps.setInt(index, (Integer) value);
//            return;
//        }
//        if (value instanceof Double) {
//            ps.setDouble(index, (Double) value);
//            return;
//        }
//        if (value instanceof BigInteger) {
//            ps.setBigDecimal(index, (BigDecimal) value);
//            return;
//        }
//        if (value instanceof String) {
//            ps.setString(index, (String) value);
//            return;
//        }
//        throw new SQLException("Unsupported data type to set");
    }
}
