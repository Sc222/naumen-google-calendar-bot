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
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ru.dudes.google_calendar_helper.services.google_entities.CalendarDto;
import ru.dudes.google_calendar_helper.services.google_entities.EventDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class GoogleService {

    private static final String APPLICATION_NAME = "Movie-Nights";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport httpTransport = new NetHttpTransport();
    private final DateTime minDate = new DateTime(new Date());

    //TODO UPDATE CALENDAR AND SOMETHING ELSE HERE

    public List<EventDto> getEvents(String tokenValue, String calendarId) {
        var credential = new GoogleCredential().setAccessToken(tokenValue);
        var calendar =
                new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        Events events = null;
        try {
            events = calendar.events().list(calendarId)
                    .setMaxResults(50)
                    .setTimeMin(minDate)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        var eventDtos = new ArrayList<EventDto>();
        events.getItems().forEach(e -> {
            EventDto eventDto = new EventDto();
            BeanUtils.copyProperties(e, eventDto);

            //todo null if not logged in
            eventDto.setStartTime(e.getStart().getDateTime().toString());
            eventDtos.add(eventDto);
        });
        return eventDtos;
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
