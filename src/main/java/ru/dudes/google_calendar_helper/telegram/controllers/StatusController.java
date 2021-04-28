package ru.dudes.google_calendar_helper.telegram.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;

@BotController
public class StatusController {

    @BotRequestMapping(value = "/status")
    public SendMessage processStatusCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));
        response.setText("A");
        return response;
    }
}
