package ru.dudes.google_calendar_helper.telegram.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;

@BotController
public class HelpController {

    @BotRequestMapping(value = "/help")
    public SendMessage processHelpCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));

        var responseBuilder = new StringBuilder("Available commands:")
                .append("\n/login - login into Google to enable bot integration")
                .append("\n/status - shows Google login status")
                //.append("\n/refresh - refresh Google login token")
                //.append("\ngetCalendars");
                //.append("\n/status - google login to enable bot integration")
                ;

        //todo map commands from ControllerSingleton
        response.setText(responseBuilder.toString());

        //todo /help COMMAND_NAME

        return response;
    }
}