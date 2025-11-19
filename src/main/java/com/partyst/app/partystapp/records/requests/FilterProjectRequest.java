package com.partyst.app.partystapp.records.requests;

import java.util.List;

import com.partyst.app.partystapp.entities.Skill;


public record FilterProjectRequest(String title, String category, Integer userId, String type, List<Skill> skills, Boolean active) {

}
