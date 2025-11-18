package com.partyst.app.partystapp.records.requests;

public record UpdateTaskRequest(Integer projectId, Integer taskId, String name, String state, String email) {

}
