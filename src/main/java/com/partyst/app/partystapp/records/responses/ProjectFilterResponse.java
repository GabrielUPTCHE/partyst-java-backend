package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ProjectFilterResponse(
    Long projectid,
    String title,
    String description,
    String category,
    List<SkillResponse> skills
) {}
