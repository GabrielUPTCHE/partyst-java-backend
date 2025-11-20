package com.partyst.app.partystapp.records.responses;

import java.util.Set;

import com.partyst.app.partystapp.entities.Skill;

public record UserByIdResponse(Long userId, String nickname, String cellphone, String email, String lastname, String name, Set<Skill> skills, String biography) {

}
