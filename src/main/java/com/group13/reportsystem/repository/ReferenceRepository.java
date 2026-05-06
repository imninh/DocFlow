package com.group13.reportsystem.repository;

import com.group13.reportsystem.model.ReferenceItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReferenceRepository {
    private final JdbcTemplate jdbcTemplate;

    public ReferenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReferenceItem> findByReportId(Integer reportId) {
        return jdbcTemplate.query("""
                SELECT reference_id, report_id, citation_text, url, added_at
                FROM references_list
                WHERE report_id = ?
                ORDER BY reference_id
                """, (rs, rowNum) -> {
            ReferenceItem item = new ReferenceItem();
            item.setReferenceId(rs.getInt("reference_id"));
            item.setReportId(rs.getInt("report_id"));
            item.setCitationText(rs.getString("citation_text"));
            item.setUrl(rs.getString("url"));
            if (rs.getTimestamp("added_at") != null) {
                item.setAddedAt(rs.getTimestamp("added_at").toLocalDateTime());
            }
            return item;
        }, reportId);
    }

    public void insert(Integer reportId, String citationText) {
        jdbcTemplate.update("""
                INSERT INTO references_list (report_id, citation_text, added_at)
                VALUES (?, ?, CURRENT_TIMESTAMP)
                """, reportId, citationText);
    }
}
