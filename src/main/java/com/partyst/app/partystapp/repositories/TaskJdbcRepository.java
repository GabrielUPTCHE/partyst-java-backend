package com.partyst.app.partystapp.repositories;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.entities.User;

@Repository
public class TaskJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Set<Task> findTasksByProjectId(Integer projectId) {
        String sql = """
            SELECT 
                t.task_id, t.name, t.project_id, t.state, t.user_id, 
                u.name as username, u.lastname, u.email, u.cellphone, u.nickname 
            FROM partyst_plastic.tasks t
            LEFT JOIN partyst_plastic.users u ON t.user_id = u.user_id
            WHERE t.project_id = ?
        """;

        List<Task> tasks = jdbcTemplate.query(sql, new Object[]{projectId}, (rs, rowNum) -> {
            // Construir el usuario solo si existe (user_id no es null)
            User assignedUser = null;
            Long userId = rs.getObject("user_id", Long.class);
            
            if (userId != null) {
                assignedUser = User.builder()
                    .userId(userId)
                    .name(rs.getString("username"))
                    .lastname(rs.getString("lastname"))
                    .email(rs.getString("email"))
                    .cellphone(rs.getString("cellphone"))
                    .nickname(rs.getString("nickname"))
                    .build();
            }
            
            return Task.builder()
                .taskId(rs.getInt("task_id"))
                .name(rs.getString("name"))
                .state(rs.getString("state"))
                .projectId(rs.getInt("project_id"))
                .assignedUser(assignedUser)
                .build();
        });
        
        return new HashSet<>(tasks);
    }
}