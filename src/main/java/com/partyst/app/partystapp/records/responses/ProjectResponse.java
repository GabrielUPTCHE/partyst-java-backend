package com.partyst.app.partystapp.records.responses;

import java.util.Set;

import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.dtos.SkillBasicDTO;
import com.partyst.app.partystapp.records.dtos.TaskBasicDTO;

public record ProjectResponse(
    Integer projectId, 
    String name,
     String description,
     Set<User> users,
     String category,
    Set<SkillBasicDTO> skills,
    Set<TaskBasicDTO> tasks
) {

}
