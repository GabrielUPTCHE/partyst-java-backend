package com.partyst.app.partystapp.records.requests;

import java.util.Set;

import com.partyst.app.partystapp.entities.Skill;

public record UpdateProjectRequest(Integer projectId, String title, String description, String category, Set<Skill> skills) {

}
