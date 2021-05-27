package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.utils.ResponseUtils;

import java.util.List;

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
        //todo use sendPhoto or markdown/html for better img formatting
        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));
        if (user == null)
            return ResponseUtils.generateNotLoggedInResponse(String.valueOf(message.getChatId()));
        else {
            response.setText(
                    "Account information:" +
                            "\nUsername: " + user.getUserName() +
                            "\nEmail: " + user.getEmail() +
                            "\n" + user.getImageUrl()
            );
            var button = ResponseUtils.createKeyboardButton("Logout", "/callback-logout");
            var keyboard = List.of(List.of(button));
            var replyMarkup = new InlineKeyboardMarkup();
            replyMarkup.setKeyboard(keyboard);
            response.setReplyMarkup(replyMarkup);
        }
        return response;
    }
}
