package com.partyst.app.partystapp.records.requests;

import java.time.LocalDate;

public record RegisterRequest(
    String email,
    String password,
    String name,
    String lastname,
    String celphone,
    LocalDate birthdate
) {

}
