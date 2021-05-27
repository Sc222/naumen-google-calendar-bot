package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.utils.ResponseUtils;

import java.util.List;

@BotController
public class LoginController {

    private final UserRepository userRepository;
    @Value("${loginURL}")
    private String homepage;

    @Autowired
    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BotRequestMapping(value = "/login")
    public SendMessage processLoginCommand(Update update) {
        var chatId = String.valueOf(update.getMessage().getChatId());
        var user = userRepository.findByChatId(chatId);
        if (user != null) {
            return ResponseUtils.createSendMessage(chatId, "You are already logged in.\nPlease type /logout and than /login again if you want to login with different account.");
        }
        var response = new SendMessage();
        response.setChatId(chatId);
        var url = homepage + "?chatId=" + chatId;
        response.setText("Your login url:\n" + url);
        
        /* TODO normal buttons + buttonControllers on production build*/
        if (homepage.startsWith("http://localhost")) { //localhost links are not valid links for telegram
            response.setText("Your login url:\n" + url);
        } else {
            response.setText("Tap the button to open browser and login:");
            var button = ResponseUtils.createKeyboardButton("Login", null, url);
            var keyboard = List.of(List.of(button));
            var replyMarkup = new InlineKeyboardMarkup();
            replyMarkup.setKeyboard(keyboard);
            response.setReplyMarkup(replyMarkup);
        }
        return response;
    }
}
