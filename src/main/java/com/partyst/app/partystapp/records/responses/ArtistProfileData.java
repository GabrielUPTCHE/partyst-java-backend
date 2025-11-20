package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ArtistProfileData(
    Long userid,
    String name,
    String lastname,
    String nickname,
    String email,
    String biography,
    List<SkillResponse> skills,
    List<ProjectFilterResponse> projects
) {}