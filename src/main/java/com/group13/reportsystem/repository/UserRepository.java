package com.group13.reportsystem.repository;

import com.group13.reportsystem.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setClassCode(rs.getString("class_code"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findByCredentials(String username, String password, String classCode) {
        List<User> users = jdbcTemplate.query("""
                SELECT * FROM users
                WHERE username = ? AND password_hash = ? AND class_code = ?
                """, userRowMapper, username, password, classCode);
        return users.isEmpty() ? null : users.get(0);
    }

    public User findById(Integer userId) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?", userRowMapper, userId);
        return users.isEmpty() ? null : users.get(0);
    }

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY role, full_name", userRowMapper);
    }

    public List<User> findInstructorsByClassCode(String classCode) {
        return jdbcTemplate.query("""
                SELECT * FROM users
                WHERE role = 'instructor' AND class_code = ?
                ORDER BY full_name
                """, userRowMapper, classCode);
    }
}
