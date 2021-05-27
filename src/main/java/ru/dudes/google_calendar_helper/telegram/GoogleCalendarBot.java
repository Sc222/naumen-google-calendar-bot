package ru.dudes.google_calendar_helper.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.dudes.google_calendar_helper.telegram.controllers.core.ControllersSingleton;

import javax.annotation.PostConstruct;

@Component
public class GoogleCalendarBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarBot.class);

    @Value("${bot.token}")
    private String token;

    @Value("${bot.username}")
    private String username;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var controller = ControllersSingleton.getController(update);
        var methods = controller.process(update);
        methods.forEach(method -> {
            try {
                if (method instanceof SendMessage) {
                    execute((SendMessage) method);
                }
                if (method instanceof EditMessageText) {
                    execute((EditMessageText) method);
                }
                if (method instanceof SendPhoto) {
                    execute((SendPhoto) method);
                }
                //todo SWITCH ALL METHOD TYPES HERE

                logger.info("Sent message to user");
            } catch (TelegramApiException e) {
                e.printStackTrace();
                logger.error("Failed to send message due to error: {}", e.getMessage());
            }
        });
    }

    public void sendMessageFromController(SendMessage sendMessage) throws TelegramApiException {
        execute(sendMessage);
    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

}