package ru.dudes.google_calendar_helper.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ru.dudes.google_calendar_helper.services.google_entities.CalendarDto;
import ru.dudes.google_calendar_helper.services.google_entities.EventDto;
import ru.dudes.google_calendar_helper.utils.GoogleServiceUtils;
import ru.dudes.google_calendar_helper.utils.TimeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class GoogleService {


    private static final String APPLICATION_NAME = "Calendar-bot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final HttpTransport httpTransport = new NetHttpTransport();
    private final DateTime minDate = new DateTime(new Date());

    //TODO UPDATE CALENDAR AND SOMETHING ELSE HERE

    //todo fix
    public EventDto getEvent(String calendarId, String eventId, String token) {
        var credential = new GoogleCredential().setAccessToken(token);
        var calendar =
                new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        Event event = null;
        try {
            event = calendar.events().get(calendarId, eventId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (event == null)
            return null;

        var eventDto = new EventDto();
        BeanUtils.copyProperties(event, eventDto);
        eventDto.setStartDateTime(event.getStart().getDateTime());
        return eventDto;
    }

    //TODO GET EVENTS WORKS BAD
    public List<EventDto> getEvents(String tokenValue, String calendarId, Date currentDate) throws IOException {
        var credential = new GoogleCredential().setAccessToken(tokenValue);
        var calendar =
                new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        var startDate = TimeUtils.getDayStart(currentDate);
        var endDate = TimeUtils.getDayEnd(currentDate);


        Events events = calendar.events().list(calendarId)
                .setMaxResults(GoogleServiceUtils.MAX_EVENT_RESULTS)
                .setTimeMin(new DateTime(startDate.getTime()))
                .setTimeMax(new DateTime(endDate.getTime()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        var eventDtos = new ArrayList<EventDto>();
        events.getItems().forEach(e -> {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(e, eventDto);
            eventDto.setStartTime(e.getStart().getDateTime().toString());
            eventDto.setEndTime(e.getEnd().getDateTime().toString());
            eventDtos.add(eventDto);
        });
        return eventDtos;
        // f (items.isEmpty()) {
        //    return new ArrayList<>();
        //  else {
        //    System.out.println("Upcoming events");
        //    for (Event event : items) {
        //        DateTime start = event.getStart().getDateTime();
        //        if (start == null) {
        //            start = event.getStart().getDate();
        //        }
        //        System.out.printf("%s (%s)\n", event.getSummary(), start);
        //    }
        //


       /* var eventDtos = new ArrayList<EventDto>();
        events.getItems().forEach(e -> {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(e, eventDto);

            eventDto.setStartTime(e.getStart().getDateTime().toString());
            eventDtos.add(eventDto);
        });
        return eventDtos;*/
    }

    public CalendarDto getCalendarByIndex(String tokenValue, int index) throws IOException {
        return getCalendars(tokenValue).get(index);
    }

    public List<CalendarDto> getCalendars(String tokenValue) throws IOException {
        var credential = new GoogleCredential().setAccessToken(tokenValue);
        var service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
        CalendarList calendarList;
        var calendarDtos = new ArrayList<CalendarDto>();
        String pageToken = null;
        do {
            calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            var items = calendarList.getItems();
            for (CalendarListEntry calendarListEntry : items) {
                var calendarDto = new CalendarDto();
                BeanUtils.copyProperties(calendarListEntry, calendarDto);
                calendarDtos.add(calendarDto);
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
        return calendarDtos;
    }

}
