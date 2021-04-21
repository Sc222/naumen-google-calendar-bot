package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.telegram.BotController;
import ru.dudes.google_calendar_helper.telegram.BotRequestMapping;

@BotController
public class LoginController {

    @Value("${loginURL}")
    private String homepage;

    @BotRequestMapping(value = "/login")
    public SendMessage login(Update update) {
        Message message = update.getMessage();
        String[] values = update.getMessage().getText().split(" ");

        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));
        if(values.length!=2)//todo && validate email
            response.setText("Wrong command format!\nplease type '/login %mail'");
        else {
            response.enableMarkdownV2(true);

            String mail = values[1];
            //TODO ADD MAIL TO DATABASE HERE

            //localhost links don't highlight
            response.setText(String.format("Login [here](%s) and then return to bot\nlink: %s\nmail:%s", homepage,homepage,mail));
        }
        return response;
    }
}
