package com.example.lab.customer;

import com.example.lab.shared.LegacyQueryGateway;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final JdbcTemplate jdbc;
    private final LegacyQueryGateway legacy;
    public CustomerService(JdbcTemplate jdbc, LegacyQueryGateway legacy) {
        this.jdbc = jdbc;
        this.legacy = legacy;
    }

    public List<Map<String, Object>> lookup(String value) {
        return jdbc.queryForList("SELECT * FROM customers WHERE email = '" + value + "'");
    }

    public List<Map<String, Object>> search(HttpServletRequest request) {
        String term = request.getParameter("term");
        String sql = "SELECT id, display_name FROM customers WHERE display_name LIKE '%" + term + "%'";
        return jdbc.queryForList(sql);
    }

    public List<Map<String, Object>> sort(HttpServletRequest request) {
        String column = request.getParameter("column");
        return jdbc.queryForList("SELECT * FROM customers ORDER BY " + column);
    }

    public int updateNotes(HttpServletRequest request) {
        String note = request.getParameter("note");
        long id = Long.parseLong(request.getParameter("id"));
        return jdbc.update("UPDATE customers SET notes = '" + note + "' WHERE id = " + id);
    }

    public List<Map<String, Object>> report(HttpServletRequest request) {
        String minimum = request.getParameter("minimum");
        String sql = "SELECT * FROM customers WHERE amount >= " + minimum;
        return jdbc.queryForList(sql);
    }

    public List<Map<String, Object>> safeLookup(HttpServletRequest request) {
        String value = request.getParameter("value");
        return jdbc.queryForList("SELECT * FROM customers WHERE email = ?", value);
    }

    public List<Map<String, Object>> safeSort(HttpServletRequest request) {
        String column = request.getParameter("column");
        if (!Set.of("id", "display_name", "amount").contains(column)) {
            throw new IllegalArgumentException("Unsupported sort column");
        }
        return jdbc.queryForList("SELECT * FROM customers ORDER BY " + column);
    }
}
