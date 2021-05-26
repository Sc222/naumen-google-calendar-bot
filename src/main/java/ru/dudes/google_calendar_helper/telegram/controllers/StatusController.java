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
    public StatusController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BotRequestMapping(value = "/status")
    public SendMessage processStatusCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();
        response.setChatId(String.valueOf(message.getChatId()));
        
        //todo use sendPhoto or markdown/html
        //new SendPhoto().setPhoto("ллл");

        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));
        if (user == null)
            response.setText(
                    "You are not logged in." +
                            "\nPlease type /login to login or service will not work"
            );
        else {
            response.setText(
                    "Account information:" +
                            "\nUsername: " + user.getUserName() +
                            "\nEmail: " + user.getEmail() +
                            "\n" + user.getImageUrl()
            );
        }
        return response;
    }
}
