package com.partyst.app.partystapp.records.responses;

import java.util.List;

public record ProjectListWrapperResponse(
    List<ProjectBasicResponse> projects
) {}
