package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
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
    public PartialBotApiMethod<Message> processStatusCommand(Update update) {
        var chatId = String.valueOf(update.getMessage().getChatId());
        var user = userRepository.findByChatId(chatId);
        if (user == null)
            return ResponseUtils.generateNotLoggedInResponse(chatId);
        var response = new SendPhoto();
        response.setChatId(chatId);
        response.setPhoto(new InputFile(user.getImageUrl()));
        response.setCaption(
                "Account information:" +
                        "\nUsername: " + user.getUserName() +
                        "\nEmail: " + user.getEmail()
        );
        var button = ResponseUtils.createKeyboardButton("Logout", "/callback-logout");
        var keyboard = List.of(List.of(button));
        var replyMarkup = new InlineKeyboardMarkup();
        replyMarkup.setKeyboard(keyboard);
        response.setReplyMarkup(replyMarkup);
        return response;
    }
}
