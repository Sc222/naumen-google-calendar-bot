package ru.dudes.google_calendar_helper.telegram.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.services.GoogleService;
import ru.dudes.google_calendar_helper.services.google_entities.CalendarDto;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMethod;
import ru.dudes.google_calendar_helper.telegram.utils.ButtonEntry;
import ru.dudes.google_calendar_helper.telegram.utils.InputUtils;
import ru.dudes.google_calendar_helper.telegram.utils.ResponseUtils;
import ru.dudes.google_calendar_helper.utils.GoogleServiceUtils;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

@BotController
public class CalendarController {

    private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);
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

    // process with updating message
    public BotApiMethod processListCalendars(String chatId, Integer messageId, String commandText) {
        var user = userRepository.findByChatId(chatId);
        if (user == null)
            return ResponseUtils.generateNotLoggedInResponse(chatId);
        var currentPage = InputUtils.getArgumentsFirstInteger(commandText, 0);
        if (currentPage == null)
            return ResponseUtils.createSendMessage(chatId, "Wrong command format!\nPlease type /calendars %page where %page is integer value");
        List<CalendarDto> calendars = null;
        var pagesCount = 0;
        try {
            var allCalendars = googleService.getCalendars(user.getToken());
            pagesCount = GoogleServiceUtils.getPagesCount(allCalendars, GoogleServiceUtils.CALENDARS_ON_PAGE);
            calendars = GoogleServiceUtils.getCalendarsOnPage(allCalendars, currentPage, GoogleServiceUtils.CALENDARS_ON_PAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (calendars == null)
            return ResponseUtils.createSendMessage(chatId, "There was a error getting your calendars.\nTry logging out and in again\nType /help for more info");
        if (currentPage >= pagesCount)
            return ResponseUtils.createSendMessage(chatId, "Page out of bounds. Try passing smaller value");
        if (currentPage < 0)
            return ResponseUtils.createSendMessage(chatId, "Page parameter must be less or equal 0");

        //todo calendars to string helper method
        String responseText;
        responseText = String.format("Page: %d/%d\n", currentPage + 1, pagesCount);
        responseText += "Calendars:\n";
        StringJoiner calendarsJoiner = new StringJoiner("\n");
        for (int i = 0; i < calendars.size(); i++) {
            CalendarDto c = calendars.get(i);
            String s = String.format("%d. %s\n%s", i + 1, i == 0 ? "Primary" : "Secondary", c.toString());
            calendarsJoiner.add(s);
        }
        responseText += calendarsJoiner.toString();
        var paginationRow = ResponseUtils.createPaginationButtonRow("/callback-calendars", currentPage, pagesCount);
        logger.info("pagesCount: " + pagesCount);
        var entriesRow = ResponseUtils.createEntriesButtonRow(CalendarDto.toButtonEntries(calendars, GoogleServiceUtils.CALENDARS_ON_PAGE * currentPage, currentPage));
        var keyboardMarkup = ResponseUtils.createKeyboardMarkupFromRows(List.of(paginationRow, entriesRow));
        if (messageId != null)
            return ResponseUtils.createEditMessage(chatId, messageId, responseText, keyboardMarkup);
        else
            return ResponseUtils.createSendMessage(chatId, responseText, keyboardMarkup);
    }

    public BotApiMethod processCalendar(String chatId, Integer messageId, String commandText) {
        var user = userRepository.findByChatId(chatId);
        if (user == null)
            return ResponseUtils.generateNotLoggedInResponse(chatId);
        var calendarIndex = InputUtils.getArgumentsFirstInteger(commandText, 0);
        if (calendarIndex == null)
            return ResponseUtils.createSendMessage(chatId, "Wrong command format!\nPlease type /calendar %calendarIndex");

        CalendarDto calendar = null;
        try {
            calendar = googleService.getCalendarByIndex(user.getToken(), calendarIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (calendar == null)
            return ResponseUtils.createSendMessage(chatId, "There was a error getting your calendar.\nTry setting smaller index or try logging out and in again\nType /help for more info");
        var responseText = "Selected Calendar:\n" + String.format("%s\n%s", calendarIndex == 0 ? "Primary" : "Secondary", calendar.toString());
        var buttonsRow = ResponseUtils.createEntriesButtonRow(List.of(
                new ButtonEntry("Events", "/help") //todo /events calendarID
        ));
        if (messageId != null) {
            var backPageIndex = InputUtils.getArgumentsSecondString(commandText);
            if (backPageIndex == null)
                return ResponseUtils.createSendMessage(chatId, "Wrong command format!\nPlease type /calendar %index");

            // back button
            buttonsRow.add(0, ResponseUtils.createKeyboardButton("Back", "/callback-calendars " + backPageIndex));
            var keyboardMarkup = ResponseUtils.createKeyboardMarkupFromRows(List.of(buttonsRow));
            return ResponseUtils.createEditMessage(chatId, messageId, responseText, keyboardMarkup);
        }
        var keyboardMarkup = ResponseUtils.createKeyboardMarkupFromRows(List.of(buttonsRow));
        return ResponseUtils.createSendMessage(chatId, responseText, keyboardMarkup);
    }

    @BotRequestMapping(value = "/callback-calendars", method = BotRequestMethod.EDIT)
    public BotApiMethod processListCalendarsButton(Update update) {
        var query = update.getCallbackQuery();
        return processListCalendars(String.valueOf(query.getMessage().getChatId()), query.getMessage().getMessageId(), query.getData());
    }

    @BotRequestMapping(value = "/calendars")
    public BotApiMethod processListCalendarsCommand(Update update) {
        var message = update.getMessage();
        return processListCalendars(String.valueOf(message.getChatId()), null, message.getText());
    }

    @BotRequestMapping(value = "/callback-calendar", method = BotRequestMethod.EDIT)
    public BotApiMethod processCalendarButton(Update update) {
        var query = update.getCallbackQuery();
        return processCalendar(String.valueOf(query.getMessage().getChatId()), query.getMessage().getMessageId(), query.getData());
    }

    @BotRequestMapping(value = "/calendar")
    public BotApiMethod processCalendarCommand(Update update) {
        var message = update.getMessage();
        return processCalendar(String.valueOf(message.getChatId()), null, message.getText());
    }
}