package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ProjectMembersResponse(
    List<MemberInfo> members
) {
    
    public record MemberInfo(
        Integer userid,
        String name,
        String lastname,
        String nickname,
        String email,
        Boolean isOwner
    ) {}
}
