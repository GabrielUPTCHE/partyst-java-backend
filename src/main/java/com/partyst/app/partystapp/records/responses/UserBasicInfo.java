package com.partyst.app.partystapp.records.responses;

public record UserBasicInfo(
    Long userId,
    String name,
    String lastname,
    String email,
    String nickname
) {}
