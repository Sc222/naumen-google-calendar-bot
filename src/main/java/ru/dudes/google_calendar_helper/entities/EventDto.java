package ru.dudes.google_calendar_helper.entities;

import lombok.Data;

@Data
public class EventDto {

    private String id;
    private String startTime;
    private String endTime;
    private String summary;
    private String description;
    private String location;
}
