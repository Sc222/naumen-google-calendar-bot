package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.services.GoogleService;
import ru.dudes.google_calendar_helper.services.google_entities.EventDto;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.utils.ResponseUtils;

import java.util.List;
import java.util.StringJoiner;

@BotController
public class EventController {

    private final UserRepository userRepository;
    private final GoogleService googleService;

    @Autowired
    public EventController(UserRepository userRepository, GoogleService googleService) {
        this.userRepository = userRepository;
        this.googleService = googleService;
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
            return ResponseUtils.generateNotLoggedInResponse(String.valueOf(message.getChatId()));
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
}


