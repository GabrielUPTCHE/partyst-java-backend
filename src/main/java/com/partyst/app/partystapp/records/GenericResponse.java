package com.partyst.app.partystapp.records;

public record GenericResponse<T>(Integer code, String message, T data) {

}
