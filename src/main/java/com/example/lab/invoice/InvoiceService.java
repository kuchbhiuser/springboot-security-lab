package com.example.lab.invoice;

import com.example.lab.shared.LegacyQueryGateway;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    private final JdbcTemplate jdbc;
    private final LegacyQueryGateway legacy;
    public InvoiceService(JdbcTemplate jdbc, LegacyQueryGateway legacy) {
        this.jdbc = jdbc;
        this.legacy = legacy;
    }

    public List<Map<String, Object>> lookup(HttpServletRequest request) {
        String value = request.getParameter("value");
        return jdbc.queryForList("SELECT * FROM invoices WHERE invoice_number = '" + value + "'");
    }

    public List<Map<String, Object>> search(HttpServletRequest request) {
        String term = request.getParameter("term");
        String sql = "SELECT id, description FROM invoices WHERE description LIKE '%" + term + "%'";
        return jdbc.queryForList(sql);
    }

    public List<Map<String, Object>> sort(HttpServletRequest request) {
        String column = request.getParameter("column");
        return jdbc.queryForList("SELECT * FROM invoices ORDER BY " + column);
    }

    public int updateNotes(HttpServletRequest request) {
        String note = request.getParameter("note");
        long id = Long.parseLong(request.getParameter("id"));
        return jdbc.update("UPDATE invoices SET notes = '" + note + "' WHERE id = " + id);
    }

    public List<Map<String, Object>> report(HttpServletRequest request) {
        String minimum = request.getParameter("minimum");
        String sql = "SELECT * FROM invoices WHERE amount >= " + minimum;
        return legacy.fetch(sql);
    }

    public List<Map<String, Object>> safeLookup(HttpServletRequest request) {
        String value = request.getParameter("value");
        return jdbc.queryForList("SELECT * FROM invoices WHERE invoice_number = ?", value);
    }

    public List<Map<String, Object>> safeSort(HttpServletRequest request) {
        String column = request.getParameter("column");
        if (!Set.of("id", "description", "amount").contains(column)) {
            throw new IllegalArgumentException("Unsupported sort column");
        }
        return jdbc.queryForList("SELECT * FROM invoices ORDER BY " + column);
    }
}

