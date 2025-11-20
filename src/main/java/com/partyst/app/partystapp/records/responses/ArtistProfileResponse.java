package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ArtistProfileResponse(
    boolean success,
    String message,
    ArtistProfileData data
) {}
