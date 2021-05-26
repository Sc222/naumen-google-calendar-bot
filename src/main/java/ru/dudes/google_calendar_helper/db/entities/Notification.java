package ru.dudes.google_calendar_helper.db.entities;

import com.google.api.client.util.DateTime;
import lombok.Getter;
import nonapi.io.github.classgraph.json.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

@Entity
public class Notification {

    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Notification(Long chatId, Date dateTime, String calendarId, String eventId) {
        ChatId = chatId;
        DateTime = dateTime;
        CalendarId = calendarId;
        EventId = eventId;
    }

    @Getter
    private Long ChatId;

    @Getter
    private Date DateTime;

    @Getter
    private String CalendarId;

    @Getter
    private String EventId;

    public Notification() {

    }
}
