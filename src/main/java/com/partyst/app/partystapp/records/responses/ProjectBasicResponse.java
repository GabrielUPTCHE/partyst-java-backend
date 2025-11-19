package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ProjectBasicResponse(
    Integer projectId,
    String name,
    String description,
    String category,
    List<SkillBasicResponse> skills
) {}
