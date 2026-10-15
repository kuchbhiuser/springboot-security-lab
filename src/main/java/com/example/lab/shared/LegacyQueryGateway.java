package com.example.lab.shared;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LegacyQueryGateway {
    private final JdbcTemplate jdbc;
    public LegacyQueryGateway(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Map<String, Object>> fetch(String sql) {
        return jdbc.queryForList(sql);
    }
}
