package com.partyst.app.partystapp.records.requests;

import java.util.Set;

import com.partyst.app.partystapp.entities.Skill;

public record EditUserRequest(String name, String lastname, String nickname, String cellphone, String email, Set<Integer> skillIds, String biography) {

}
