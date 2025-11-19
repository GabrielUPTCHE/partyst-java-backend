package com.partyst.app.partystapp.records.responses;

public record TaskResponse(
    Integer taskId,
    String name,
    String state,
    Integer projectId,
    UserBasicInfo assignedUser
) {}
