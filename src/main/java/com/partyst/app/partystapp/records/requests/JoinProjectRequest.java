package com.partyst.app.partystapp.records.requests;

public record JoinProjectRequest(
    Integer projectId,
    Integer userId,
    String message
) {}
