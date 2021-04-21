package ru.dudes.google_calendar_helper.telegram.controllers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.BotController;
import ru.dudes.google_calendar_helper.telegram.BotRequestMapping;

@BotController
public class StartController {

    @BotRequestMapping(value = "/start")
    public SendMessage processStartCommand(Update update) {
        Message message = update.getMessage();
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));
        response.setChatId(String.valueOf(update.getMessage().getChatId()));
        response.setText("Google Calendar bot start info");
        return response;
    }
}