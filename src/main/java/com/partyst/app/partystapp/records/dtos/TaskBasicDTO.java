package com.partyst.app.partystapp.records.dtos;

public record TaskBasicDTO (
    Integer taskId,
    String name,
    String state,
    String userEmail,
    String userName,
    String userLastname
) {}
