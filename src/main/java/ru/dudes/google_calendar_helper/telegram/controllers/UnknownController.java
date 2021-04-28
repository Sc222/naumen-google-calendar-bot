package ru.dudes.google_calendar_helper.telegram.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;

@BotController
public class UnknownController {

    @BotRequestMapping(value = "")
    public SendMessage processUnknownCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));
        response.setChatId(String.valueOf(update.getMessage().getChatId()));
        response.setText("Unknown command! type help to see list of available commands");
        return response;
    }
}