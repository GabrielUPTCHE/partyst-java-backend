package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ProjectMembersResponse(
    boolean success,
    String message,
    Data data
) {
    public record Data(
        List<MemberInfo> members
    ) {}
    
    public record MemberInfo(
        Integer userid,
        String name,
        String lastname,
        String nickname,
        String email,
        Boolean isOwner
    ) {}
}
