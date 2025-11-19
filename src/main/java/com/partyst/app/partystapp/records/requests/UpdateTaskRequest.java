package com.partyst.app.partystapp.records.requests;

public record UpdateTaskRequest(
    Integer taskId,
    String name,
    String state,
    String assignedUserEmail
) {}
