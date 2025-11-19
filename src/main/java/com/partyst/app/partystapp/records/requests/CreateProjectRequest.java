package com.partyst.app.partystapp.records.requests;

import java.util.List;

import com.partyst.app.partystapp.records.dtos.SkillIdDTO;
import com.partyst.app.partystapp.records.dtos.TaskCreateDTO;

public record CreateProjectRequest(
    String title, 
    Integer categoryId, 
    String description, 
    Integer userId, 
    List<SkillIdDTO> skills, 
    List<TaskCreateDTO> tasks
) {

}
