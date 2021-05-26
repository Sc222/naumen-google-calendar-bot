package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.entities.Notification;
import ru.dudes.google_calendar_helper.db.repositories.NotifyRepository;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.services.GoogleService;
import ru.dudes.google_calendar_helper.services.google_entities.CalendarDto;
import ru.dudes.google_calendar_helper.services.google_entities.EventDto;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@BotController
public class CalendarController {

    private final UserRepository userRepository;
    private final NotifyRepository notifyRepository;
    private final GoogleService googleService;

    @Autowired
    public CalendarController(UserRepository userRepository, NotifyRepository notifyRepository, GoogleService googleService) {
        this.userRepository = userRepository;
        this.notifyRepository = notifyRepository;
        this.googleService = googleService;
    }

    @BotRequestMapping(value = "/calendars")
    public SendMessage processListCalendarsCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));

        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));

        String responseText;
        if (user == null) //todo move to helper method
            responseText = "You are not logged in.\nPlease type /login to login or service will not work";
        else {
            List<CalendarDto> calendars = null;
            try {
                calendars = googleService.getCalendars(user.getToken());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //todo  move to helper method
            if (calendars == null)
                responseText = "There was a error getting your calendars.\nTry /login or /refresh_token commands.";
            else {
                responseText = "Your calendars:\n";
                StringJoiner calendarsJoiner = new StringJoiner("\n");
                for (int i = 0; i < calendars.size(); i++) {
                    CalendarDto c = calendars.get(i);
                    String s = String.format("%d. %s\n%s", i + 1, i == 0 ? "Primary" : "", c.toString());
                    calendarsJoiner.add(s);
                }
                responseText += calendarsJoiner.toString();
            }
        }
        response.setText(responseText);
        return response;
    }

    //todo list events by date, name, etc (NOT ONLY BY CALENDAR ID)
    @BotRequestMapping(value = "/events")
    public SendMessage processListEventsByCalendarIdCommand(Update update) {
        var message = update.getMessage();
        var values = message.getText().split(" ");
        var response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));

        if (values.length != 2) {
            response.setText("Wrong command format!\nPlease type /events %calendarID\nCalendar ID's can be listed using /calendars .");
            return response;
        }
        var calendarId = values[1];

        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));

        String responseText;
        if (user == null)
            return ResponseHelper.generateNotLoggedInResponse(String.valueOf(message.getChatId()));
        else {
            List<EventDto> events = googleService.getEvents(user.getToken(), calendarId);

            //todo  move to helper method
            if (events == null)
                responseText = "There was a error getting your calendar events.\nCheck command arguments or try /login or /refresh_token commands.";
            else if (events.size() == 0)
                responseText = "No events with specified parameters";
            else {
                responseText = "Your events:\n";
                StringJoiner calendarsJoiner = new StringJoiner("\n");
                for (int i = 0; i < events.size(); i++) {
                    EventDto c = events.get(i);
                    String s = String.format("%d. %s\n%s", i + 1, i == 0 ? "Primary" : "", c.toString());
                    calendarsJoiner.add(s);
                }
                responseText += calendarsJoiner.toString();
            }
        }
        response.setText(responseText);
        return response;
    }

    @BotRequestMapping(value = "/notify")
    public SendMessage NotifyEvent(Update update) {
        var message = update.getMessage();
        var values = message.getText().split(" ");
        var response = new SendMessage();
        response.setChatId(String.valueOf(update.getMessage().getChatId()));
        if (values.length != 3) {
            response.setText("Wrong command format!\nPlease type /events %calendarID %eventId\nCalendar ID's can be listed using /calendars .");
            return response;
        }
        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));
        var calendarId = values[1];
        var eventId = values[2];
        var event = googleService.getEvent(calendarId, eventId, user.getToken());
        if (event == null) {
            response.setText("There was a error getting your calendar events.\nCheck command arguments or try /login command.");
            return response;
        }

        var notification = new Notification(message.getChatId(), new Date(event.getStartDateTime().getValue()), calendarId, eventId);
        notifyRepository.save(notification);

        response.setText("Success adding notification");
        return response;
    }
}


