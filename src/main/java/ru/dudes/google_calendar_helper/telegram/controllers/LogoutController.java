package ru.dudes.google_calendar_helper.telegram.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotController;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotRequestMapping;

@BotController
public class LogoutController {

    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    public LogoutController(UserRepository userRepository, OAuth2AuthorizedClientService auth2AuthorizedClientService) {
        this.authorizedClientService = auth2AuthorizedClientService;
        this.userRepository = userRepository;
    }

    @BotRequestMapping(value = "/logout")
    public SendMessage processLoginCommand(Update update) {
        var message = update.getMessage();
        var response = new SendMessage();
        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));
        if(user != null) {
            authorizedClientService.removeAuthorizedClient(user.getOAuthRegistrationId(), user.getOAuthName());
            userRepository.deleteById(user.getId());
            response.setText("Success logout");
        }
        else
            response.setText("U are not logged in");
        response.setChatId(String.valueOf(message.getChatId()));
        return response;

        //todo oauth2.0 logout callback + remove user from database
        /*
        var user = userRepository.findByChatId(String.valueOf(message.getChatId()));

        String responseText;
        if(user!=null)  //todo move to helper method
            responseText="You are already logged in.\nPlease type /logout and than /login again if you want to login with different account.";
        else {
            var loginUrl = String.format("Login url:\n%s?chatId=%d", homepage, update.getMessage().getChatId());
            //!!!localhost links don't highlight
            responseText = loginUrl;
        }
        response.setText(responseText);
        (/
        return response;*/
    }
}
