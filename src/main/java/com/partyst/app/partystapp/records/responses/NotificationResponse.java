package com.partyst.app.partystapp.records.responses;


public record NotificationResponse(Integer notificationId, String title, String description, String type, String date, Boolean wasRead) {

}
