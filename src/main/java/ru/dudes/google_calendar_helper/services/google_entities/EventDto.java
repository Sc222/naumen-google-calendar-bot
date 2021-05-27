package ru.dudes.google_calendar_helper.services.google_entities;

import com.google.api.client.util.DateTime;
import lombok.Data;

@Data
public class EventDto {

    private String id;
    private DateTime startDateTime;
    private String startTime;
    private String endTime;
    private String summary;
    private String description;
    private String location;
}
