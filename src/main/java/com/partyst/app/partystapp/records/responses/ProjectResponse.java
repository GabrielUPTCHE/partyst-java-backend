package com.partyst.app.partystapp.records.responses;

import java.util.Set;

import com.partyst.app.partystapp.entities.Category;
import com.partyst.app.partystapp.entities.Skill;
import com.partyst.app.partystapp.entities.User;

public record ProjectResponse(
    Integer projectId, 
    String name,
     String description,
     Set<User> users,
     Set<Category> categories,
    Set<Skill> skill
) {

}
