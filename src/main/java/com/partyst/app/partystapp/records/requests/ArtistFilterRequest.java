package com.partyst.app.partystapp.records.requests;
import java.util.List;

public record ArtistFilterRequest(
    String nickname,
    List<SkillRequest> skills
) {}
