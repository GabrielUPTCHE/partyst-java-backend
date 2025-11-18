package com.partyst.app.partystapp.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.entities.User;

@Repository
public class TaskJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Task> findTasks(Integer taskId, Integer projectId) {

        String sql = """
            SELECT 
                t.task_id, t.name, t.project_id, t.state, t.user_id, u.name as username, u.lastname,
                u.email, u.cellphone, u.nickname 
            FROM partyst_plastic.tasks t
            JOIN partyst_plastic.users u ON
            t.user_id = u.user_id
            WHERE project_id = ?
        """;

        return jdbcTemplate.query(sql, new Object[]{projectId}, (rs, rowNum) ->
                    Task.builder()
                    .taskId(rs.getInt("task_id"))
                    .name(rs.getString("name"))
                    .state(rs.getString("state"))
                    .assignedUser(
                        User.builder().
                        name(rs.getString("username")).
                        userId(rs.getLong("user_id")).
                        lastname(rs.getString("lastname")).
                        email(rs.getString("email")).
                        cellphone(rs.getString("cellphone")).
                        nickname(rs.getString("nickname")).
                        build()
                    )
                    .build()
               /*  new Task(
                    rs.getInt("task_id"),
                    rs.getString("name")
                    
                ) */
        );
    }
}