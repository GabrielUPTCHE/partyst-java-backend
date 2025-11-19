package com.partyst.app.partystapp.records.requests;

public record CreateTaskRequest(
    Integer projectId,
    String name,
    String state,
    String assignedUserEmail
) {}
