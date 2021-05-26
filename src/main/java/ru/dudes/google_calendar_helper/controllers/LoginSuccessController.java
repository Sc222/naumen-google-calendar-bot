package ru.dudes.google_calendar_helper.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dudes.google_calendar_helper.auth2.Auth2InfoHelper;
import ru.dudes.google_calendar_helper.db.entities.User;
import ru.dudes.google_calendar_helper.db.repositories.UserRepository;
import ru.dudes.google_calendar_helper.telegram.GoogleCalendarBot;
import ru.dudes.google_calendar_helper.telegram.controllers.core.BotApiMethodContainer;

import javax.servlet.http.HttpSession;

@RestController
public class LoginSuccessController {

    private static final Logger logger = LoggerFactory.getLogger(BotApiMethodContainer.class);
    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GoogleCalendarBot telegramBot;

    @Autowired
    public LoginSuccessController(UserRepository userRepository, OAuth2AuthorizedClientService authorizedClientService, GoogleCalendarBot telegramBot) {
        this.userRepository = userRepository;
        this.authorizedClientService = authorizedClientService;
        this.telegramBot = telegramBot;
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(HttpSession session, OAuth2AuthenticationToken authentication) {
        var client = Auth2InfoHelper.loadClientInfo(authentication, authorizedClientService);
        var userInfo = Auth2InfoHelper.getUserInfo(client);
        var tgChatId = (String) session.getAttribute("chatId");
        logger.info("Success login for telegram chat: " + tgChatId);
        logger.info(userInfo.toString());
        var newUser = new User(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName(),
                tgChatId,
                client.getAccessToken().getTokenValue(),
                userInfo);
        var dbUser = userRepository.findByChatId(tgChatId);
        if (dbUser != null) { // save dbUser because user to update should have database ID
            dbUser.updateWithValues(newUser.getChatId(), newUser.getToken(), newUser.getUserName());
            userRepository.save(dbUser);
        } else
            userRepository.save(newUser);

        // send telegram message
        var sendMessage = new SendMessage();
        sendMessage.setChatId(tgChatId);
        sendMessage.setText("Successfully logged in as:\n" + userInfo.getName());
        try {
            telegramBot.sendMessageFromController(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("Can't send telegram message after success login");
        }
        return "Successfully logged in with user: " + userInfo.getName();
    }
}