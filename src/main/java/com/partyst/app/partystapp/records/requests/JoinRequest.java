package com.partyst.app.partystapp.records.requests;

public record JoinRequest(
    Integer userid,
    String name,
    String lastname,
    String nickname,
    String email
) {}