package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ArtistFilterResponse(
    boolean success,
    String message,
    ArtistFilterData data
) {}
