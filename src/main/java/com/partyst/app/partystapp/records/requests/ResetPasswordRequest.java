package com.partyst.app.partystapp.records.requests;

public record ResetPasswordRequest(
    String email,
    String newPassword,
    String confirmationCode
) {}
