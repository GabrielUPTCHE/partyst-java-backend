package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ArtistData(
    Long userid,
    String nickname,
    String biography,
    List<SkillResponse> skills
) {}
