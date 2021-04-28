package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;

@BotController
public class StatusController {

    private final UserRepository userRepository;

    @Autowired
    public StatusController(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @BotRequestMapping(value = "/status")
    public SendMessage processStatusCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));

        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));

        String responseText;
        if(user==null)  //todo move to helper method
            responseText="You are not logged in.\nPlease type /login to login or service will not work";
        else //todo more info about logged in user (message, etc)
            responseText= String.format("Logged in as %s!\nChat: %s", user.getUserName(),user.getChatId());
        response.setText(responseText);
        return response;
    }
}
