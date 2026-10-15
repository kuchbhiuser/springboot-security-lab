package com.example.lab.order;

import com.example.lab.shared.LegacyQueryGateway;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final JdbcTemplate jdbc;
    private final LegacyQueryGateway legacy;
    public OrderService(JdbcTemplate jdbc, LegacyQueryGateway legacy) {
        this.jdbc = jdbc;
        this.legacy = legacy;
    }

    public List<Map<String, Object>> lookup(HttpServletRequest request) {
        String value = request.getParameter("value");
        return jdbc.queryForList("SELECT * FROM orders WHERE reference = '" + value + "'");
    }

    public List<Map<String, Object>> search(HttpServletRequest request) {
        String term = request.getParameter("term");
        String sql = "SELECT id, description FROM orders WHERE description LIKE '%" + term + "%'";
        return jdbc.queryForList(sql);
    }

    public List<Map<String, Object>> sort(HttpServletRequest request) {
        String column = request.getParameter("column");
        return jdbc.queryForList("SELECT * FROM orders ORDER BY " + column);
    }

    public int updateNotes(HttpServletRequest request) {
        String note = request.getParameter("note");
        long id = Long.parseLong(request.getParameter("id"));
        return jdbc.update("UPDATE orders SET notes = '" + note + "' WHERE id = " + id);
    }

    public List<Map<String, Object>> report(HttpServletRequest request) {
        String minimum = request.getParameter("minimum");
        String sql = "SELECT * FROM orders WHERE amount >= " + minimum;
        NamedParameterJdbcTemplate reporting = new NamedParameterJdbcTemplate(jdbc);
        return reporting.queryForList(sql, Map.of());
    }

    public List<Map<String, Object>> safeLookup(HttpServletRequest request) {
        String value = request.getParameter("value");
        return jdbc.queryForList("SELECT * FROM orders WHERE reference = ?", value);
    }

    public List<Map<String, Object>> safeSort(HttpServletRequest request) {
        String column = request.getParameter("column");
        if (!Set.of("id", "description", "amount").contains(column)) {
            throw new IllegalArgumentException("Unsupported sort column");
        }
        return jdbc.queryForList("SELECT * FROM orders ORDER BY " + column);
    }
}
