package ru.dudes.google_calendar_helper.telegram.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.services.GoogleService;
import ru.dudes.google_calendar_helper.services.google_entities.EventDto;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMethod;
import ru.dudes.google_calendar_helper.telegram.utils.InputUtils;
import ru.dudes.google_calendar_helper.telegram.utils.ResponseUtils;
import ru.dudes.google_calendar_helper.utils.TimeUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@BotController
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final UserRepository userRepository;
    private final GoogleService googleService;

    @Autowired
    public EventController(UserRepository userRepository, GoogleService googleService) {
        this.userRepository = userRepository;
        this.googleService = googleService;
    }

    public BotApiMethod processListEvents(String chatId, Message messageToModify, String commandText) {
        var messageToModifyText = messageToModify.getText();
        var messageId = messageToModify.getMessageId();

        Date currentDate = null; // TODO GET FROM RESPONSE

        var user = userRepository.findByChatId(chatId);
        if (user == null)
            return ResponseUtils.generateNotLoggedInResponse(chatId);
        String calendarId;
        if (messageId == null) {
            currentDate = new Date(System.currentTimeMillis());
            calendarId = InputUtils.getArgumentsFirstString(commandText);
            logger.info("retrieve calendarId from command: " + calendarId);
        } else {
            try {
                currentDate = TimeUtils.DATE_FORMAT_SHORT.parse(InputUtils.getArgumentsFirstString(commandText));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            var messageToModifyLines = messageToModifyText.split("\n");
            calendarId = messageToModifyLines[0].replaceFirst("Calendar: ", "");
            logger.info("retrieve calendarId from message: " + calendarId);
        }
        if (currentDate == null || calendarId == null)
            return ResponseUtils.createSendMessage(chatId, "Wrong command format!\nPlease type /events %calendarId");
        List<EventDto> events = null;
        try {
            events = googleService.getEvents(user.getToken(), calendarId, currentDate);
            //todo split not only for day but also for events count (what if someone has so much events that string will be longer 4098)
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (events == null)
            return ResponseUtils.createSendMessage(chatId, "There was a error getting your events.\nTry logging out and in again\nType /help for more info");

        //todo calendars to string helper method
        var responseText = "Calendar: " + calendarId + "\n\n";
        if (events.size() > 0) {
            responseText += String.format("Events for: %s/\n\n", TimeUtils.DATE_FORMAT_SHORT.format(currentDate));
            StringJoiner calendarsJoiner = new StringJoiner("\n\n");
            for (int i = 0; i < events.size(); i++) {
                EventDto e = events.get(i);
                String s = String.format("%d. %s", i + 1, e.toString());
                calendarsJoiner.add(s);
            }
            responseText += calendarsJoiner.toString();
        } else {
            responseText += "No events for: " + TimeUtils.DATE_FORMAT_SHORT.format(currentDate);
        }
        var paginationRow = ResponseUtils.createDatePaginationButtonRow("/callback-events", currentDate);
        var entriesRow = ResponseUtils.createEntriesButtonRow(EventDto.toButtonEntries(events));
        var backRow = List.of(ResponseUtils.createKeyboardButton("Back to calendars", "/callback-calendars 0"));
        var keyboardMarkup = ResponseUtils.createKeyboardMarkupFromRows(List.of(paginationRow, entriesRow, backRow));
        if (messageId != null)
            return ResponseUtils.createEditMessage(chatId, messageId, responseText, keyboardMarkup);
        else
            return ResponseUtils.createSendMessage(chatId, responseText, keyboardMarkup);
    }

    @BotRequestMapping(value = "/callback-events", method = BotRequestMethod.EDIT)
    public BotApiMethod processListCalendarsButton(Update update) {
        var query = update.getCallbackQuery();
        return processListEvents(String.valueOf(query.getMessage().getChatId()), query.getMessage(), query.getData());
    }

    @BotRequestMapping(value = "/events")
    public BotApiMethod processListCalendarsCommand(Update update) {
        var message = update.getMessage();
        return processListEvents(String.valueOf(message.getChatId()), null, message.getText());
    }


    /*//todo list events by date, name, etc (NOT ONLY BY CALENDAR ID)
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
    }*/
}


