package com.partyst.app.partystapp.auth.controller;

public record LoginRequest(
    String email,
    String password
) {

}
