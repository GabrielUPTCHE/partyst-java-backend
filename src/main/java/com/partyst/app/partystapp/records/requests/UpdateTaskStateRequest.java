package com.partyst.app.partystapp.records.requests;

public record UpdateTaskStateRequest(
    Integer taskId,
    String state
) {}
