package ru.dudes.google_calendar_helper.services.google_entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class EventDto {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String startTime;

    @Getter
    @Setter
    private String endTime;

    @Getter
    @Setter
    private String summary;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String location;
}
