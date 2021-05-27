package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMethod;

@BotController
public class LogoutController {

    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public LogoutController(UserRepository userRepository, OAuth2AuthorizedClientService auth2AuthorizedClientService) {
        this.authorizedClientService = auth2AuthorizedClientService;
        this.userRepository = userRepository;
    }

    private SendMessage processLogout(String chatId) {
        var response = new SendMessage();
        response.setChatId(chatId);
        var user = userRepository.findByChatId(chatId);
        if (user == null)
            return ResponseHelper.generateNotLoggedInResponse(chatId);
        else {
            authorizedClientService.removeAuthorizedClient(user.getOAuthRegistrationId(), user.getOAuthName());
            userRepository.deleteById(user.getId());
            response.setText("Logged out successfully.");
        }
        return response;
    }

    // button callback request
    @BotRequestMapping(value = "/callback-logout", method = BotRequestMethod.EDIT)
    public SendMessage processLogoutButton(Update update) {
        return processLogout(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
    }

    //text request
    @BotRequestMapping(value = "/logout")
    public SendMessage processLogoutCommand(Update update) {
        return processLogout(String.valueOf(update.getMessage().getChatId()));
    }
}
