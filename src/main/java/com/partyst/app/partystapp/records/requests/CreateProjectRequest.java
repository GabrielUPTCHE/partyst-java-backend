package com.partyst.app.partystapp.records.requests;

import java.util.List;
import java.util.Set;

import com.partyst.app.partystapp.entities.Skill;
import com.partyst.app.partystapp.entities.Task;

public record CreateProjectRequest(String title, String category, Integer userId, Set<Skill> skills, List<Task> tasks) {

}
