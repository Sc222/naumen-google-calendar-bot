package ru.dudes.google_calendar_helper.utils;

import ru.dudes.google_calendar_helper.services.google_entities.CalendarDto;

import java.util.List;
import java.util.stream.Collectors;

public class GoogleServiceUtils {

    public static final int CALENDARS_ON_PAGE = 2;
    public static final Integer MAX_EVENT_RESULTS = 50;

    public static List<CalendarDto> getCalendarsOnPage(List<CalendarDto> calendars, int page, int calendarsOnPage) {
        return calendars.stream().skip(page * calendarsOnPage).limit(calendarsOnPage).collect(Collectors.toList());
    }

    public static <T> int getPagesCount(List<T> calendars, int calendarsOnPage) {
        return (int) Math.ceil((calendars.size() * 1d) / calendarsOnPage);
    }
}
