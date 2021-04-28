package ru.dudes.google_calendar_helper.services.google_entities;

import lombok.Getter;
import lombok.Setter;


public class CalendarDto {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String summary;

    @Getter
    @Setter
    private String description;

    @Override
    public String toString() {
        var result = String.format("  Id: %s\n  Summary: %s", id, summary);
        if (description != null)
            result += "\n  Description: " + description;
        return result;
    }
}
