package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;

@BotController
public class LoginController {

    @Value("${loginURL}")
    private String homepage;

    @BotRequestMapping(value = "/login")
    public SendMessage processLoginCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();

        response.setChatId(String.valueOf(message.getChatId()));

        var loginUrl = String.format("%s?chatId\\=%d", homepage, update.getMessage().getChatId());
        //!!!localhost links don't highlight
        //todo: Please follow the link below to login
        response.setText(loginUrl);

        return response;
    }
}
