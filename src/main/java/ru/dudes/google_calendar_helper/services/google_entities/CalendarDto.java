package ru.dudes.google_calendar_helper.services.google_entities;

import lombok.Data;
import ru.dudes.google_calendar_helper.telegram.utils.ButtonEntry;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class CalendarDto {

    private String id;
    private String summary;
    private String description;

    public static List<ButtonEntry> toButtonEntries(List<CalendarDto> calendars, int indexShift, int backPageIndex) {
        return IntStream.range(0, calendars.size()) //calendar id makes value out of tg bounds
                .mapToObj(i -> new ButtonEntry(String.valueOf(i + 1), "/callback-calendar " + (indexShift + i) + " " + backPageIndex))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        var result = String.format("Id: %s\nSummary: %s", id, summary);
        if (description != null)
            result += "\nDescription: " + description;
        return result;
    }
}
