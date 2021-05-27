package ru.dudes.google_calendar_helper.services.google_entities;

import com.google.api.client.util.DateTime;
import lombok.Data;
import ru.dudes.google_calendar_helper.telegram.utils.ButtonEntry;
import ru.dudes.google_calendar_helper.utils.TimeUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class EventDto {

    private String id;
    private DateTime startDateTime;
    private String startTime;
    private String endTime;
    private String summary;
    private String description;
    private String location;

    public static List<ButtonEntry> toButtonEntries(List<EventDto> events) {
        // todo finish notify
        return IntStream.range(0, events.size()) //get eventID from message as callbackData size is small
                .mapToObj(i -> new ButtonEntry(String.valueOf(i + 1), "/callback-notify"))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        var result = String.format("Summary: %s", summary);
        if (description != null)
            result += "\nDescription: " + description;
        if (startTime != null)
            result += "\nStart Time: " + TimeUtils.DATE_FORMAT_LONG.format(new Date(new DateTime(startTime).getValue()));
        if (endTime != null)
            result += "\nEnd Time: " + TimeUtils.DATE_FORMAT_LONG.format(new Date(new DateTime(endTime).getValue()));
        return result;
    }
}
