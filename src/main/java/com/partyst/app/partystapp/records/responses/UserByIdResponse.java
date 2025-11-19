package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record UserByIdResponse(
    String name,
    String lastname, 
    String nickname,
    String cellphone,
    String email,
    String password,
    List<SkillResponse> skills,
    String biography
) {}
