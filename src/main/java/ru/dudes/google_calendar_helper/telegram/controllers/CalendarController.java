package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.services.GoogleService;
import ru.dudes.google_calendar_helper.services.google_entities.CalendarDto;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMethod;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

@BotController
public class CalendarController {

    private final UserRepository userRepository;
    private final GoogleService googleService;

    @Autowired
    public CalendarController(UserRepository userRepository, GoogleService googleService) {
        this.userRepository = userRepository;
        this.googleService = googleService;
    }

    //FIXME: TODOS ARE HERE
    //TODO: !!!calendar navigation
    //TODO: PROPER EVENTS
    //TODO: FAVOURITE
    //TODO: ADD OR REMOVE EVENTS

    public SendMessage processListCalendars(String chatId) {
        var response = new SendMessage();
        response.setChatId(chatId);
        var user = userRepository.findByChatId(chatId);
        if (user == null)
            return ResponseHelper.generateNotLoggedInResponse(chatId);
        else {
            String responseText;
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

            //todo check if text length is less than 4098

            response.setText(responseText);
            return response;
        }
    }

    @BotRequestMapping(value = "/callback-calendars", method = BotRequestMethod.EDIT)
    public SendMessage processListCalendarsButton(Update update) {
        return processListCalendars(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
    }

    @BotRequestMapping(value = "/calendars")
    public SendMessage processListCalendarsCommand(Update update) {
        return processListCalendars(String.valueOf(update.getMessage().getChatId()));
    }
}


