package com.partyst.app.partystapp.records.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationResponse(
    @JsonProperty("notificationid") String notificationId, 
    String title, 
    String description, 
    String type, 
    String date, 
    @JsonProperty("wasRead") Boolean wasRead
) {

}
