package ru.dudes.google_calendar_helper.telegram.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMethod;

@BotController
public class HelpController {

    private SendMessage processHelp(String chatId) {
        var response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        var responseBuilder = new StringBuilder("Available commands:")
                .append("\n\nAccount:")
                .append("\n/login - login into Google to enable integration")
                .append("\n/logout - logout from Google to disable integration")
                .append("\n/status - show Google login status")
                .append("\n\nGet Data:")
                .append("\n/calendars %page - list available calendars")
                .append("\n/calendar %id - show calendar info")
                .append("\n/events %calendarId %interval - list all calendar events")
                .append("\n\nSetup Notifications:")
                .append("\n/notify %calendarId %eventId - setup event notification");

        // TODO UNNOTIFY
        // .append("\n/unnotify %calendarId %eventId - disable event notification");
        // todo: /help %COMMAND_NAME
        // todo: map commands from ControllerSingleton (just add command description to annotation)
        response.setText(responseBuilder.toString());
        return response;
    }

    @BotRequestMapping(value = "/callback-help", method = BotRequestMethod.EDIT)
    public SendMessage processHelpButton(Update update) {
        return processHelp(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
    }

    @BotRequestMapping(value = "/help")
    public SendMessage processHelpCommand(Update update) {
        return processHelp(String.valueOf(update.getMessage().getChatId()));
    }
}