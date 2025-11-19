package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ProjectRequestsResponse(
    boolean success,
    String message,
    Data data
) {
    public record Data(
        List<RequestInfo> requests
    ) {}
    
    public record RequestInfo(
        Integer userid,
        String name,
        String lastname,
        String nickname,
        String email,
        String message,
        String requestDate
    ) {}
}
